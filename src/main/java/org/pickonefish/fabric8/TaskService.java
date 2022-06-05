package org.pickonefish.fabric8;

import org.springframework.stereotype.Component;

@Component
public class TaskService {

  public void demo(String hellWorld, String instanceId) {
    System.out.println(hellWorld);
    System.out.println(instanceId);
  }

}
