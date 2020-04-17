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

import io from '@/module/io'

export default {
    /**
   * Get trigger group list
   */
  getTriGroupList ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.get(`projects/${state.projectName}/event-trigger/group/list`, payload, res => {
        resolve(res.data)
      }).catch(e => {
        reject(e)
      })
    })
  },
  /**
   * Get trigger group list
   */
  getTriGroupPageList ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.get(`projects/${state.projectName}/event-trigger/group/list-paging`, payload, res => {
        resolve(res.data)
      }).catch(e => {
        reject(e)
      })
    })
  },
  /**
   * Create event trigger groups
   */
  createGroups ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.post(`projects/${state.projectName}/event-trigger/group/create`, payload, res => {
        resolve(res)
      }).catch(e => {
        reject(e)
      })
    })
  },
  /**
   * Delete event trigger groups
   */
  deleteGroups ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.get(`projects/${state.projectName}/event-trigger/group/delete`, payload, res => {
        resolve(res)
      }).catch(e => {
        reject(e)
      })
    })
  },

  /**
   * edit Group
   */
  updateGroups ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.post(`projects/${state.projectName}/event-trigger/group/update`, payload, res => {
        resolve(res)
      }).catch(e => {
        reject(e)
      })
    })
  },
  /**
   * edit Group
   */
  saveMembers ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.post(`projects/${state.projectName}/event-trigger/member/save-batch`, payload, res => {
        resolve(res)
      }).catch(e => {
        reject(e)
      })
    })
  },
  /**
   * Get trigger group member list
   */
  getTriGrpMbrList ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.get(`projects/${state.projectName}/event-trigger/group-member/list-paging`, payload, res => {
        resolve(res.data)
      }).catch(e => {
        reject(e)
      })
    })
  },
  /**
   * Get a list of members by group name
   */
  getMemberByGroupInfo ({ state }, payload) {
    return new Promise((resolve, reject) => {
      io.get(`projects/${state.projectName}/event-trigger/group-member/list`, payload, res => {
        resolve(res.data)
      }).catch(res => {
          reject(res)
        })
    })
  }
}
