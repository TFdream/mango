package mango.demo.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Ricky Fung
 */
public class ServerApp {

    public static void main( String[] args ) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:mango-server.xml");
        System.out.println("server start...");
    }
}
