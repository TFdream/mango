package mango.util;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class Constants {

    public static final String FRAMEWORK_NAME = "mango";

    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String PATH_SEPARATOR = "/";

    public static final String ZOOKEEPER_REGISTRY_NAMESPACE = "/mango";

    //头部信息的大小应该是 short+byte+long+int = 2+1+8+4 = 15
    public static final int HEADER_SIZE = 15;

    public static final short NETTY_MAGIC_TYPE = (short) 0x9F9F;

    public static final byte FLAG_REQUEST = 0x01;
    public static final byte FLAG_RESPONSE = 0x03;
    public static final byte FLAG_OTHER = (byte) 0xFF;


    public static final byte REQUEST_ONEWAY = 0x03;
    public static final byte REQUEST_SYNC = 0x05;
    public static final byte REQUEST_ASYNC = 0x07;

    public static final int DEFAULT_PORT = 21918;

    public static final int DEFAULT_INT_VALUE = 0;

    public static final String REGISTRY_PROTOCOL_LOCAL = "local";

    public static final String HOST_PORT_SEPARATOR = ":";

    public static final String DEFAULT_VALUE = "default";

    public static final String PROVIDER = "provider";
    public static final String CONSUMER = "consumer";

}
