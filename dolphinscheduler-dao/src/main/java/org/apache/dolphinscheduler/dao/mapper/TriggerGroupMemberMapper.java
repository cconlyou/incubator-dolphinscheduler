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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

import org.apache.dolphinscheduler.dao.entity.TriggerGroupMember;
import org.apache.ibatis.annotations.Param;

/**
 * task event trigger configuration : group member info
 */
public interface TriggerGroupMemberMapper extends BaseMapper<TriggerGroupMember> {

  int countMembers(@Param("projectName") String projectName
                  ,@Param("groupName") String groupName
                  ,@Param("enableFlag") Integer enableFlag);

  List<TriggerGroupMember> queryByGroup(@Param("userId") Integer userId
                                   ,@Param("triProjectName") String triProjectName
                                   ,@Param("groupName") String groupName
                                   ,@Param("enableFlag") Integer enableFlag);

  List<TriggerGroupMember> queryByMember(@Param("userId") Integer userId
                                  ,@Param("groupName") String groupName
                                  ,@Param("projectName") String projectName
                                  ,@Param("processDefName") String processDefName
                                  ,@Param("enableFlag") Integer enableFlag);

  IPage<TriggerGroupMember> queryListPaging(IPage<TriggerGroupMember> page,
                                     @Param("searchVal") String searchVal,
                                     @Param("userId") int userId,
                                     @Param("projectId") int projectId,
                                     @Param("groupName") String groupName,
                                     @Param("enableFlag") Integer enableFlag,
                                     @Param("isAdmin") boolean isAdmin);

  int deleteMembers(@Param("projectName") String projectName
                   ,@Param("groupName") String groupName
                   ,@Param("enableFlag") Integer enableFlag);

}
