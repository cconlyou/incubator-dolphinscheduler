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
package org.apache.dolphinscheduler.api.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.service.EventTriggerService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.utils.ParameterUtils;
import org.apache.dolphinscheduler.dao.entity.TriggerGroup;
import org.apache.dolphinscheduler.dao.entity.TriggerGroupMember;
import org.apache.dolphinscheduler.dao.entity.User;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

import static org.apache.dolphinscheduler.api.enums.Status.CREATE_PROJECT_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.DELETE_PROJECT_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.UPDATE_PROJECT_ERROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * event trigger controller
 */
@Api(tags = "EVENT_TRIGGER_TAG", position = 11)
@RestController
@RequestMapping("projects/{projectName}/event-trigger")
public class EventTriggerController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(EventTriggerController.class);

    @Autowired
    private EventTriggerService eventTriggerService;

    /**
     * query event trigger group list
     *
     * @param loginUser login user
     * @param projectName project name
     * @return event trigger group list
     */
    @ApiOperation(value = "listEventTriggerGroup", notes= "LIST_EVENT_TRIGGER_GROUP")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processDefId", value = "PROCESS_DEF_ID", required = false, dataType = "Integer", example = "100"),
        @ApiImplicitParam(name = "enableFlag", value = "ENABLE_FLAG", required = false, dataType = "Integer", example = "1")
    })
    @GetMapping(value="/group/list")
    @ResponseStatus(HttpStatus.OK)
    public Result listEventTriggerGroup(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser
                                       ,@ApiParam(name = "projectName", value = "PROJECT_NAME",required = true) @PathVariable String projectName
                                       ,@RequestParam(value = "processDefId", required = false) Integer processDefId
                                       ,@RequestParam(value = "enableFlag"  , required = false) Integer enableFlag
    ){
        try{
            logger.info("query event trigger group list, login user:{}, project name:{}, processDefId:{}, enableFlag:{}",
                    loginUser.getUserName(), projectName, processDefId ,enableFlag);
            Map<String, Object> result = eventTriggerService.listEventTriggerGroup(loginUser, projectName, processDefId ,enableFlag);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(Status.LIST_EVENT_TRIGGER_GROUP.getMsg(),e);
            return error(Status.LIST_EVENT_TRIGGER_GROUP.getCode(), Status.LIST_EVENT_TRIGGER_GROUP.getMsg());
        }
    }

    /**
     * list event trigger group by page
     * @param loginUser login user
     * @param projectName project name
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @param userId user id
     * @return event trigger group page
     */
    @ApiOperation(value = "pageListEventTriggerGroup", notes= "PAGE_LIST_EVENT_TRIGGER_GROUP")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "searchVal", value = "SEARCH_VAL", required = false, type = "String"),
            @ApiImplicitParam(name = "userId", value = "USER_ID", required = false, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", required = true, dataType = "Int", example = "100")
    })
    @GetMapping(value="/group/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result pageListEventTriggerGroup(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                   @ApiParam(name = "projectName", value = "PROJECT_NAME",required = true) @PathVariable String projectName,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam(value = "searchVal", required = false) String searchVal,
                                                   @RequestParam(value = "userId", required = false) Integer userId,
                                                   @RequestParam("pageSize") Integer pageSize){
        try{
            logger.info("query event trigger group list paging, login user:{}, project name:{}", loginUser.getUserName(), projectName);
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if(result.get(Constants.STATUS) != Status.SUCCESS){
                return returnDataListPaging(result);
            }
            searchVal = ParameterUtils.handleEscapes(searchVal);
            if(userId == null){
              userId = loginUser.getId();
            }
            result = eventTriggerService.pageListEventTriggerGroup(loginUser, projectName, searchVal, pageNo, pageSize, userId);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(Status.PAGE_LIST_EVENT_TRIGGER_GROUP.getMsg(),e);
            return error(Status.PAGE_LIST_EVENT_TRIGGER_GROUP.getCode(), Status.PAGE_LIST_EVENT_TRIGGER_GROUP.getMsg());
        }
    }

    /**
     * create event trigger group
     * @param loginUser login user
     * @param projectName project name
     * @param groupName group name
     * @param groupDesc group description
     * @param triProcessDefId triggered process define id
     * @param triProcessDefName triggered process define name
     * @param triTimeType trigger time type : DD/MM/YY/HH/MI/SS
     * @param remark remark
     * @param enableFlag enable flag : 1:enabled; 0:disabled
     * @return returns whether success
     */
    @ApiOperation(value = "checkEventTriggerGroup", notes= "CHECK_EVENT_TRIGGER_GROUP")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "GROUP_NAME", dataType ="String"),
            @ApiImplicitParam(name = "triProcessDefName", value = "TRI_PROCESS_DEF_NAME", dataType ="String")
    })
    @PostMapping(value = "/group/check")
    @ResponseStatus(HttpStatus.OK)
    public Result checkEventTriggerGroup(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value="groupName",required = false) String groupName,
                                @RequestParam(value="triProcessDefName",required = false) String triProcessDefName) {

        try {
            logger.info("login user {}, check event trigger group: {}, process:{}"
                       , loginUser.getUserName(), groupName, triProcessDefName);
            TriggerGroup group = eventTriggerService.queryOne(null, groupName, null);
            if(group != null){
                return error(Status.FAILED.getCode(),"Group name has exists");
            }else{
                return success();
            }
        } catch (Exception e) {
            logger.error(Status.FAILED.getMsg(), e);
            return error(Status.FAILED.getCode(), Status.FAILED.getMsg());
        }
    }

    /**
     * create event trigger group
     * @param loginUser login user
     * @param projectName project name
     * @param groupName group name
     * @param groupDesc group description
     * @param triProcessDefId triggered process define id
     * @param triProcessDefName triggered process define name
     * @param triTimeType trigger time type : DD/MM/YY/HH/MI/SS
     * @param remark remark
     * @param enableFlag enable flag : 1:enabled; 0:disabled
     * @return returns whether success
     */
    @ApiOperation(value = "createEventTriggerGroup", notes= "CREATE_EVENT_TRIGGER_GROUP")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectName", value = "PROJECT_NAME", dataType ="String"),
            @ApiImplicitParam(name = "groupName", value = "GROUP_NAME", dataType ="String"),
            @ApiImplicitParam(name = "groupDesc", value = "GROUP_DESC", dataType ="String"),
            @ApiImplicitParam(name = "triProcessDefId", value = "TRI_PROCESS_DEF_ID", dataType ="Integer"),
            @ApiImplicitParam(name = "triProcessDefName", value = "TRI_PROCESS_DEF_NAME", dataType ="String"),
            @ApiImplicitParam(name = "triTimeType", value = "TRI_TIME_TYPE", dataType ="String"),
            @ApiImplicitParam(name = "remark", value = "REMARK", dataType ="String"),
            @ApiImplicitParam(name = "enableFlag", value = "ENABLE_FLAG", dataType = "Integer")
    })
    @PostMapping(value = "/group/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createEventTriggerGroup(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam("projectName") String projectName,
                                @RequestParam("groupName") String groupName,
                                @RequestParam(value = "groupDesc", required = false) String groupDesc,
                                @RequestParam("triProcessDefId") int triProcessDefId,
                                @RequestParam("triProcessDefName") String triProcessDefName,
                                @RequestParam("triTimeType") String triTimeType,
                                @RequestParam(value = "remark", required = false) String remark,
                                @RequestParam(value = "enableFlag", defaultValue = "1") int enableFlag) {

        try {
            logger.info("login user {}, create event trigger group: {}, project:{} process:{}, type:{}"
                       , loginUser.getUserName(), groupName, projectName, triProcessDefName, triTimeType);
            Map<String, Object> result = eventTriggerService.createEventTriggerGroup(loginUser,projectName,groupName,groupDesc
                                         ,triProcessDefId,triProcessDefName,triTimeType,remark,enableFlag);
            return returnDataList(result);
        } catch (Exception e) {
            logger.error(Status.CREATE_EVENT_TRIGGER_GROUP_ERROR.getMsg(), e);
            return error(Status.CREATE_EVENT_TRIGGER_GROUP_ERROR.getCode(), Status.CREATE_EVENT_TRIGGER_GROUP_ERROR.getMsg());
        }
    }

    /**
     * create event trigger group
     * @param loginUser login user
     * @param projectName project name
     * @param groupName group name
     * @param groupDesc group description
     * @param triProcessDefId triggered process define id
     * @param triProcessDefName triggered process define name
     * @param triTimeType trigger time type : DD/MM/YY/HH/MI/SS
     * @param remark remark
     * @param enableFlag enable flag : 1:enabled; 0:disabled
     * @return returns whether success
     */
    @ApiOperation(value = "updateEventTriggerGroup", notes= "UPDATE_EVENT_TRIGGER_GROUP")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "groupId", value = "GROUP_ID", dataType ="Integer"),
        @ApiImplicitParam(name = "groupName", value = "GROUP_NAME", dataType ="String"),
        @ApiImplicitParam(name = "groupDesc", value = "GROUP_DESC", dataType ="String"),
        @ApiImplicitParam(name = "triProcessDefId", value = "TRI_PROCESS_DEF_ID", dataType ="Integer"),
        @ApiImplicitParam(name = "triProcessDefName", value = "TRI_PROCESS_DEF_NAME", dataType ="String"),
        @ApiImplicitParam(name = "triTimeType", value = "TRI_TIME_TYPE", dataType ="String"),
        @ApiImplicitParam(name = "remark", value = "REMARK", dataType ="String"),
        @ApiImplicitParam(name = "enableFlag", value = "ENABLE_FLAG", dataType = "Integer")
    })
    @PostMapping(value = "/group/update")
    @ResponseStatus(HttpStatus.OK)
    public Result updateProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam("groupId") int groupId,
                                @RequestParam(value = "groupName", required = false) String groupName,
                                @RequestParam(value = "groupDesc", required = false) String groupDesc,
                                @RequestParam(value = "triProcessDefId", required = false) Integer triProcessDefId,
                                @RequestParam(value = "triProcessDefName", required = false) String triProcessDefName,
                                @RequestParam(value = "triTimeType", required = false) String triTimeType,
                                @RequestParam(value = "remark", required = false) String remark,
                                @RequestParam(value = "enableFlag", required = false) Integer enableFlag) {
        try {
            logger.info("login user {} , update event trigger group: {}/{}, process:{}, type:{}"
                       , loginUser.getUserName(), groupId, groupName, triProcessDefName, triTimeType);
            Map<String, Object> result = eventTriggerService.updateEventTriggerGroup(loginUser,groupId,groupName,groupDesc
                    ,triProcessDefId,triProcessDefName,triTimeType,remark,enableFlag);
            return returnDataList(result);
        } catch (Exception e) {
            logger.error(Status.UPDATE_EVENT_TRIGGER_GROUP_ERROR.getMsg(), e);
            return error(Status.UPDATE_EVENT_TRIGGER_GROUP_ERROR.getCode(), Status.UPDATE_EVENT_TRIGGER_GROUP_ERROR.getMsg());
        }
    }

    /**
     * delete group by id
     *
     * @param loginUser login user
     * @param groupId group id
     * @return delete result code
     */
    @ApiOperation(value = "deleteGroupById", notes= "DELETE_GROUP_BY_ID")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "groupId", value = "GROUP_ID", dataType ="Int", example = "100")
    })
    @GetMapping(value = "/group/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteEventTriggerGroup(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                          @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                          @RequestParam("groupId") Integer groupId
    ) {
        try {
            logger.info("login user {}, delete group: {}.", loginUser.getUserName(), groupId);
            Map<String, Object> result = eventTriggerService.deleteEventTriggerGroup(loginUser, projectName, groupId);
            return returnDataList(result);
        } catch (Exception e) {
            logger.error(Status.DELETE_EVENT_TRIGGER_GROUP_ERROR.getMsg(), e);
            return error(Status.DELETE_EVENT_TRIGGER_GROUP_ERROR.getCode(), Status.DELETE_EVENT_TRIGGER_GROUP_ERROR.getMsg());
        }
    }

    /**
     * query event trigger group member list
     *
     * @param loginUser login user
     * @param projectName project name
     * @return event trigger group member list
     */
    @ApiOperation(value = "listEventTriggerGroupMember", notes= "LIST_EVENT_TRIGGER_GROUP_MEMBER")
    @GetMapping(value="/group-member/list")
    @ResponseStatus(HttpStatus.OK)
    public Result listEventTriggerGroupMember(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @RequestParam("projectName") String projectName,
                                              @RequestParam("groupName") String groupName
    ){
        try{
            logger.info("query event trigger group member, login user:{}, project name:{}, group name:{}",
                    loginUser.getUserName(), projectName, groupName);
            Map<String, Object> result = eventTriggerService.listEventTriggerGroupMember(loginUser, projectName, groupName);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(Status.LIST_EVENT_TRIGGER_GROUP_MEMBER.getMsg(),e);
            return error(Status.LIST_EVENT_TRIGGER_GROUP_MEMBER.getCode(), Status.LIST_EVENT_TRIGGER_GROUP_MEMBER.getMsg());
        }
    }

    /**
     * list event trigger group member by page
     * @param loginUser login user
     * @param projectName project name
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @param userId user id
     * @return event trigger group member page
     */
    @ApiOperation(value = "pageListEventTriggerGroupMember", notes= "PAGE_LIST_EVENT_TRIGGER_GROUP_MEMBER")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "searchVal", value = "SEARCH_VAL", required = false, type = "String"),
            @ApiImplicitParam(name = "groupName", value = "GROUP_NAME", required = false, type = "String"),
            @ApiImplicitParam(name = "userId", value = "USER_ID", required = false, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", required = true, dataType = "Int", example = "100")
    })
    @GetMapping(value="/group-member/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result pageListEventTriggerGroupMember(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                   @ApiParam(name = "projectName", value = "PROJECT_NAME",required = true) @PathVariable String projectName,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam(value = "searchVal", required = false) String searchVal,
                                                   @RequestParam(value = "groupMember", required = false) String groupMember,
                                                   @RequestParam(value = "userId", required = false) Integer userId,
                                                   @RequestParam("pageSize") Integer pageSize){
        try{
            logger.info("query event trigger group member list paging, login user:{}, project name:{}", loginUser.getUserName(), projectName);
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if(result.get(Constants.STATUS) != Status.SUCCESS){
                return returnDataListPaging(result);
            }
            searchVal = ParameterUtils.handleEscapes(searchVal);
            if(userId == null){
              userId = loginUser.getId();
            }
            result = eventTriggerService.pageListEventTriggerGroupMember(loginUser, projectName, groupMember, searchVal, pageNo, pageSize, userId);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(Status.PAGE_LIST_EVENT_TRIGGER_GROUP_MEMBER.getMsg(),e);
            return error(Status.PAGE_LIST_EVENT_TRIGGER_GROUP_MEMBER.getCode(), Status.PAGE_LIST_EVENT_TRIGGER_GROUP_MEMBER.getMsg());
        }
    }

    /**
     * save event trigger group members
     * @param loginUser login user
     * @param groupId unique id of each group
     * @param groupName group name
     * @param oldMbrsNum original members number
     * @param nowMbrsNum current members number
     * @param groupMembers JSON of current members
     * @return returns whether success
     */
    @ApiOperation(value = "saveEventTriggerMembers", notes= "SAVE_EVENT_TRIGGER_GROUP_MEMBERS")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "groupId", value = "GROUP_ID", dataType ="Integer"),
        @ApiImplicitParam(name = "groupName", value = "GROUP_NAME", dataType ="String"),
        @ApiImplicitParam(name = "oldMbrsNum", value = "OLD_MEMBERS_NUM", dataType ="Integer"),
        @ApiImplicitParam(name = "nowMbrsNum", value = "NOW_MEMBERS_NUM", dataType ="Integer"),
        @ApiImplicitParam(name = "groupMembers", value = "GROUP_MEMBERS", dataType ="String")
    })
    @PostMapping(value = "/member/save-batch")
    @ResponseStatus(HttpStatus.OK)
    public Result saveMembers(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "groupId", required = false) Integer groupId,
                                @RequestParam(value = "groupName", required = true) String groupName,
                                @RequestParam(value = "oldMbrsNum", required = true) int oldMembersNum,
                                @RequestParam(value = "nowMbrsNum", required = true) int nowMembersNum,
                                @RequestParam(value = "groupMembers", required = false) String groupMembers) {
        try {
            logger.info("login user {} , save event trigger members: {}/{}, members number:{}, members:{}"
                       , loginUser.getUserName(), groupId, groupName, nowMembersNum, groupMembers);
            List<TriggerGroupMember> members = null;
            Result result = null;
            if(StringUtils.isNotBlank(groupMembers)){
                members = JSONArray.parseArray(groupMembers,TriggerGroupMember.class);
                if(members.size() != nowMembersNum){
                    result = new Result();
                    result.setCode(Status.FAILED.getCode());
                    result.setMsg("Invalid params");
                    return result;
                }
            }
            if(oldMembersNum == 0 && nowMembersNum == 0){
                //do nothing
                result = new Result();
                result.setCode(Status.SUCCESS.getCode());
                result.setMsg("No change");
                return result;
            }else if(oldMembersNum > 0 && nowMembersNum == 0){
                //truncate original members
                Map<String, Object> optResult = eventTriggerService.deleteEventTriggerMembers(loginUser, groupId, groupName, oldMembersNum);
                return returnDataList(optResult);
            }else{
                //insert after truncate
                Map<String, Object> checkResult = eventTriggerService.checkMembers(members);
                if(!Status.SUCCESS.equals(checkResult.get(Constants.STATUS))){
                    return returnDataList(checkResult);
                }
                //TODO backup first : Do roll back if failed
                Map<String, Object> optResult = eventTriggerService.deleteEventTriggerMembers(loginUser, groupId, groupName, oldMembersNum);
                if(optResult.get(Constants.STATUS).equals(Status.SUCCESS)){
                    optResult.putAll(eventTriggerService.saveEventTriggerMembers(loginUser, groupId, groupName, members));
                    return returnDataList(optResult);
                }else{
                    return returnDataList(optResult);
                }
            }
        } catch (Exception e) {
            logger.error(Status.UPDATE_EVENT_TRIGGER_GROUP_ERROR.getMsg(), e);
            return error(Status.UPDATE_EVENT_TRIGGER_GROUP_ERROR.getCode(), Status.UPDATE_EVENT_TRIGGER_GROUP_ERROR.getMsg());
        }
    }

}
