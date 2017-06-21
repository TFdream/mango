package mango.rpc;

import mango.common.URL;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Node {

    void init();

    void destroy();

    boolean isAvailable();

    String desc();

    URL getUrl();
}
