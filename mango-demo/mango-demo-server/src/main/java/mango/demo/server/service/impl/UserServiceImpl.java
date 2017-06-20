package mango.demo.server.service.impl;

import mango.demo.model.User;
import mango.demo.service.UserService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public Long insert(User user) {
        System.out.println("insert user"+user);
        return 15L;
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
