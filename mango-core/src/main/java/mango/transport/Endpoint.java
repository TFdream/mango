package mango.transport;

import mango.common.URL;
import java.net.InetSocketAddress;

/**
 * @author Ricky Fung
 */
public interface Endpoint {

    /**
     * get local socket address.
     *
     * @return local address.
     */
    InetSocketAddress getLocalAddress();

    /**
     * get remote socket address
     *
     * @return
     */
    InetSocketAddress getRemoteAddress();

    boolean open();

    boolean isAvailable();

    boolean isClosed();

    URL getUrl();

    void close();

    /**
     * close the channel gracefully.
     */
    void close(int timeout);
}
