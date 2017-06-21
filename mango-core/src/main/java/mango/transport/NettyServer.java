package mango.transport;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface NettyServer {

    boolean open();

    boolean isAvailable();

    void shutdown();

}
