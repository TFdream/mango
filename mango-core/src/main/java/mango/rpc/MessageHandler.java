package mango.rpc;

import mango.core.Request;
import mango.core.Response;
import mango.core.extension.SPI;
import mango.util.Constants;

/**
 * @author Ricky Fung
 */
public interface MessageHandler {

    Response handle(Request request);

}
