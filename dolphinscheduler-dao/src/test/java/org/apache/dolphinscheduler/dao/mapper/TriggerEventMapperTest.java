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
package org.apache.dolphinscheduler.dao.mapper;


import org.apache.dolphinscheduler.common.enums.EventTriggerType;
import org.apache.dolphinscheduler.common.enums.FailureStrategy;
import org.apache.dolphinscheduler.common.enums.ReleaseState;
import org.apache.dolphinscheduler.common.enums.WarningType;
import org.apache.dolphinscheduler.common.utils.DateUtils;
import org.apache.dolphinscheduler.dao.entity.ProcessDefinition;
import org.apache.dolphinscheduler.dao.entity.Project;
import org.apache.dolphinscheduler.dao.entity.Schedule;
import org.apache.dolphinscheduler.dao.entity.TriggerEvent;
import org.apache.dolphinscheduler.dao.entity.TriggerGroup;
import org.apache.dolphinscheduler.dao.entity.User;
import org.joda.time.LocalDate;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TriggerEventMapperTest {

    
    @Autowired
    ScheduleMapper scheduleMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    ProcessDefinitionMapper processDefinitionMapper;
    
    @Autowired
    TriggerEventMapper triggerEventMapper;
    
    private DateFormat dateFmt = new SimpleDateFormat("yyyyMMdd");

    @Test
    public void testCountTriggeredGroups(){
      Integer res = triggerEventMapper.countTriggeredGroups("PL1", "P_CHILD01", "TASK_TST", EventTriggerType.EVENT_TRIGGER_TASK.getCode());
      Assert.assertTrue(res > 0);
    }
    
    @Test
    public void testQueryTriggeredGroups(){
      List<TriggerEvent> list = triggerEventMapper.queryTriggeredGroups("PL1", "P_CHILD01", "TASK_TST", EventTriggerType.EVENT_TRIGGER_TASK.getCode());
      Assert.assertTrue(list.size() > 0);
    }
    
    @Test
    public void testSaveOrUpdate1() throws ParseException{
      List<TriggerEvent> list = triggerEventMapper.queryTriggeredGroups("PL1", "P_CHILD01", "TASK_TST", EventTriggerType.EVENT_TRIGGER_TASK.getCode());
      int insertRows = 0;
      int updateRows = 0;
      for(TriggerEvent event : list){
        event.setTriggerTime(dateFmt.parse(dateFmt.format(new Date())));
        event.setScheduleTime(new Date());
        if(triggerEventMapper.saveIfNotExist(event) > 0){
          insertRows += 1;
        }else{
          updateRows += triggerEventMapper.updateIfExist(event);
        }
      }
      System.out.println(String.format("Total[%s] and insert [%s] and update[%s] trigger events",list.size(),insertRows,updateRows));
      Assert.assertTrue(insertRows + updateRows > 0);
    }

    public void testSaveOrUpdate2() throws ParseException{
      List<TriggerEvent> list = triggerEventMapper.queryTriggeredGroups("PL1", "P_CHILD01", "TASK_TST", EventTriggerType.EVENT_TRIGGER_TASK.getCode());
      int effectRows = 0;
      for(TriggerEvent event : list){
        event.setTriggerTime(dateFmt.parse(dateFmt.format(new Date())));
        event.setScheduleTime(new Date());
        effectRows += triggerEventMapper.saveOrUpdate(event);
      }
      Assert.assertTrue(effectRows > 0);
    }

    @Test
    public void testPollTriggeredGroup(){
      List<TriggerGroup> list = triggerEventMapper.pollTriggeredGroup(",");
      for(TriggerGroup element : list){
        System.out.println(element.getTriProjectName() + "_" + element.getTriProcessDefName() + "_" + dateFmt.format(element.getTriggerTime()));
      }
      Assert.assertTrue(list.size() > 0);
    }
    
    @Test
    public void testUpdateTriggerEventsStatus(){
      Assert.assertTrue(triggerEventMapper.updateTriggerEventsStatus(new int[]{4},0,"test") > 0 );
    }
 
    @Test
    public void testBackupTriggerEvents(){
      Assert.assertTrue(triggerEventMapper.backupTriggerEvents(new int[]{4}) > 0 );
    }

    @Test
    public void testDeleteTriggerEvents(){
      Assert.assertTrue(triggerEventMapper.deleteTriggerEvents(new int[]{4}) > 0 );
    }

}