package mango.demo.client;

import mango.demo.service.FooService;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Ricky Fung
 */
public class MethodTest {

    @Test
    public void testException() throws NoSuchMethodException {

        Class<?> clz = FooService.class;
        Method method = clz.getMethod("order", String.class);

        System.out.println("returnType:"+method.getReturnType());

        Class<?>[] exceptionTypes = method.getExceptionTypes();
        System.out.println("exceptionTypes:"+ Arrays.toString(exceptionTypes));

        method = clz.getMethod("hello", String.class);
        System.out.println("returnType:"+method.getReturnType());

        exceptionTypes = method.getExceptionTypes();
        System.out.println("exceptionTypes:"+Arrays.toString(exceptionTypes));
    }
}
