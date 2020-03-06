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


import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.CommandType;
import org.apache.dolphinscheduler.common.enums.ReleaseState;
import org.apache.dolphinscheduler.common.utils.DateUtils;
import org.apache.dolphinscheduler.common.utils.StringUtils;
import org.apache.dolphinscheduler.dao.entity.Command;
import org.apache.dolphinscheduler.dao.entity.ProcessDefinition;
import org.apache.dolphinscheduler.dao.entity.Schedule;
import org.apache.dolphinscheduler.dao.entity.TriggerGroup;
import org.apache.dolphinscheduler.service.process.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * trigger schedule
 */
public class TriggerSchedule extends Thread{

    /**
     * logger of ProcessScheduleJob
     */
    private static final Logger logger = LoggerFactory.getLogger(TriggerSchedule.class);

    /**
     * process service
     */
    private static ProcessService processService;

    /**
     * init
     * @param processService process dao
     */
    public static void init(ProcessService processService) {
        TriggerSchedule.processService = processService;
    }

    @Override
    public void run() {
        while(true){
          try {
            logger.info("EventTrigger start scan ...");
            Thread.sleep(Constants.EVENT_TRIGGER_SLEEP_SECONDS * 1000);
          } catch (InterruptedException e) {
            logger.error("EventTrigger stop :{}",e.getMessage(),e);
            break;
          }
        }
    }

    public void scanTriggeredGroups(){
      List<TriggerGroup> triggedGroup = null;
      try{
        triggedGroup = processService.pollTriggeredGroup();
      }catch(Exception e){
        logger.warn("Poll triggered event failed",e);
        return;
      }
      if(triggedGroup != null && triggedGroup.size() > 0){
        for(int i=0;i<triggedGroup.size();i++){
          TriggerGroup element = triggedGroup.get(i);
          try{
            dealTriggeredGroup(element);
          }catch(Exception e){
            logger.error("Trigger group[{}] failed",element.getGroupName(),e);
          }
        }
      }
    }

    public void dealTriggeredGroup(TriggerGroup triggeredGroup){
      Date scheduledFireTime = triggeredGroup.getTriggerTime();
      Date fireTime = new Date();
      String projectName = triggeredGroup.getTriProjectName();
      String processDefName = triggeredGroup.getTriProcessDefName();
      String[] eventIdsStrArr = triggeredGroup.getEventIds().split(Constants.COMMA,-1);
      int[] eventIds = StringUtils.strArr2intArr(eventIdsStrArr);
      eventIds = new int[eventIdsStrArr.length];
      logger.info("scheduled fire time :{}, fire time :{}, project name :{}, process name :{}"
                 , scheduledFireTime, fireTime, projectName, processDefName); 
      // query schedule
      Schedule schedule = processService.findSchedule(triggeredGroup.getGroupId());
      if (schedule == null) {
          logger.warn("Group[{}]'s schedule does not exist in schedule config, disable these events[{}]"
              , triggeredGroup.getGroupName()
              , triggeredGroup.getEventIds()
              );
          processService.disableTriggerEvents(eventIds,"no schedule configed");
          return;
      }
      ProcessDefinition processDefinition = processService.findProcessDefineById(triggeredGroup.getTriProcessDefId());
      if(processDefinition == null){
        logger.warn("Process[{}_{}] does not exist !", projectName, processDefName);
        processService.disableTriggerEvents(eventIds,"process doesn't exist");
        return;
      }
      // release state : online/offline
      ReleaseState releaseState = processDefinition.getReleaseState();
      if (releaseState == ReleaseState.OFFLINE) {
        logger.warn("Process[{}_{}] is offline !", projectName, processDefName);
        processService.disableTriggerEvents(eventIds,"process is offline");
        return;
      }

      Command command = new Command();
      command.setCommandType(CommandType.SCHEDULER);
      command.setExecutorId(schedule.getUserId());
      command.setFailureStrategy(schedule.getFailureStrategy());
      command.setProcessDefinitionId(triggeredGroup.getTriProcessDefId());
      command.setScheduleTime(scheduledFireTime);
      command.setStartTime(fireTime);
      command.setWarningGroupId(schedule.getWarningGroupId());
      command.setWorkerGroupId(schedule.getWorkerGroupId());
      command.setWarningType(schedule.getWarningType());
      command.setProcessInstancePriority(schedule.getProcessInstancePriority());

      processService.createCommand(command);
      logger.info("Process[{}_{}_{}] is submit successfully !"
          , projectName
          , processDefName
          , DateUtils.format(scheduledFireTime,"yyyyMMddHHmmss"));
      processService.disableTriggerEvents(eventIds,"trigger successfully");
      processService.safeDeleteTriggerEvents(eventIds);
    }
}
