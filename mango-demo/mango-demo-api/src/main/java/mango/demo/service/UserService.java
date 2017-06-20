package mango.demo.service;

import mango.demo.model.User;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface UserService {

    Long insert(User user);

    List<User> getUsers(int age);

    int update(User user);
}
