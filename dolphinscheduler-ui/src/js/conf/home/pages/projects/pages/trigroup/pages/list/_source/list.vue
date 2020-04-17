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
  <div class="list-model">
    <div class="table-box">
      <table class="fixed">
        <tr>
          <th scope="col">
            <span>{{$t('#')}}</span>
          </th>
          <th scope="col" width="60">
            <span>Group ID</span>
          </th>
          <th scope="col" width="240">
            <span>{{$t('Group Name')}}</span>
          </th>
          <th scope="col">
            <span>{{$t('Project Name')}}</span>
          </th>
          <!-- <th scope="col">
            <span>TriProcessID</span>
          </th> -->
          <th scope="col" width="200">
            <span>{{$t('Process Name')}}</span>
          </th>
          <th scope="col">
            <span>{{$t('Trigger Time Type')}}</span>
          </th>
          <th scope="col">
            <span>{{$t('Group Description')}}</span>
          </th>
          <th scope="col" width="60">
            <span>{{$t('Enable Flag')}}</span>
          </th>
          <th scope="col" width="70">
            <span>{{$t('Update Time')}}</span>
          </th>
          <th scope="col" width="160">
            <span>{{$t('Operation')}}</span>
          </th>
        </tr>
        <tr v-for="(item, $index) in list" :key="$index">
          <td>
            <span>{{parseInt(pageNo === 1 ? ($index + 1) : (($index + 1) + (pageSize * (pageNo - 1))))}}</span>
          </td>
          <td>
            <span>{{item.groupId}}</span>
          </td>
          <td>
            <span>{{item.groupName || '-'}}</span>
          </td>
          <td>
            <span>{{item.triProjectName}}</span>
          </td>
          <!-- <td>
            <span>{{item.triProcessDefId}}</span>
          </td> -->
          <td>
            <span>{{item.triProcessDefName}}</span>
          </td>
          <td>
            <span>{{item.triTimeType}}</span>
          </td>
          <td>
            <span>{{item.groupDesc}}</span>
          </td>
          <td>
            <span>{{_rtReleaseEnableFlag(item.enableFlag)}}</span>
          </td>
          <td>
            <span v-if="item.updateTime">{{item.updateTime | formatDate}}</span>
            <span v-else>-</span>
          </td>
          <td>
            <x-button
                    type="info"
                    shape="circle"
                    size="xsmall"
                    data-toggle="tooltip"
                    :title="$t('Edit')"
                    @click="_edit(item,'Group')"
                    icon="ans-icon-edit">
            </x-button>
            <x-button
                    type="info"
                    shape="circle"
                    size="xsmall"
                    data-toggle="tooltip"
                    :title="$t('Group Members Manage')"
                    @click="_edit(item,'Member')"
                    icon="ans-icon-group">
            </x-button>
            <x-poptip
                    :ref="'jumpto-' + $index"
                    placement="bottom-end"
                    width="90">
              <p>{{$t('It will jump to another page')}}</p>
              <div style="text-align: center; margin: 0;padding-top: 4px;">
                <x-button type="text" size="xsmall" shape="circle" @click="_closeJumpTo($index)">{{$t('Cancel')}}</x-button>
                <x-button type="primary" size="xsmall" shape="circle" @click="_jumpTo(item,$index)">{{$t('Confirm')}}</x-button>
              </div>
              <template slot="reference">
                <x-button
                        type="info"
                        shape="circle"
                        size="xsmall"
                        data-toggle="tooltip"
                        :title="$t('Trigger Manage')"
                        icon="ans-icon-datetime">
                </x-button>
              </template>
            </x-poptip>
            <!-- delete button -->
            <x-poptip
                    :ref="'poptip-' + $index"
                    placement="bottom-end"
                    width="90">
              <p>{{$t('Delete?')}}</p>
              <div style="text-align: center; margin: 0;padding-top: 4px;">
                <x-button type="text" size="xsmall" shape="circle" @click="_closeDelete($index)">{{$t('Cancel')}}</x-button>
                <x-button type="primary" size="xsmall" shape="circle" @click="_delete(item,$index)">{{$t('Confirm')}}</x-button>
              </div>
              <template slot="reference">
                <x-button
                        type="error"
                        shape="circle"
                        size="xsmall"
                        data-toggle="tooltip"
                        :title="$t('delete')"
                        icon="ans-icon-trash">
                </x-button>
              </template>
            </x-poptip>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>
<script>
  import _ from 'lodash'
  import { mapActions } from 'vuex'
  import localStore from '@/module/util/localStorage'
  import { findComponentDownward } from '@/module/util/'
  import { enableFlagEnum } from '@/conf/home/pages/dag/_source/config'

  export default {
    name: 'list',
    data () {
      return {
        list: []
      }
    },
    props: {
      triGroupsList: Array,
      pageNo: Number,
      pageSize: Number
    },
    methods: {
      ...mapActions('triggers', ['deleteGroups']),
      /**
       * return event trigger type
       */
      _rtReleaseEnableFlag (code) {
        return _.filter(enableFlagEnum, v => v.id === code)[0].desc
      },
      /**
       * edit project
       * @param item Current record
       */
      _edit (item,flag) {
        findComponentDownward(this.$root, 'event-trigger-group-list-index')._create(item,flag)
      },
      /**
       * cancel jump operation
       */
      _closeJumpTo(i) {
        this.$refs[`jumpto-${i}`][0].doClose()
      },
      _jumpTo(item, i) {
        this.$refs[`jumpto-${i}`][0].doClose()
        //this.$message.warning('Feature not yet open, please wait')
        this.$router.push({ path: `/projects/definition/list/timing/${item.triProcessDefId}`})
      },
      /**
       * cancel delete
       */
      _closeDelete (i) {
        this.$refs[`poptip-${i}`][0].doClose()
      },
      /**
       * Delete event trigger group
       * @param item Current record
       * @param i index
       */
      _delete (item, i) {
        this.deleteGroups({
          projectName: item.triProjectName,
          groupId: item.groupId
        }).then(res => {
          this.$refs[`poptip-${i}`][0].doClose()
          this.$emit('on-update')
          this.$message.success(res.msg)
        }).catch(e => {
          this.$refs[`poptip-${i}`][0].doClose()
          this.$message.error(e.msg || '')
        })
      }
    },
    watch: {
      triGroupsList (a) {
        this.list = []
        setTimeout(() => {
          this.list = a
        })
      }
    },
    created () {
    },
    mounted () {
      this.list = this.triGroupsList
    },
    components: { }
  }
</script>
