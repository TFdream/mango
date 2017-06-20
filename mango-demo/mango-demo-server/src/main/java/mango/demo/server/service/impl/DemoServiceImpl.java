package mango.demo.server.service.impl;

import mango.demo.service.DemoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ricky Fung
 */
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
