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
  <m-list-construction :title="pageHeader">
    <template slot="conditions">
      <m-conditions>
        <template slot="button-group">
          <x-button type="ghost" :disabled="!enableNew" size="small" @click="_create()">{{$t('Create Trigger')}}</x-button>
        </template>
        <template slot="search-group">
          <p></p>
        </template>
      </m-conditions>
    </template>
    <template slot="content">
      <m-list v-if="listShow" @toParent="_onParent"></m-list>
    </template>
  </m-list-construction>
</template>
<script>
  import i18n from '@/module/i18n'
  import { mapActions } from 'vuex'
  import mList from './_source/list'
  import mTiming from '../pages/list/_source/timing'
  import mSecondaryMenu from '@/module/components/secondaryMenu/secondaryMenu'
  import mConditions from '@/module/components/conditions/conditions'
  import mListConstruction from '@/module/components/listConstruction/listConstruction'
  export default {
    name: 'definition-timing-index',
    data () {
      return {
        processDefId: 0,
        processDefName: null,
        listShow: true,
        enableNew: false,
        pageHeader:  `${i18n.$t('Trigger Manage')}`
      }
    },
    props: {},
    methods: {
      ...mapActions('dag', ['getReceiver']),
      /**
       * get emial
       */
      _getReceiver (id) {
        return new Promise((resolve, reject) => {
          this.getReceiver({ processDefinitionId: id }).then(res => {
            resolve({
              receivers: res.receivers && res.receivers.split(',') || [],
              receiversCc: res.receiversCc && res.receiversCc.split(',') || []
            })
          })
        })
      },
      /**
       * timing
       */
      _create () {
        let self = this
        this._getReceiver(this.processDefId).then(res => {
          let modal = this.$modal.dialog({
            closable: false,
            showMask: true,
            escClose: true,
            className: 'v-modal-custom',
            transitionName: 'opacityp',
            render (h) {
              return h(mTiming, {
                on: {
                  onUpdate () {
                    self._onUpdate()
                    modal.remove()
                  },
                  close () {
                    modal.remove()
                  },
                  onJump (info) {
                    modal.remove()
                    let params = {
                       triProcessDefId: parseInt(info.processDefinitionId)
                      ,isPopUp:true
                    }
                    self.$router.push({ path: '/projects/event-trigger/group/list', query:{jumpParams:params}})
                  }
                },
                props: {
                  item: {processDefinitionId: self.processDefId
                  },
                  receiversD: res.receivers,
                  receiversCcD: res.receiversCc,
                  type: 'timing'
                }
              })
            }
          })
        })
      },
      _onUpdate(){
        this.listShow = false; //close show
        this.$nextTick(function () { //reopen after dom of close end
          this.listShow = true;
        })
      },
      _onParent(info){
        if(info){
          this.enableNew = info.enableNew || false
        }
      }
    },
    watch: {
    },
    created () {
      if(this.$route.params && this.$route.params.id){
        this.processDefId = this.$route.params.id
      }
    },
    mounted () {
    },
    components: { mList, mListConstruction, mSecondaryMenu, mConditions }
  }
</script>
