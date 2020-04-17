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
package org.apache.dolphinscheduler.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.utils.PageInfo;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.EventTriggerType;
import org.apache.dolphinscheduler.dao.entity.*;
import org.apache.dolphinscheduler.dao.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * event trigger service
 */
@Service
public class EventTriggerService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(EventTriggerService.class);

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TriggerGroupMapper triggerGroupMapper;

    @Autowired
    private TriggerGroupMemberMapper triggerGroupMemberMapper;

    @Autowired
    private ProjectService projectService;

    /**
     * query Trigger group instance
     * @param groupId
     * @param groupName
     * @@param enableFlag
     * @return
     */
    public TriggerGroup queryOne(Integer groupId, String groupName ,Integer enableFlag){
        TriggerGroup group = null;
        //get instance from id first
        if(groupId != null){
            group = triggerGroupMapper.selectById(groupId);
            if(group == null){
                return null;
            }
        }
        //check name if exists, or get instance from name
        if(StringUtils.isNotBlank(groupName)){
            if(group != null){
                if(!groupName.equals(group.getGroupName())){
                    return null;
                }
            }else{
                group = triggerGroupMapper.queryByName(groupName);
            }
        }
        //check enable flag if exists
        if(group != null && enableFlag != null){
            return group.getEnableFlag() == enableFlag ? group : null;
        }
        return group;
    }

    public Map<String, Object> listEventTriggerGroup(User loginUser, String projectName, Integer processDefId, Integer enableFlag){
        HashMap<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByName(projectName);

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        List<TriggerGroup> resultList = triggerGroupMapper.queryAll(loginUser.getId(), project.getId(), processDefId, enableFlag);
        result.put(Constants.DATA_LIST, resultList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createEventTriggerGroup(User loginUser
                                                    ,String projectName
                                                    ,String groupName
                                                    ,String groupDesc
                                                    ,int triProcessDefId
                                                    ,String triProcessDefName
                                                    ,String triTimeType
                                                    ,String remark
                                                    ,int enableFlag
                                                    ){
        HashMap<String, Object> result = new HashMap<>(5);
        // check project auth
        Project project = projectMapper.queryByName(projectName);
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        // check group name whether exists
        TriggerGroup inst = this.queryOne(null, groupName, null);
        if(inst != null){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Group name has exists");
            return result;
        }
        /**
         * TODO
         * 1. check process whether exists
         * 2. check process id and name whether matches
         */
        inst = new TriggerGroup();
        inst.setGroupName(groupName);
        inst.setGroupDesc(groupDesc);
        inst.setTriProjectId(project.getId());
        inst.setTriProjectName(project.getName());
        inst.setTriProcessDefId(triProcessDefId);
        inst.setTriProcessDefName(triProcessDefName);
        inst.setTriTimeType(triTimeType);
        inst.setRemark(remark);
        inst.setEnableFlag(enableFlag);
        inst.setUpdateUser(loginUser.getUserName());
        triggerGroupMapper.insert(inst);
        putMsg(result, Status.SUCCESS);
        result.put("groupId", inst.getGroupId());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateEventTriggerGroup(User loginUser
                                                    ,int groupId
                                                    ,String groupName
                                                    ,String groupDesc
                                                    ,Integer triProcessDefId
                                                    ,String triProcessDefName
                                                    ,String triTimeType
                                                    ,String remark
                                                    ,Integer enableFlag
                                                    ){
        HashMap<String, Object> result = new HashMap<>(5);
        // check group exists
        TriggerGroup group = triggerGroupMapper.selectById(groupId);
        if(group == null){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Trigger group does not exists");
            return result;
        }
        // check project auth
        Project project = projectMapper.queryByName(group.getTriProjectName());
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, group.getTriProjectName());
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        // check whether same with member
        List<TriggerGroupMember> sameMbrs = triggerGroupMemberMapper.queryByMember(null, group.getGroupName(), project.getName(), triProcessDefName, null);
        if(sameMbrs != null && sameMbrs.size() > 0){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Process can not be same with member");
            return result;
        }
        /**
         * TODO
         * 1. check process whether exists
         * 2. check process id and name whether matches
         */
        if(StringUtils.isNotBlank(groupName)){
            group.setGroupName(groupName);
        }
        if(StringUtils.isNotBlank(groupDesc)){
            if(groupDesc.length() > 300){
                group.setGroupDesc(groupDesc.substring(0,300));
            }else{
                group.setGroupDesc(groupDesc);
            }
        }
        if(triProcessDefId != null){
            group.setTriProcessDefId(triProcessDefId);
        }
        if(StringUtils.isNotBlank(triProcessDefName)){
            group.setTriProcessDefName(triProcessDefName);
        }
        if(StringUtils.isNotBlank(triTimeType)){
            group.setTriTimeType(triTimeType);
        }
        if(StringUtils.isNotBlank(remark)){
            group.setRemark(remark);
        }
        if(enableFlag != null){
            group.setEnableFlag(enableFlag);
        }
        group.setUpdateUser(loginUser.getUserName());
        group.setUpdateTime(new Date());
        int update = triggerGroupMapper.updateById(group);
        if (update > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * check whether has members before delete
     * delete group by id
     *
     * @param loginUser login user
     * @param projectName project name
     * @param groupId group id
     * @return delete result code
     */
    public Map<String, Object> deleteEventTriggerGroup(User loginUser,String projectName, Integer groupId) {
        Map<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByName(projectName);

        //check project permission
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status) checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }

        //check group whether exists
        TriggerGroup group = triggerGroupMapper.selectById(groupId);
        if ( group == null) {
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Trigger group does not exists");
            return result;
        }

        //check group whether has members
        int memberNums = triggerGroupMemberMapper.countMembers(projectName, group.getGroupName(), null);

        if(memberNums > 0){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Please delete its members first !");
            return result;
        }
        int delete = triggerGroupMapper.deleteById(groupId);
        if (delete > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.DELETE_EVENT_TRIGGER_GROUP_ERROR);
        }
        return result;
    }

    public Map<String, Object> pageListEventTriggerGroup(User loginUser
            ,String projectName
            ,String searchVal
            ,int pageNo
            ,int pageSize
            ,int userId
            ){
        HashMap<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByName(projectName);

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        Page<TriggerGroup> page = new Page(pageNo, pageSize);
        IPage<TriggerGroup> groupIPage = triggerGroupMapper.queryListPaging(
                page, searchVal, userId, project.getId(), null, isAdmin(loginUser));
        PageInfo pageInfo = new PageInfo<TriggerGroup>(pageNo, pageSize);
        pageInfo.setTotalCount((int)groupIPage.getTotal());
        pageInfo.setLists(groupIPage.getRecords());
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> listEventTriggerGroupMember(User loginUser
            ,String projectName
            ,String groupName){
        HashMap<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByName(projectName);

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        List<TriggerGroupMember> resultList = triggerGroupMemberMapper.queryByGroup(loginUser.getId(), project.getName(), groupName, null);
        result.put(Constants.DATA_LIST, resultList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> pageListEventTriggerGroupMember(User loginUser
            ,String projectName
            ,String groupName
            ,String searchVal
            ,int pageNo
            ,int pageSize
            ,int userId
            ){
        HashMap<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByName(projectName);

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        Page<TriggerGroupMember> page = new Page(pageNo, pageSize);
        IPage<TriggerGroupMember> groupIPage = triggerGroupMemberMapper.queryListPaging(
                page, searchVal, userId, project.getId(), groupName, null, isAdmin(loginUser));
        PageInfo pageInfo = new PageInfo<TriggerGroupMember>(pageNo, pageSize);
        pageInfo.setTotalCount((int)groupIPage.getTotal());
        pageInfo.setLists(groupIPage.getRecords());
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteEventTriggerMembers(User loginUser
                                                    ,Integer groupId
                                                    ,String groupName
                                                    ,int oldMbrsNum
                                                    ){
        HashMap<String, Object> result = new HashMap<>(5);
        //check group whether exists
        TriggerGroup group = triggerGroupMapper.queryByName(groupName);
        if ( group == null) {
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Trigger group does not exists");
            return result;
        //check group id and name whether matches
        } else if (groupId != null && groupId != group.getGroupId()){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Group's id and name is not matched");
            return result;
        } else {
            //do nothing to keep whole 'if' structure
        }
        // check project auth
        Project project = projectMapper.queryByName(group.getTriProjectName());
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, group.getTriProjectName());
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        //check group members number
        int memberNums = triggerGroupMemberMapper.countMembers(group.getTriProjectName(), group.getGroupName(), null);
        if (oldMbrsNum != memberNums) {
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Group members may has been changed !");
            return result;
        }
        int deleteNums = triggerGroupMemberMapper.deleteMembers(group.getTriProjectName(), group.getGroupName(), null);
        if(deleteNums == memberNums){
            result.put(Constants.STATUS,Status.SUCCESS);
            result.put(Constants.MSG,"Delete successlly.");
        }else{
            result.put(Constants.STATUS,Status.SUCCESS);
            result.put(Constants.MSG,"Delete finished, but may be not as expected !");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveEventTriggerMembers(User loginUser
                                                    ,Integer groupId
                                                    ,String groupName
                                                    ,List<TriggerGroupMember> groupMembers
                                                    ){
        HashMap<String, Object> result = new HashMap<>(5);
        //check group whether exists
        TriggerGroup group = triggerGroupMapper.queryByName(groupName);
        if ( group == null) {
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Trigger group does not exists");
            return result;
        //check group id and name whether matches
        } else if (groupId != null && groupId != group.getGroupId()){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Group id and name is not matched");
            return result;
        } else {
            //do nothing to keep whole 'if' structure
        }
        // check project auth
        Project project = projectMapper.queryByName(group.getTriProjectName());
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, group.getTriProjectName());
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }
        for(int i=0;i<groupMembers.size();i++){
            TriggerGroupMember member = groupMembers.get(i);
            if(!"ALL".equals(member.getTaskId()) && StringUtils.isNotBlank(member.getTaskName())){
                member.setMemberType(EventTriggerType.EVENT_TRIGGER_TASK);
            }else{
                member.setMemberType(EventTriggerType.EVENT_TRIGGER_PROCESS);
            }
            member.setGroupId(group.getGroupId());
            member.setGroupName(group.getGroupName());
            //TODO : batch save
            triggerGroupMemberMapper.insert(member);
        }
        result.put(Constants.STATUS,Status.SUCCESS);
        result.put(Constants.MSG,"Save trigger group members successfully.");
        return result;
    }

    public Map<String, Object> checkMembers(List<TriggerGroupMember> members){
        Map<String, Object> result = null;
        for(int i=0;i<members.size();i++){
            TriggerGroupMember member = members.get(i);
            result = checkMember(member);
            if(!result.get(Constants.STATUS).equals(Status.SUCCESS)){
                result.put(Constants.MSG,String.format("Member[%s] : %s"
                                                      ,member.getOrderInGroup()
                                                      ,result.get(Constants.MSG)));
                return result;
            }
        }
        result.put(Constants.STATUS,Status.SUCCESS);
        result.put(Constants.MSG,"Check for members is ok");
        return result;
    }

    public Map<String, Object> checkMember(TriggerGroupMember member){
        HashMap<String, Object> result = new HashMap<>(5);
        if(member == null){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Member is null");
            return result;
        }
        if(member.getProjectId() == 0 || StringUtils.isBlank(member.getProjectName())){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Project id or name is necessary for member");
            return result;
        }
        if(member.getProcessDefId() == 0 || StringUtils.isBlank(member.getProcessDefName())){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Process id or name is necessary for member");
            return result;
        }
        if("ALL".equals(member.getTaskId()) && StringUtils.isNotBlank(member.getTaskName())){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Task id is necessary when name exists !");
            return result;
        }
        if(!"ALL".equals(member.getTaskId()) && StringUtils.isBlank(member.getTaskName())){
            result.put(Constants.STATUS,Status.FAILED);
            result.put(Constants.MSG,"Task name is necessary when id exists !");
            return result;
        }
        result.put(Constants.STATUS,Status.SUCCESS);
        result.put(Constants.MSG,"Check for member is ok");
        return result;
    }
}

