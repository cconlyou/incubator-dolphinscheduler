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
  <div class="dep-list-model">
    <div :key='memberIndex' class="list" @click="itemIndex = memberIndex">
      <label style="width:15px;text-align:center;">{{memberIndex + 1}}</label>
      <x-select filterable :style="{width:isInstance ? '150px' : '150px'}" :disabled="isDetails" v-model="member.projectId" :title="$t('Project info of Member')" @on-change="_onChangeProjectId">
        <x-option v-for="item in projectList" :key="item.value" :value="item.value" :label="item.label">
        </x-option>
      </x-select>
      <x-select filterable :style="{width:isInstance ? '270px' : '270px'}" :disabled="isDetails" v-model="member.definitionId" :title="$t('Process info of Member')" @on-change="_onChangeDefinitionId">
        <x-option v-for="item in member.definitionList" :key="item.value" :value="item.value" :label="item.label">
        </x-option>
      </x-select>
      <x-select filterable :style="{width:isInstance ? '180px' : '180px'}" :disabled="isDetails" v-model="member.depTasks" :title="$t('SubTask info of Member')"  @on-change="_onChangeTaskId">
        <x-option v-for="item in member.depTasksList || []" :key="item.value" :value="item.label" :label="item.label">
        </x-option>
      </x-select>
      <span class="operation">
        <a href="javascript:" @click="!isDetails && _changeEnable(memberIndex)">
          <em key="ENABLED" v-if="member.enableFlag" class="ans-icon-circle-solid enabled" data-toggle="tooltip" data-container="body" :title="$t('Trigger GrpMember')+'-'+$t('Enable Flag')+':'+$t('Enabled')"></em>
          <em key="DISABLED" v-if="!member.enableFlag" class="ans-icon-circle-solid disabled" data-toggle="tooltip" data-container="body" :title="$t('Trigger GrpMember')+'-'+$t('Enable Flag')+':'+$t('Disabled')"></em>
        </a>
        <a href="javascript:" class="delete" @click="!isDetails && _remove(memberIndex)">
          <em class="ans-icon-trash" :class="_isDetails" data-toggle="tooltip" data-container="body" :title="$t('delete')" ></em>
        </a>
      </span>
    </div>
  </div>
</template>
<script>
  import _ from 'lodash'
  import i18n from '@/module/i18n'
  import disabledState from '@/module/mixin/disabledState'
  export default {
    name: 'dep-list',
    data () {
      return {
        list: [],
        projectList: [],
        isInstance: false,
      }
    },
    mixins: [disabledState],
    props: {
      memberIndex: Number,
      member: Object
    },
    model: {
    },
    methods: {
      /**
       * change enable status
       */
      _changeEnable(i){
        this._removeTip()
        this.$set(this.member,'enableFlag',!this.member.enableFlag)
      },
      /**
       * remove task
       */
      _remove (i) {
        this._removeTip()
        this.$emit('on-delete-mbr', this.memberIndex)
      },
      _getProjectList () {
        if(!this.projectList.length){
          return new Promise((resolve, reject) => {
            this.store.dispatch('dag/getProjectList', {}).then(res => {
              this.projectList = _.map(_.cloneDeep(res), v => {
                return {
                  value: v.id,
                  label: v.name
                }
              })
              resolve()
            })
          })
        }
      },
      /**
       * get processlist
       */
      _getProcessList () {
        return new Promise((resolve, reject) => {
          let definitionList = _.map(_.cloneDeep(this.store.state.dag.processListS), v => {
            return {
              value: v.id,
              label: v.name
            }
          })
          resolve(definitionList)
        })
      },
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
      /**
       * get dependItemList
       */
      _getDependItemList (ids, is = true) {
        return new Promise((resolve, reject) => {
          if (is) {
            this.store.dispatch('dag/getProcessTasksList', { processDefinitionId: ids }).then(res => {
              let taskList = _.map(_.cloneDeep(res), v => {
                return {
                  value: v.id,
                  label: v.name
                }
              }).concat({value:'ALL'})
              resolve(taskList)
            })
          } else {
            this.store.dispatch('dag/getTaskListDefIdAll', { processDefinitionIdList: ids }).then(res => {
              resolve(res)
            })
          }
        })
      },
      /**
       * change process get dependItemList
       */
      _onChangeProjectId (selected) {
        this._getProcessByProjectId(selected.value).then(definitionList => {
          this.$set(this.member,'projectName',selected.label)
          this.$set(this.member,'definitionList',definitionList)
          this.$set(this.member,'definitionId',null)
          this.$set(this.member,'depTasksList',['ALL'])
          this.$set(this.member,'depTasks','ALL')
        })
      },
      _onChangeDefinitionId (selected) {
        // get depItem list data
        this._getDependItemList(selected.value).then(depTasksList => {
          this.$set(this.member,'definitionName',selected.label)
          this.$set(this.member,'depTasksList',depTasksList)
          this.$set(this.member,'depTasks','ALL')
        })
      },
      _onChangeTaskId (selected) {
        this.$set(this.member,'taskName',selected.label)
      },
      /**
       * remove tip
       */
      _removeTip () {
        $('body').find('.tooltip.fade.top.in').remove()
      }
    },
    watch: {
    },
    beforeCreate () {
    },
    created () {
      //TODO : is type projects-instance-details
      this.isInstance = this.router.history.current.name === 'projects-instance-details'
      // get processlist
      this._getProjectList().then(() => {
        if (!Object.keys(this.member).length) {
          //this.$set(this.member,'projectId',`${i18n.$t('Please Select')}`)
          //this.$set(this.member,'definitionId',`${i18n.$t('Please Select')}`)
          this.$set(this.member,'depTasks','ALL')
          this.$set(this.member,'enableFlag',true)
        } else {
          // get item list
          if(this.member.definitionId){
            //only one
            //init process list
            this._getProcessByProjectId(this.member.projectId).then(definitionList => {
              this.$set(this.member,'definitionList',definitionList)
            })
            //init task list
            this._getDependItemList(String(this.member.definitionId)).then(taskList => {
              this.$set(this.member,'depTasksList',taskList)
            })
          }
        }
      })
    },
    mounted () {
    },
    components: {}
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .dep-list-model {
    position: relative;
    min-height: 32px;
    .list {
      margin-bottom: 6px;
      select {
        font-size: 12px;
      }
      .operation {
        padding-left: 4px;
        a {
          i {
            font-size: 18px;
            vertical-align: middle;
          }
        }
        .delete {
          color: #ff0000;
        }
        .enabled {
          color: #0097e0;
        }
        .disabled {
          color: #888888;
        }
      }
    }
    .instance-state {
      display: inline-block;
      width: 24px;
      .iconfont {
        font-size: 20px;
        vertical-align: middle;
        cursor: pointer;
        margin-left: 6px;
        &.icon-SUCCESS {
          color: #33cc00;
        }
        &.icon-WAITING {
          color: #888888;
        }
        &.icon-FAILED {
          color: #F31322;
        }
      }
    }
  }
</style>