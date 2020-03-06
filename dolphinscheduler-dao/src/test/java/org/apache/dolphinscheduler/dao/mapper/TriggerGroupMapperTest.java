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


import org.apache.dolphinscheduler.dao.entity.ProcessDefinition;
import org.apache.dolphinscheduler.dao.entity.Project;
import org.apache.dolphinscheduler.dao.entity.TriggerGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TriggerGroupMapperTest {

    
    @Autowired
    TriggerGroupMapper triggerGroupMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    ProcessDefinitionMapper processDefMapper;

    /**
     * insert
     * @return TriggerGroup
     */
    private TriggerGroup insertOne(){
//      Project project = new Project();
//      project.setName("PL1");
//      project.setDescription("Junit test");
//      projectMapper.insert(project);
      Project project = projectMapper.queryByName("PL1");
      
      Assert.assertNotNull(project);
      //insertOne
      TriggerGroup groupInst = new TriggerGroup();
      groupInst.setGroupName("CP1_P_TST.01");
      groupInst.setGroupDesc("Junit test");
      groupInst.setTriProjectId(project.getId());
      groupInst.setTriProjectName(project.getName());
      ProcessDefinition process = processDefMapper.queryByDefineName(project.getId(),"P_TST");
      groupInst.setTriProcessDefId(process.getId());
      groupInst.setTriProcessDefName(process.getName());
      groupInst.setTriTimeType("DD");
      return groupInst;
    }

    @Test
    public void testInsert(){
      triggerGroupMapper.insert(insertOne());
    }

}