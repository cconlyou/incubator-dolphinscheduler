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
  <div class="line-box">
    <span class="line"></span>
    <span class="text"><slot name="name"></slot></span>
    <span class="line"></span>
  </div>
</template>
<script>
  export default {
    name: 'divider'
  }
</script>

<style lang="scss" rel="stylesheet/scss">
.line-box{
  display: flex;
  justify-content: space-between;
  align-items: center;
  //margin: -15px 0px 0px 0px;
}
.text{
  //min-width: 120px;
  //font-size: 10px;
  text-align: center;
}
.line{
  width: 40%;
  height: 1px;
  background-color: #ccc;
}
</style>
