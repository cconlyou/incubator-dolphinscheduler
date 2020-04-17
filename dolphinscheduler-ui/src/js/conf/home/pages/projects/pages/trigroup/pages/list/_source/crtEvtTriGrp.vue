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
<template>
  <m-popup style="width:720px;"
           ref="popup"
           :ok-text="$t('Submit')"
           :nameText="(popFlag == 'NEW' ? $t('Create') : $t('Edit')) + (popFlag == 'Member' ? $t('Trigger GrpMember') : $t('Trigger group'))"
           @ok="_ok">
    <template slot="content">
      <div class="event-trigger-group">
        <m-list-box-f>
          <template slot="name"><strong>*</strong>{{$t('Project Name')}}</template>
          <template slot="content">
            <x-input
                    type="input"
                    v-model="projectName"
                    autocomplete="off"
                    disabled="true">
            </x-input>
          </template>
        </m-list-box-f>
        <m-list-box-f>
          <template slot="name"><strong>*</strong>{{$t('Process Name')}}</template>
          <template slot="content">
            <!-- <x-input
                    type="input"
                    v-model="triProcessDefName"
                    maxlength="60"
                    :placeholder="$t('Please enter name')"
                    autocomplete="off">
            </x-input> -->
            <x-select style="width: 100%;"
                      filterable
                      v-model="triProcessDefId"
                      :disabled="popFlag == 'Member'"
                      @on-change="_onChangeDefinitionId">
              <x-option v-for="item in processDefList" :key="item.value" :value="item.value" :label="item.label">
              </x-option>
            </x-select>
          </template>
        </m-list-box-f>
        <m-list-box-f>
          <template slot="name"><strong>*</strong>{{$t('Trigger Time Type')}}</template>
          <template slot="content">
            <x-select
                    style="width: 100%;"
                    :disabled="popFlag == 'Member'"
                    v-model="triTimeType">
              <x-option
                      v-for="item in triTimeTypeList"
                      :key="item.code"
                      :value="item.code"
                      :label="item.desc">
              </x-option>
            </x-select>
          </template>
        </m-list-box-f>
        <m-divider>
          <template slot="name">Group Info</template>
        </m-divider>
        <m-list-box-f>
          <template slot="name"><strong>*</strong>{{$t('Group Name')}}</template>
          <template slot="content">
            <x-input
                    type="input"
                    v-model="groupName"
                    :disabled="popFlag == 'Member'"
                    :placeholder="$t('Please Enter')+$t('Group Name')"
                    autocomplete="off">
            </x-input>
          </template>
        </m-list-box-f>
        <m-list-box-f>
          <template slot="name">{{$t('Group Description')}}</template>
          <template slot="content">
            <x-input
                    type="textarea"
                    v-model="groupDesc"
                    :disabled="popFlag == 'Member'"
                    :placeholder="$t('Please Enter')+$t('Trigger group')+$t('Description')"
                    autocomplete="off">
            </x-input>
          </template>
        </m-list-box-f>
        <m-list-box-f style="margin:-15px 0px 0px 0px;">
          <template slot="name">{{$t('Enable Flag')}}</template>
          <template slot="content">
            <x-switch style="margin:7px 0px 0px 0px;" v-model="enableFlag" :disabled="popFlag == 'Member'"></x-switch>
          </template>
        </m-list-box-f>
        <div class="mbr-opt" v-if="popFlag != 'Group'">
          <m-divider>
            <template slot="name">Member Info</template>
          </m-divider>
          <a href="javascript:"
             @click="!isDetails && _addMember()"
             class="add-mbr">
            <em v-if="!isLoading" class="ans-icon-increase" :class="_isDetails" data-toggle="tooltip" :title="$t('Add')"></em>
            <em v-if="isLoading" class="ans-icon-spinner2 as as-spin" data-toggle="tooltip" :title="$t('Add')"></em>
            {{$t('Add')}} {{$t('Trigger GrpMember')}}
          </a>
        </div>
        <div class="mbr-list" v-if="memberList.length">
          <div class="mbr-item" v-for="(item,$index) in memberList" :key='$index'>
            <m-member
              @on-delete-mbr="_onDeleteMember"
              :member="item"
              :memberIndex="$index">
            </m-member>
          </div>
        </div>
      </div>
    </template>
  </m-popup>
