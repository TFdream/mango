package mango.registry.zookeeper;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public enum ZkNodeType {
    SERVER("providers"),
    CLIENT("consumers");

    private String value;

    ZkNodeType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
