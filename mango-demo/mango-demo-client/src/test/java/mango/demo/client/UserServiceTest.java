package mango.demo.client;

import mango.demo.model.User;
import mango.demo.service.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mango-client.xml"})
public class UserServiceTest {

    @Resource(name = "userService")
    private UserService userService;

    @Test
    @Ignore
    public void testInsert() {

        System.out.println(userService.insert(new User()));
    }

    @Test
    @Ignore
    public void testGetUsers() {

        List<User> users = userService.getUsers(28);
        System.out.println("users:"+users);
    }

}
