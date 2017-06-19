# HRPC
## Overview
HRPC is a high-performance, open-source java RPC framework. 

## Features
* Supports various serialization protocol, like protostuff, kryo, hessian, msgpack, jackson.
* Supports service discovery services like ZooKeeper or Consul.
* Supports oneway, synchronous or asynchronous invoking.
* Easy integrated with Spring Framework 4.x.

## Quick Start

### 1. Synchronous calls
1. Add dependencies to pom.
```
    <dependency>
        <groupId>com.mindflow</groupId>
        <artifactId>hrpc-core</artifactId>
        <version>1.0.0</version>
    </dependency>

    <dependency>
        <groupId>com.mindflow</groupId>
        <artifactId>hrpc-registry-zk</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.mindflow</groupId>
        <artifactId>hrpc-serializer-protostuff</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.mindflow</groupId>
        <artifactId>hrpc-springsupport</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>4.3.6</version>
    </dependency>
```

2. Create an interface for both service provider and consumer.
```
package com.mindflow.rpc.demo.service;

public interface DemoService {

    void hello(String msg);

    String echo(String msg);

    Map<String, String> introduce(String name, List<String> hobbies);
}
```

3. Write an implementation, create and start RPC Server.
```
@Service("demoService")
public class DemoServiceImpl implements DemoService {

    @Override
    public void hello(String msg) {
        System.out.println(msg);
    }

    @Override
    public String echo(String msg) {
        return "hello, "+msg;
    }

    @Override
    public Map<String, String> introduce(String name, List<String> hobbies) {
        System.out.println("name:"+name + ", hobbies:"+hobbies);
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        return map;
    }

}
```

rpc-provider.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:hrpc="http://code.mindflow.com/schema/hrpc"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.mindflow.com/schema/hrpc http://code.mindflow.com/schema/hrpc/hrpc.xsd
       http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
    <context:component-scan base-package="com.mindflow.rpc.demo"/>

    <hrpc:protocol name="hrpc" port="21918"/>

    <hrpc:registry protocol="zookeeper" address="127.0.0.1:2181" timeout="5000" />

    <!-- -->
    <bean id="rpcServer" class="com.mindflow.framework.rpc.config.springsupport.RpcServer" />

    <hrpc:service interface="com.mindflow.rpc.demo.service.DemoService" ref="demoService" group="group1" version="1.0.0" />
    <hrpc:service interface="com.mindflow.rpc.demo.service.UserService" ref="userService" version="1.0.0" />

</beans>
```

ServerApp.java
```
public class ServerApp {

    public static void main( String[] args ) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:rpc-provider.xml");
        System.out.println("server start...");
    }
}
```

4. Create and start RPC Client.
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:util="http://www.springframework.org/schema/util"
       xmlns:hrpc="http://code.mindflow.com/schema/hrpc"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.mindflow.com/schema/hrpc http://code.mindflow.com/schema/hrpc/hrpc.xsd
       http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
    <context:component-scan base-package="com.mindflow.rpc.demo"/>

    <hrpc:protocol name="hrpc" port="21918" serialization="hessian"/>

    <hrpc:registry protocol="zookeeper" address="127.0.0.1:2181" timeout="5000" />

    <!--引用服务-->
    <hrpc:reference id="demoService" interface="com.mindflow.rpc.demo.service.DemoService" group="group1" />
    <hrpc:reference id="userService" interface="com.mindflow.rpc.demo.service.UserService"/>

</beans>
```

ClientApp.java
```
public class ClientApp {

    public static void main( String[] args ) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:rpc-consumer.xml");

        DemoService service = (DemoService) ctx.getBean("demoService");

        service.hello("rpc");
        System.out.println("echo:"+service.echo("rpc"));

        List<String> hobbies = new ArrayList<>();
        hobbies.add("NBA");
        hobbies.add("读书");
        Map<String, String> map = service.introduce("hh", hobbies);
        System.out.println("map:"+map);
    }
}
```

### 2. Asynchronous calls
TODO
