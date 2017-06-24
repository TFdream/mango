package mango.demo.client;

import mango.demo.service.DemoService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mango-client.xml"})
public class DemoServiceTest {

    @Resource(name = "demoService")
    private DemoService demoService;

    @Test
    @Ignore
    public void testHello() {

        demoService.hello("rpc");
    }

    @Test
    @Ignore
    public void testEcho() {

        System.out.println("echo:"+demoService.echo("rpc"));
    }

    @Test
    @Ignore
    public void testIntroduce() {

        List<String> hobbies = new ArrayList<>();
        hobbies.add("NBA");
        hobbies.add("读书");
        Map<String, String> map = demoService.introduce("hh", hobbies);
        System.out.println("map:"+map);
    }
}
