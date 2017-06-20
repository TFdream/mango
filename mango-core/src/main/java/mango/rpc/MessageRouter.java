package mango.rpc;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;
import mango.core.extension.SPI;
import mango.util.Constants;

/**
 * @author Ricky Fung
 */
@SPI(Constants.DEFAULT_VALUE)
public interface MessageRouter {

    Response handle(Request request);

    <T> Exporter<T> register(Provider<T> provider, URL url);
}
