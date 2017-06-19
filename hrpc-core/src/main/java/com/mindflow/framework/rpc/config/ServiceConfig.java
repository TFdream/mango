package com.mindflow.framework.rpc.config;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ServiceConfig<T> extends AbstractInterfaceConfig {
    private T ref;

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }
}
