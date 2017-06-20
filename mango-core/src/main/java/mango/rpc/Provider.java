package mango.rpc;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;

/**
 * @author Ricky Fung
 */
public interface Provider<T> {

    URL getUrl();

    Class<T> getInterface();

    Response call(Request request);
}
