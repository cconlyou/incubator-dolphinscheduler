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

import org.apache.dolphinscheduler.dao.entity.TriggerEvent;
import org.apache.dolphinscheduler.dao.entity.TriggerGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * task instance mapper interface
 */
public interface TriggerEventMapper extends BaseMapper<TriggerEvent> {
  
    Integer countTriggeredGroups(@Param("projectInfo") String projectInfo,
                                 @Param("processDefInfo") String processInfo,
                                 @Param("taskInfo") String taskInfo,
                                 @Param("memberType") int triggerEventType);
    
    /**
     * no act datas
     * @param projectInfo
     * @param processInfo
     * @param taskInfo
     * @param triggerEventType
     * @return
     */
    List<TriggerEvent> queryTriggeredGroups(@Param("projectInfo") String projectInfo,
                                            @Param("processDefInfo") String processInfo,
                                            @Param("taskInfo") String taskInfo,
                                            @Param("memberType") int triggerEventType);
    /**
     * merge into
     * @deprecated MySQL does not support merge
     * @param triggerEvent
     * @return
     */
    int saveOrUpdate(@Param("item") TriggerEvent triggerEvent);
    
    int saveIfNotExist(@Param("item") TriggerEvent triggerEvent);

    int updateIfExist(@Param("item") TriggerEvent triggerEvent);
    /**
     * insert when not exists,ignore when exists
     * @param triggerEvents
     * @return
     * @deprecated
     */
    Integer saveBatchIfNotExist(@Param("items") List<TriggerEvent> triggerEvents);
    
    /**
     * @param separator like Constants.COMMA
     * @return only some key info set
     */
    List<TriggerGroup> pollTriggeredGroup(@Param("separator") String separator);

    public int updateTriggerEventsStatus(@Param("ids") int[] eventIds,@Param("enableFlag") int enableFlag,@Param("remark") String reason);

    public int backupTriggerEvents(@Param("ids") int[] eventIds);

    public int deleteTriggerEvents(@Param("ids") int[] eventIds);

}
