package mango.rpc;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Exporter<T> extends Node {

    Provider<T> getProvider();

    void unexport();
}
