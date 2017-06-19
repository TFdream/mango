package com.mindflow.rpc.demo.service.impl;

import com.mindflow.framework.rpc.annotation.RpcService;
import com.mindflow.rpc.demo.model.User;
import com.mindflow.rpc.demo.service.DemoService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ricky Fung
 */
@RpcService(DemoService.class)
public class DemoServiceImpl implements DemoService {
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public void hello(String msg) {
        System.out.println(msg);
    }

    @Override
    public String echo(String msg) {
        return "hello, "+msg;
    }

    @Override
    public List<User> getUsers(int age) {

        List<User> users = new ArrayList<>();
        for(int i=0; i<5; i++) {
            User user = new User();
            user.setId(counter.getAndIncrement());
            user.setName("ricky_"+user.getId());
            user.setPassword("root");
            user.setAge(age+i);
            users.add(user);
        }
        return users;
    }

    @Override
    public int update(User user) {
        System.out.println("update user:"+user);
        return 1;
    }
}
