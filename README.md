# Mango
## Overview
Mango is a high-performance, open-source java RPC framework. 

## Features
* Supports various serialization protocol, like [protostuff](http://protostuff.io), Kryo, Hessian, msgpack, Jackson, Fastjson.
* Netty 4.1 as transport layer framework.
* Supports service discovery services like ZooKeeper or Consul.
* Supports oneway, synchronous or asynchronous invoking.
* Easy integrated with Spring Framework 4.x.

## Quick Start

### 1. Synchronous calls
1. Add dependencies to pom.
```
    <dependency>
        <groupId>com.mindflow</groupId>
        <artifactId>mango-core</artifactId>
        <version>1.0.0</version>
    </dependency>

    <dependency>
        <groupId>com.mindflow</groupId>
        <artifactId>mango-registry-zk</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- dependencies blow were only needed for spring integrated -->
    <dependency>
        <groupId>com.mindflow</groupId>
        <artifactId>mango-springsupport</artifactId>
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

mango-server.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mango="http://code.mindflow.com/schema/mango"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.mindflow.com/schema/mango http://code.mindflow.com/schema/mango/mango.xsd
       http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
    <context:component-scan base-package="mango.demo"/>

    <mango:application name="mango-server" />
    <mango:protocol name="mango" port="21918"/>

    <mango:registry protocol="zookeeper" address="10.141.5.49:2181" connect-timeout="2000" session-timeout="60000" />

    <!--export services-->
    <mango:service interface="mango.demo.service.DemoService" ref="demoService" group="group1" version="1.0.0" />
    <mango:service interface="mango.demo.service.UserService" ref="userService" version="1.0.0" />

</beans>
```

ServerApp.java
```
public class ServerApp {

    public static void main( String[] args ) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:mango-server.xml");
        System.out.println("server start...");
    }
}
```

4. Create and start RPC Client.
mango-client.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:util="http://www.springframework.org/schema/util"
       xmlns:mango="http://code.mindflow.com/schema/mango"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.mindflow.com/schema/mango http://code.mindflow.com/schema/mango/mango.xsd
       http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
    <context:component-scan base-package="mango.demo.client"/>

    <mango:application name="mango-client" />
    <mango:protocol name="mango" port="21918"/>

    <mango:registry protocol="zookeeper" address="10.141.5.49:2181" connect-timeout="5000" />

    <!--refer services-->
    <mango:reference id="demoService" interface="mango.demo.service.DemoService" group="group1" />
    <mango:reference id="userService" interface="mango.demo.service.UserService"/>

</beans>
```

ClientApp.java
```
public class ClientApp {

    public static void main( String[] args ) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:mango-client.xml");

        DemoService service = (DemoService) ctx.getBean("demoService");

        service.hello("rpc");
        System.out.println("echo:"+service.echo("rpc"));

        List<String> hobbies = new ArrayList<>();
        hobbies.add("NBA");
        hobbies.add("Reading");
        Map<String, String> map = service.introduce("hh", hobbies);
        System.out.println("map:"+map);
    }
}
```

### 2. Asynchronous calls
TODO
