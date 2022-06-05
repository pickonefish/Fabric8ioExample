package org.pickonefish.fabric8;

import io.fabric8.kubernetes.api.model.ConfigMapEnvSource;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvFromSource;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

      Job job = k8s.batch().v1().jobs().withName("").get();
      Boolean suspend = job.getSpec().getSuspend();
      if (suspend) {
        List<Container> containers = job.getSpec().getTemplate().getSpec().getContainers();
        Container container = containers.get(0);
        String image = container.getImage();
        List<String> args = container.getArgs();
        for (EnvFromSource envFrom : container.getEnvFrom()) {
          ConfigMapEnvSource configMap = envFrom.getConfigMapRef();
        }
      }

//      ObjectMeta objectMeta = new ObjectMetaBuilder()
//              .withAnnotations(cronJob.getSpec().getJobTemplate().getMetadata().getAnnotations())
//              .withLabels(cronJob.getSpec().getJobTemplate().getMetadata().getLabels())
//              .addNewOwnerReferenceLike(
//                      new OwnerReferenceBuilder()
//                              .withKind("CronJob")
//                              .withName(cronJob.getMetadata().getName())
//                              .withUid(cronJob.getMetadata().getUid())
//                              .build()
//              ).and().build();
//      Job cloneJob = new JobBuilder()
//              .withMetadata(objectMeta)
//              .withSpec(cronJob.getSpec().getJobTemplate().getSpec())
//              .build();
//      k8s.batch().v1().jobs().inNamespace(cronJob.getMetadata().getNamespace()).create(cloneJob);
    }
  }

}