</template>
<script>
  import _ from 'lodash'
  import i18n from '@/module/i18n'
  import store from '@/conf/home/store'
  import { triTimeTypeEnum } from '@/conf/home/pages/dag/_source/config'
  import mPopup from '@/module/components/popup/popup'
  import mListBoxF from '@/module/components/listBoxF/listBoxF'
  import mDivider from '@/module/components/divider/divider'
  import mMember from './GroupMember'

  export default {
    //event trigger group create
    name: 'evtTriGrp-create',
    data () {
      return {
        store,
        triTimeTypeList: triTimeTypeEnum,
        groupName: '',
        groupNameAuto : true,
        projectId: store.state.triggers.projectId,
        projectName: store.state.triggers.projectName,
        triProcessDefId: 0,
        triProcessDefName: '',
        processDefList: Array,
        triTimeType: '',
        remark: '',
        enableFlag: true

        //member module
        ,oldMembersNum: 0
        ,memberList: []
        ,isLoading: false
      }
    },
    props: {
      tableItem: Object
      ,popFlag: String
    },
    methods: {
      _getProcessByProjectId (id) {
        return new Promise((resolve, reject) => {
          this.store.dispatch('dag/getProcessByProjectId', { projectId: id }).then(res => {
            let definitionList = _.map(_.cloneDeep(res), v => {
              return {
                value: v.id,
                label: v.name
              }
            })
            resolve(definitionList)
          })
        })
      },
      _getMemberByGroupInfo (projectName,groupName) {
        return new Promise((resolve, reject) => {
          this.store.dispatch('triggers/getMemberByGroupInfo', { projectName: projectName,groupName: groupName }).then(res => {
            let resultList = _.map(_.cloneDeep(res), v => {
              return {
                projectId: v.projectId,
                projectName: v.projectName,
                definitionId: v.processDefId,
                definitionName: v.processDefName,
                depTasks: v.taskId || 'ALL',
                enableFlag: v.enableFlag == 1 ? true : false
              }
            })
            resolve(resultList)
          })
        })
      },
      _onChangeDefinitionId ({label}) {
        this.triProcessDefName = label
      },
      _ok () {
        //start check
        if (!this._checkGroup()) {
          return
        }
        if (!this._checkMember()) {
          return
        }
        //group info
        let groupParam = {
          projectName: _.trim(this.projectName),
          groupName: _.trim(this.groupName),
          groupDesc: _.trim(this.groupDesc),
          triProcessDefId: this.triProcessDefId,
          triProcessDefName: _.trim(this.triProcessDefName),
          triTimeType: _.trim(this.triTimeType),
          remark: _.trim(this.remark),
          enableFlag: this.enableFlag ? 1: 0
        }
        // whether is group edit
        if (this.tableItem) {
          groupParam.groupId = this.tableItem.groupId
        }
        this.$refs['popup'].spinnerLoading = true
        //update members after updated group
        if(this.popFlag == 'NEW'){
          this.store.dispatch('triggers/createGroups', groupParam).then(res => {
            this._saveMembers()
          }).catch(e => {
            this.$message.error(e.msg || '')
            this.$refs['popup'].spinnerLoading = false
          })
        //only update group
        }else if(this.popFlag == 'Group'){
          this.store.dispatch('triggers/updateGroups', groupParam).then(res => {
            this.$emit('onUpdate')
            this.$message.success(res.msg)
            setTimeout(() => {
              this.$refs['popup'].spinnerLoading = false
            }, 800)
          }).catch(e => {
            this.$message.error(e.msg || '')
            this.$refs['popup'].spinnerLoading = false
          })
        //only update members
        }else if(this.popFlag == 'Member'){
          this._saveMembers()
        }else{
          this.$message.error('Do nothing')
          this.$refs['popup'].spinnerLoading = false
        }
      },
      _saveMembers() {
        //members info
        let memberParam = {
          groupName: _.trim(this.groupName),
          oldMbrsNum: this.oldMembersNum || 0,
          nowMbrsNum: this.memberList.length || 0
        }
        //set param of 'groupId'
        if (this.tableItem) {
          memberParam.groupId = this.tableItem.groupId
        }
        //set param of 'groupMembers'
        if(this.memberList.length){
          let groupMbrs = _.map(this.memberList,(v,i) => {
            return {
                orderInGroup: i+1,
                projectId: v.projectId,
                projectName: v.projectName,
                processDefId: v.definitionId,
                processDefName: v.definitionName,
                taskId: v.depTasks || 'ALL',
                taskName: v.taskName || null,
                enableFlag: v.enableFlag ? 1 : 0
              }
          })
          memberParam.groupMembers = JSON.stringify(groupMbrs)
        }
        if(memberParam.oldMbrsNum == 0 && memberParam.nowMbrsNum == 0){
          if(this.popFlag == 'Member'){
            this.$message.warning('No change')
          }
          this.$refs['popup'].spinnerLoading = false
          this.$emit('onUpdate')
        }else{
          this.$refs['popup'].spinnerLoading = true
          this.store.dispatch('triggers/saveMembers', memberParam).then(res => {
            this.$emit('onUpdate')
            this.$message.success(res.msg)
            setTimeout(() => {
              this.$refs['popup'].spinnerLoading = false
            }, 800)
          }).catch(e => {
            this.$message.error(e.msg || '')
            this.$refs['popup'].spinnerLoading = false
          })
        }
      },
      _checkGroup () {
        if (!this.projectName) {
          this.$message.warning(`${i18n.$t('Please enter name')}`)
          return false
        }
        if (!this.triProcessDefId) {
          this.$message.warning(`${i18n.$t('Please Select')}${i18n.$t('Process Name')}`)
          return false
        }
        if (!this.triTimeType) {
          this.$message.warning(`${i18n.$t('Please Select')}${i18n.$t('Trigger Time Type')}`)
          return false
        }
        if (!this.groupName) {
          this.$message.warning(`${i18n.$t('Please Enter')}${i18n.$t('Group Name')}`)
          return false
        }
        return true
      },
      _checkMember () {
        if(this.memberList.length > 0){
          //check for each other
          for(let i=0;i<this.memberList.length;i++) {
            let v = this.memberList[i]
            if(!v.projectId){
              this.$message.warning(`${i18n.$t('Please Select')}${i18n.$t('Project Name')}` + ' For member['+(i+1)+']')
              return false
            }
            if(!v.definitionId){
              this.$message.warning(`${i18n.$t('Please Select')}${i18n.$t('Process Name')}` + ' For member['+(i+1)+']')
              return false
            }
            if(v.projectId == this.projectId && v.definitionId == this.triProcessDefId){
              this.$message.warning('The member['+(i+1)+'] can not be same with group !')
              return false
            }
            //can be same with each other
            for(let j=0;j<=i-1;j++){
              let v2 = this.memberList[j]
              if(v2.projectId == v.projectId && v2.definitionId == v.definitionId){
                if(v2.depTasks == v.depTasks){
                  this.$message.warning('The member['+(i+1)+'] can not be same with member['+(j+1)+'] !')
                  return false
                }else if(v2.depTasks == 'ALL'){
                  this.$message.warning('The member['+(i+1)+'] is invalid when member['+(j+1)+'] exists !')
                  return false
                }else if(v.depTasks == 'ALL'){
                  this.$message.warning('The member['+(j+1)+'] is invalid when member['+(i+1)+'] exists !')
                  return false
                }else{
                  //do nothing
                }
              }
            }
          }
        }else if(this.oldMembersNum > 0){
          //TODO add confirm popup
        }else{
          //do nothing
        }
        return true
      },
      _genGroupName(processName){
        if(!processName || !this.projectName){
          return ''
        }else{
          return this.projectName + '_' + processName
        }
      },
      /**START**** for member manager ******/
      _onDeleteMember (i) {
        this.memberList.splice(i,1)
        this.$message.info(`${i18n.$t('It will be deleted actually after submit')}`)
      },
      _addMember () {
        if (!this.isLoading) {
          this.isLoading = true
          this.memberList.push({})
        }
      }
      /**End**** for member manager ******/
    },
    watch: {
      triProcessDefName (name) {
        if (this.groupNameAuto) {
          this.groupName = this._genGroupName(name)
        }
      },
      groupName (name) {
        if (this.groupName != this._genGroupName(this.triProcessDefName)) {
          this.groupNameAuto = false
        } else {
          this.groupNameAuto = true
        }
      },
      memberList (e) {
        setTimeout(() => {
          this.isLoading = false
        }, 500)
      },
      processDefList(e) {
        if(e.length){
          let item = _.filter(e, v => v.value === this.triProcessDefId)[0]
          if(item && item.label){
            this.triProcessDefName = item.label
          }
        }
      }
    },
    created () {
      // init process list
      if (this.popFlag == 'Member') {
        //disabled : only for view
        this.processDefList = [{value:this.tableItem.triProcessDefId
                               ,label:this.tableItem.triProcessDefName}]
      }else {
        this._getProcessByProjectId(this.projectId).then(definitionList => {
          this.processDefList = definitionList
        })
      }

      if (this.tableItem) {
        if(this.tableItem.groupName){
          this.groupNameAuto = false
          this.groupName = this.tableItem.groupName
        }else{
          this.groupNameAuto = true
        }
        this.projectName = this.tableItem.triProjectName || this.store.state.dag.projectName
        this.triProcessDefId = this.tableItem.triProcessDefId
        
        //this.triProcessDefName = this.tableItem.triProcessDefName
        this.triTimeType = this.tableItem.triTimeType
        this.groupDesc = this.tableItem.groupDesc
        this.enableFlag = this.tableItem.enableFlag == 1
      }
      // init group members
      if (this.popFlag == 'Member' && this.tableItem) {
        this._getMemberByGroupInfo(this.tableItem.triProjectName,this.tableItem.groupName).then(resultList => {
          this.memberList = resultList
          this.oldMembersNum = resultList.length || 0
        })
      }
    },
    mounted () {
      
    },
    components: { mPopup, mListBoxF, mDivider, mMember }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .event-trigger-group {
    margin-top: -10px;
    .list-box-f {
      margin-bottom: 10px;
    }
    .mbr-opt {
      margin-bottom: 0px;
      padding-top: 3px;
      line-height: 24px;
      .add-mbr {
        color: #0097e0;
        margin-left: 20px;
        //margin-right: 10px;
        i {
          font-size: 18px;
          vertical-align: middle;
        }
      }
    }
    .mbr-list {
      overflow-y: scroll;
      min-height: 200px;
      max-height: 200px;
    }
    .mbr-item {
      margin-bottom: 0px;
      position: relative;
      border-left: 1px solid #eee;
      padding-left: 10px;
      margin-left: 0px;
      transition: all 0.2s ease-out;
      padding-bottom: 0px;
      .mbr-delete {
        position: absolute;
        bottom: -6px;
        left: 14px;
        font-size: 18px;
        color: #ff0000;
        cursor: pointer;
      }
    }
    .mbr-box {
      border-left: 4px solid #eee;
      margin-left: -46px;
      padding-left: 42px;
      position: relative;
      .mbr-relation {
        position: absolute;
        width: 20px;
        height: 20px;
        border: 1px solid #e2e2e2;
        text-align: center;
        top: 50%;
        margin-top: -10px;
        z-index: 1;
        left: -12px;
        border-radius: 10px;
        background: #fff;
        font-size: 12px;
        cursor: pointer;
        &::selection {
          background:transparent;
        }
        &::-moz-selection {
          background:transparent;
        }
        &::-webkit-selection {
          background:transparent;
        }
      }
    }
  }
</style>
