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

/**
 * trigger event
 */
@TableName("td_etl_trigger_group")
public class TriggerGroup {

    /**
     * group id
     */
    @TableId(value="group_id", type=IdType.AUTO)
    private int groupId;
    /**
     * group name
     */
    private String groupName;
    /**
     * group description
     */
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
    @TableField(exist = false)
    private Date triggerTime;

    /**
     * enbale flag
     * 0 : on ; 1:off
     */
    private int enableFlag = 1;

    /**
     * remark
     */
    private String remark;

    /**
     * update user
     */
    private String updateUser;

    /**
     * update time
     * auto generate by database
     */
    private Date updateTime = new Date();

    /**
     * ids separated by Constant.COMMA
     * only for query
     */
    @TableField(exist = false)
    private String eventIds;

    public String getEventIds() {
      return eventIds;
    }

    public void setEventIds(String eventIds) {
      this.eventIds = eventIds;
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

    public int getEnableFlag() {
      return enableFlag;
    }

    public void setEnableFlag(int enableFlag) {
      this.enableFlag = enableFlag;
    }

    public Date getUpdateTime() {
      return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TriggerGroup that = (TriggerGroup) o;

        return groupId == that.groupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId);
    }

    public String getRemark() {
      return remark;
    }

    public void setRemark(String remark) {
      this.remark = remark;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

}
