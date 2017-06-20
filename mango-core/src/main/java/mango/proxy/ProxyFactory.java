package mango.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface ProxyFactory {

    <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler);
}
