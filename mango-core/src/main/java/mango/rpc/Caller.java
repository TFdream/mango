package mango.rpc;

import mango.core.Request;
import mango.core.Response;

/**
 *
 * @author Ricky Fung
 */
public interface Caller<T> extends Node {

    Class<T> getInterface();

    Response call(Request request);

}
