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
package org.apache.dolphinscheduler.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.util.Objects;

import org.apache.dolphinscheduler.common.enums.EventTriggerType;

/**
 * trigger event
 */
@TableName("td_etl_trigger_group_member")
public class TriggerGroupMember {

    /**
     * member id
     */
    @TableId(value="eventId", type=IdType.AUTO)
    private int memberId;
    /**
     * group id
     */
    private int groupId;
    /**
     * group name
     */
    private String groupName;
    /**
     * group description
     * only for query
     */
    @TableField(exist = false)
    private String groupDesc;
    /**
     * project id
     */
    private int projectId;
    /**
     * project name
     */
    private String projectName;
    /**
     * process definition id
     */
    private int processDefId;
    /**
     * process definition name
     */
    private String processDefName;
    /**
     * task id
     */
    private int taskId;
    /**
     * task name
     */
    private String taskName;
    
    /**
     * trigger event type / member type
     */
    private EventTriggerType memberType;

    /**
     * schedule time of member
     */
    @TableField(exist = false)
    private Date scheduleTime;

    /**
     * enbale flag
     * 0 : on ; 1:off
     * only for query
     */
    @TableField(exist = false)
    private int enableFlag = 1;

    /**
     * update time
     * auto generate by database
     * only for query
     */
    @TableField(exist = false)
    private String updateTime;
    
    /**
     * remark
     */
    private String remark;

    public int getMemberId() {
      return memberId;
    }

    public void setMemberId(int memberId) {
      this.memberId = memberId;
    }

    public int getGroupId() {
      return groupId;
    }

    public void setGroupId(int groupId) {
      this.groupId = groupId;
    }

    public String getGroupName() {
      return groupName;
    }

    public void setGroupName(String groupName) {
      this.groupName = groupName;
    }

    public String getGroupDesc() {
      return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
      this.groupDesc = groupDesc;
    }

    public int getProjectId() {
      return projectId;
    }

    public void setProjectId(int projectId) {
      this.projectId = projectId;
    }

    public String getProjectName() {
      return projectName;
    }

    public void setProjectName(String projectName) {
      this.projectName = projectName;
    }

    public int getProcessDefId() {
      return processDefId;
    }

    public void setProcessDefId(int processDefId) {
      this.processDefId = processDefId;
    }

    public String getProcessDefName() {
      return processDefName;
    }

    public void setProcessDefName(String processDefName) {
      this.processDefName = processDefName;
    }

    public int getTaskId() {
      return taskId;
    }

    public void setTaskId(int taskId) {
      this.taskId = taskId;
    }

    public String getTaskName() {
      return taskName;
    }

    public void setTaskName(String taskName) {
      this.taskName = taskName;
    }

    public EventTriggerType getMemberType() {
      return memberType;
    }

    public void setMemberType(EventTriggerType memberType) {
      this.memberType = memberType;
    }

    public Date getScheduleTime() {
      return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
      this.scheduleTime = scheduleTime;
    }

    public int getEnableFlag() {
      return enableFlag;
    }

    public void setEnableFlag(int enableFlag) {
      this.enableFlag = enableFlag;
    }

    public String getUpdateTime() {
      return updateTime;
    }

    public void setUpdateTime(String updateTime) {
      this.updateTime = updateTime;
    }

    public String getRemark() {
      return remark;
    }

    public void setRemark(String remark) {
      this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TriggerGroupMember that = (TriggerGroupMember) o;

        return memberId == that.memberId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

}
