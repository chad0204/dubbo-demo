/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.demo.DemoService;
import com.alibaba.dubbo.rpc.RpcContext;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {

        String secret1 = RpcContext.getContext().getAttachment("secret1");

        String secret2 = RpcContext.getContext().getAttachment("secret2");

        String secret3 = RpcContext.getContext().getAttachment("secret3");



        System.out.println(secret1+secret2+secret3);//secret1+secret2+null
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response from provider: " + RpcContext.getContext().getLocalAddress();
    }




    @Override
    public List<String> groupList() {
        return Arrays.asList("aa","bb");
    }

    @Override
    public String sayGood() {

        String secret1 = RpcContext.getContext().getAttachment("secret1");

        String secret2 = RpcContext.getContext().getAttachment("secret2");//

        String secret3 = RpcContext.getContext().getAttachment("secret3");



        System.out.println(secret1+secret2+secret3);//null+null+secret3

        return "good from DemoServiceImpl";
    }

}
