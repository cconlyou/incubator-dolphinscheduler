/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dolphinscheduler.service.trigger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * single Trigger executors instance
 */
public class TriggerExecutors {

  /**
   * logger of TriggerExecutors
   */
  private static final Logger logger = LoggerFactory.getLogger(TriggerExecutors.class);

  /**
   * Task and process instance trigger
   */
  private static TriggerSchedule scheduler;

  /**
   * instance of TriggerExecutors
   */
  private static volatile TriggerExecutors INSTANCE = null;

  private TriggerExecutors() {}

  /**
   * thread safe and performance promote
   * @return instance of Trigger Executors
   */
  public static TriggerExecutors getInstance() {
    if (INSTANCE == null) {
      synchronized (TriggerExecutors.class) {
        // when more than two threads run into the first null check same time, to avoid instanced more than one time, it needs to be checked again.
        if (INSTANCE == null) {
          INSTANCE = new TriggerExecutors();
          //finish QuartzExecutors init
          INSTANCE.init();
        }
      }
    }
    return INSTANCE;
  }


  /**
   * init
   *
   * Returns a client-usable handle to a Scheduler.
   */
  private void init() {
    scheduler = new TriggerSchedule();
  }

  /**
   * Whether the scheduler has been started.
   *
   * @throws SchedulerException scheduler exception
   */
  public void start() {
    if (!scheduler.isAlive()){
      scheduler.start();
      logger.info("Event trigger schedule started" );
    }
  }

  /**
   * stop all scheduled tasks
   *
   * Halts the Scheduler's firing of Triggers,
   * and cleans up all resources associated with the Scheduler.
   *
   * The scheduler cannot be re-started.
   * @throws SchedulerException scheduler exception
   */
  public void shutdown() throws Exception {
    if (scheduler.isAlive()) {
        // don't wait for the task to complete
        scheduler.interrupt();
        logger.info("Event trigger service stopped, and halt all tasks");
    }
  }

}
