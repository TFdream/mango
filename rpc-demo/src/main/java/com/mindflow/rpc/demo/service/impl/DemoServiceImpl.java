package com.mindflow.rpc.demo.service.impl;

import com.mindflow.framework.rpc.annotation.RpcService;
import com.mindflow.rpc.demo.model.User;
import com.mindflow.rpc.demo.service.DemoService;
import java.util.List;

/**
 * @author Ricky Fung
 */
@RpcService(DemoService.class)
public class DemoServiceImpl implements DemoService {

    @Override
    public void hello(String msg) {

    }

    @Override
    public String echo(String msg) {
        return null;
    }

    @Override
    public List<User> getUsers(int age) {
        return null;
    }

    @Override
    public int update(User user) {
        return 0;
    }
}
