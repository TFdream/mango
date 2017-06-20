package mango.demo.client;

import mango.demo.model.User;
import mango.demo.service.DemoService;
import mango.demo.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
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

        System.out.println("*****************************");

        UserService userService = (UserService) ctx.getBean("userService");
        System.out.println(userService.insert(new User()));

        List<User> users = userService.getUsers(28);
        System.out.println("users:"+users);

    }
}
