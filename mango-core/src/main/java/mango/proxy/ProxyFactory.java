package mango.proxy;

import mango.core.extension.SPI;
import java.lang.reflect.InvocationHandler;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI("jdk")
public interface ProxyFactory {

    <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler);
}
