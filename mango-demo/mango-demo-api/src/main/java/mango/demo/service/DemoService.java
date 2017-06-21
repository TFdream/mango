package mango.demo.service;

import java.util.List;
import java.util.Map;

/**
 * @author Ricky Fung
 */
public interface DemoService {

    void hello(String msg);

    String echo(String msg);

    Map<String, String> introduce(String name, List<String> hobbies);

}
