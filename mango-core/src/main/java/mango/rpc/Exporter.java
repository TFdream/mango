package mango.rpc;

import mango.common.URL;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Exporter<T> {

    URL getUrl();

    Provider<T> getProvider();

    void close();
}
