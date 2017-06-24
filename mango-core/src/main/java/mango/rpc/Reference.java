package mango.rpc;

import mango.common.URL;

/**
 * reference to a service
 *
 * @author Ricky Fung
 */
public interface Reference<T> extends Caller<T> {

    URL getServiceUrl();
}
