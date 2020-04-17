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
          <th scope="col" width="240">
            <span>{{$t('Group Name')}}</span>
          </th>
          <th scope="col" width="60">
            <span>{{$t('#Group')}}</span>
          </th>
          <th scope="col" width="80">
            <span>{{$t('Project Name')}}</span>
          </th>
          <!-- <th scope="col">
            <span>Process ID</span>
          </th> -->
          <th scope="col" width="200">
            <span>{{$t('Process Name')}}</span>
          </th>
          <th scope="col">
            <span>Task Name</span>
          </th>
          <th scope="col">
            <span>Member Type</span>
          </th>
          <!-- <th scope="col">
            <span>Remark</span>
          </th> -->
          <th scope="col" width="60">
            <span>{{$t('Enable Flag')}}</span>
          </th>
          <th scope="col" width="70">
            <span>{{$t('Update Time')}}</span>
          </th>
        </tr>
        <tr v-for="(item, $index) in list" :key="$index">
          <td>
            <span>{{parseInt(pageNo === 1 ? ($index + 1) : (($index + 1) + (pageSize * (pageNo - 1))))}}</span>
          </td>
          <td>
            <span>{{item.groupName}}</span>
          </td>
          <td>
            <span>{{item.orderInGroup}}</span>
          </td>
          <td>
            <span>{{item.projectName}}</span>
          </td>
          <!-- <td>
            <span>{{item.processDefId}}</span>
          </td> -->
          <td>
            <span>{{item.processDefName}}</span>
          </td>
          <td>
            <span>{{item.taskName}}</span>
          </td>
          <td>
            <span>{{_rtReleaseEvtTriType(item.memberType)}}</span>
          </td>
          <!-- <td>
            <span>{{item.remark}}</span>
          </td> -->
          <td>
            <span>{{_rtReleaseEnableFlag(item.enableFlag)}}</span>
          </td>
          <td>
            <span v-if="item.updateTime">{{item.updateTime | formatDate}}</span>
            <span v-else>-</span>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>
<script>
  import _ from 'lodash'
  import localStore from '@/module/util/localStorage'
  import { findComponentDownward } from '@/module/util/'
  import { eventTriggerTypeEnum,enableFlagEnum } from '@/conf/home/pages/dag/_source/config'

  export default {
    name: 'list',
    data () {
      return {
        list: []
      }
    },
    props: {
      triGrpMbrList: Array,
      pageNo: Number,
      pageSize: Number
    },
    methods: {
      /**
       * return event trigger type
       */
      _rtReleaseEvtTriType (code) {
        return _.filter(eventTriggerTypeEnum, v => v.code === code)[0].desc
      },
      /**
       * return event trigger type
       */
      _rtReleaseEnableFlag (code) {
        return _.filter(enableFlagEnum, v => v.id === code)[0].desc
      }
    },
    watch: {
      triGrpMbrList (a) {
        this.list = []
        setTimeout(() => {
          this.list = a
        })
      }
    },
    created () {
    },
    mounted () {
      this.list = this.triGrpMbrList
    },
    components: { }
  }
</script>
