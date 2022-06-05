package org.pickonefish.fabric8;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Fabric8Application implements ApplicationRunner {

  private static final String TASK_XXX = "xxx";

  @Autowired
  private TaskService taskService;

  public static void main(String[] args) {
    SpringApplication.run(Fabric8Application.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    String instanceId = args.getOptionValues("instanceId").get(0);
    String task = args.getOptionValues("task").get(0);

    switch (task) {
      case TASK_XXX:
        taskService.demo(task, instanceId);
        break;
    }
  }
}
