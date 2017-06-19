package com.mindflow.rpc.demo.service;

import com.mindflow.rpc.demo.model.User;
import java.util.List;

/**
 * @author Ricky Fung
 */
public interface DemoService {

    void hello(String msg);

    String echo(String msg);

    List<User> getUsers(int age);

    int update(User user);
}
