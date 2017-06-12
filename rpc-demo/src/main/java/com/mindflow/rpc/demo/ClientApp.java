package com.mindflow.rpc.demo;

import com.mindflow.framework.rpc.client.RpcClient;
import com.mindflow.rpc.demo.service.DemoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class ClientApp {

    public static void main( String[] args ) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:rpc-consumer.xml");

        RpcClient rpcClient = (RpcClient) ctx.getBean("rpcClient");

        DemoService service = rpcClient.create(DemoService.class);

        service.hello("rpc");
        System.out.println(service.echo("rpc"));
    }
}
