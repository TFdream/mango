package mango.transport;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface NettyServer {

    void bind() throws InterruptedException;

    void shutdown();

}
