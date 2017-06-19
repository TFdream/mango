package com.mindflow.framework.rpc.config;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ReferenceConfig<T> extends AbstractInterfaceConfig {

    protected transient volatile T proxy;

    public T getProxy() {
        return proxy;
    }

    public void setProxy(T proxy) {
        this.proxy = proxy;
    }
}
