package org.pickonefish.fabric8;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Fabric8KubernetesJobCreator {

  public void createJob(String url, String ca, String token, String namespace){
    Config config = new ConfigBuilder()
            .withMasterUrl(url)
            .withCaCertData(ca)
            .withOauthToken(token)
            .withNamespace(namespace)
            .build();

    try (KubernetesClient k8s = new DefaultKubernetesClient(config)) {
      k8s.pods().list().getItems().forEach(pod -> {
        log.debug(pod.getMetadata().getName());
        log.debug(pod.getMetadata().getNamespace());
      });
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
