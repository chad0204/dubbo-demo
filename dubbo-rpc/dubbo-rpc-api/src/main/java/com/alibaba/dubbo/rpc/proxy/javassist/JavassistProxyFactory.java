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
package com.alibaba.dubbo.rpc.proxy.javassist;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.Proxy;
import com.alibaba.dubbo.common.bytecode.Wrapper;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;

/**
 * JavassistRpcProxyFactory
 */
public class JavassistProxyFactory extends AbstractProxyFactory {

    /**
     * package org.apache.dubbo.common.bytecode;
     *
     * public class proxy0 implements org.apache.dubbo.demo.DemoService {
     *
     *     public static java.lang.reflect.Method[] methods;
     *
     *     private java.lang.reflect.InvocationHandler handler;
     *
     *     public proxy0() {
     *     }
     *
     *     public proxy0(java.lang.reflect.InvocationHandler arg0) {
     *         handler = $1;
     *     }
     *
     *     public java.lang.String sayHello(java.lang.String arg0) {
     *         Object[] args = new Object[1];
     *         args[0] = ($w) $1;
     *         Object ret = handler.invoke(this, methods[0], args);
     *         return (java.lang.String) ret;
     *     }
     *  }
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        // 生成 Proxy 子类（Proxy 是抽象类）。并调用 Proxy 子类的 newInstance 方法创建 Proxy 实例
        return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }

    /**
     *
     *
     * public Object invokeMethod(Object object, String string, Class[] arrclass, Object[] arrobject) throws InvocationTargetException {
     *      DemoService demoService;
     *      try {
     *          // 类型转换
     *          demoService = (DemoService)object;
     *      } catch (Throwable throwable) {
     *          throw new IllegalArgumentException(throwable);
     *      }
     *      try {
     *          // 根据方法名调用指定的方法
     *          if ("sayHello".equals(string) && arrclass.length == 1) {
     *              return demoService.sayHello((String)arrobject[0]);
     *          }
     *          if( "sayGood".equals( $2 )  &&  $3.length == 0 ) {
     *              return ($w)w.sayGood();
     *          }
     *          if( "groupList".equals( $2 )  &&  $3.length == 0 ) {
     *              return ($w)w.groupList();
     *          }
     *
     *      } catch (Throwable throwable) {
     *          throw new InvocationTargetException(throwable);
     *      }
     *      throw new NoSuchMethodException(new StringBuffer().append("Not found method \"").append(string).append("\" in class com.alibaba.dubbo.demo.DemoService.").toString());
     * }
     *
     *
     * 使用动态代理是性能考虑，请求过来，直接根据方法名，参数调用代理类，代理类调用正在的实现类，省去了每次调用都需要反射。
     *
     * @param proxy impl对象
     * @param type 接口类型
     * @param url url
     * @return
     */
    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        // TODO Wrapper cannot handle this scenario correctly: the classname contains '$'
        // 为目标类创建 Wrapper
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);
        // 创建匿名 Invoker 类对象，并实现 doInvoke 方法。
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                // 调用 Wrapper 的 invokeMethod 方法，invokeMethod 最终会调用目标方法（消费端调用）
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }

}
