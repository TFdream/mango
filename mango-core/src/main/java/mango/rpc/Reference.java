package mango.rpc;

import mango.common.URL;

/**
 * reference to a service
 *
 * @author Ricky Fung
 */
public interface Reference<T> extends Caller<T> {

    /**
     * 当前Reference的调用次数
     * @return
     */
    int activeCount();

    URL getServiceUrl();
}
