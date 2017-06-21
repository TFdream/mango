package mango.rpc;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;

/**
 * RPC调用方
 *
 * @author Ricky Fung
 */
public interface Invoker<T> extends Node {

    Class<T> getInterface();

    Response call(Request request);

    URL getServiceUrl();
}
