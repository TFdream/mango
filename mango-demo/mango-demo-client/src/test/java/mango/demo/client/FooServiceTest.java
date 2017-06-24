package mango.demo.client;

import mango.demo.service.FooService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mango-client.xml"})
public class FooServiceTest {

    @Resource(name = "fooService")
    private FooService fooService;

    @Test
    @Ignore
    public void testHello() {

        System.out.println(fooService.hello("mango"));
    }

    @Test
    @Ignore
    public void testOrder() {

        System.out.println(fooService.order("mango"));
    }

    @Test
    @Ignore
    public void testOrderException() {

        try {
            System.out.println(fooService.order(null));
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
