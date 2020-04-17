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
@TableName("tr_etl_trigger_event")
public class TriggerEvent {

    /**
     * id /event id
     */
    @TableId(value="eventId", type=IdType.AUTO)
    private int eventId;
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
     * triggered project id
     */
    private int triProjectId;
    /**
     * triggered project name
     */
    private String triProjectName;
    /**
     * triggered process definition id
     */
    private int triProcessDefId;
    /**
     * triggered process definition name
     */
    private String triProcessDefName;
    /**
     * trigger time type like day,month,week and so on
     * //TODO complex trigger time like add number and date type
     */
    private String triTimeType;
    /**
     * get a value according to scheduleTime and triTimeType
     * //TODO complex trigger time like add number and date type
     */
    private Date triggerTime;
    /**
     * member id
     * only for query
     */
    @TableField(exist = false)
    private int memberId;
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
    private String taskId;
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
    private Date scheduleTime;

    /**
     * enbale flag
     * 0 : on ; 1:off
     */
    private int enableFlag = 1;

    /**
     * update time
     * auto generate by database
     * only for query
     */
    @TableField(exist = false)
    private String updateTime;

    /**
     * count submit times
     * only for query
     */
    @TableField(exist = false)
    private int submitCount = 1;

    public int getSubmitCount() {
      return submitCount;
    }

    public void setSubmitCount(int submitCount) {
      this.submitCount = submitCount;
    }

    public int getEventId() {
      return eventId;
    }

    public void setEventId(int eventId) {
      this.eventId = eventId;
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

    public int getTriProjectId() {
      return triProjectId;
    }

    public void setTriProjectId(int triProjectId) {
      this.triProjectId = triProjectId;
    }

    public String getTriProjectName() {
      return triProjectName;
    }

    public void setTriProjectName(String triProjectName) {
      this.triProjectName = triProjectName;
    }

    public int getTriProcessDefId() {
      return triProcessDefId;
    }

    public void setTriProcessDefId(int triProcessDefId) {
      this.triProcessDefId = triProcessDefId;
    }

    public String getTriProcessDefName() {
      return triProcessDefName;
    }

    public void setTriProcessDefName(String triProcessDefName) {
      this.triProcessDefName = triProcessDefName;
    }

    public String getTriTimeType() {
      return triTimeType;
    }

    public void setTriTimeType(String triTimeType) {
      this.triTimeType = triTimeType;
    }

    public Date getTriggerTime() {
      return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
      this.triggerTime = triggerTime;
    }

    public int getMemberId() {
      return memberId;
    }

    public void setMemberId(int memberId) {
      this.memberId = memberId;
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

    public String getTaskId() {
      return taskId;
    }

    public void setTaskId(String taskId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TriggerEvent that = (TriggerEvent) o;

        return eventId == that.eventId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

}
