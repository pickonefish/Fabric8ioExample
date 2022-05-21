package org.pickonefish.fabric8;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Fabric8Application {

  public static void main(String[] args) {
    SpringApplication.run(Fabric8Application.class, args);

//    try (KubernetesClient k8s = new DefaultKubernetesClient()) {
//      k8s.getConfiguration().getContexts()
//              .stream()
//              .map(NamedContext::getName)
//              .forEach(log::info);
//    }
    try (KubernetesClient k8s = new DefaultKubernetesClient(Config.autoConfigure("stg-ec@aks-dev"))) {
      CronJob cronJob = k8s.batch().v1().cronjobs().withName("").get();

      ObjectMeta objectMeta = new ObjectMetaBuilder()
              .withAnnotations(cronJob.getSpec().getJobTemplate().getMetadata().getAnnotations())
              .withLabels(cronJob.getSpec().getJobTemplate().getMetadata().getLabels())
              .addNewOwnerReferenceLike(
                      new OwnerReferenceBuilder()
                              .withKind("CronJob")
                              .withName(cronJob.getMetadata().getName())
                              .withUid(cronJob.getMetadata().getUid())
                              .build()
              ).and().build();
      Job job = new JobBuilder()
              .withMetadata(objectMeta)
              .withSpec(cronJob.getSpec().getJobTemplate().getSpec())
              .build();
      k8s.batch().v1().jobs().inNamespace(cronJob.getMetadata().getNamespace()).create(job);
    }

  }
}
