package mango.common;

import mango.util.Constants;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public enum URLParam {

    application("application", ""),
    /** version **/
    version("version", "1.0.0"),
    /** group **/
    group("group", "default_rpc"),

    protocol("protocol", Constants.FRAMEWORK_NAME),
    path("path", ""),
    host("host", ""),
    port("port", 0),

    /** request timeout **/
    requestTimeout("timeout", 500),
    /** connect timeout **/
    connectTimeout("connectTimeout", 1000),
    /** service min worker threads **/
    minWorkerThread("minWorkerThread", 20),
    /** service max worker threads **/
    maxWorkerThread("maxWorkerThread", 200),

    /**netty**/
    maxContentLength("maxContentLength", 1<<24),

    bufferSize("buffer_size", 1024*16),

    loadBalance("loadbalance", "random"),
    haStrategy("haStrategy", "failfast"),

    check("check", true),
    retries("retries", 0),

    proxyType("proxy", "jdk"),

    /**Registry**/
    registryProtocol("reg_protocol", "local"),
    registryAddress("reg_address", "localhost"),

    registrySessionTimeout("reg_session_timeout", 60*1000),
    registryConnectTimeout("reg_connect_timeout", 5000),

    side("side", ""),
    timestamp("timestamp", 0),

    /** serialize **/
    serialization("serialization", "protostuff"),
    /** codec **/
    codec("codec", Constants.FRAMEWORK_NAME);

    private String name;
    private String value;
    private long longValue;
    private int intValue;
    private boolean boolValue;

    URLParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    URLParam(String name, long longValue) {
        this.name = name;
        this.value = String.valueOf(longValue);
        this.longValue = longValue;
    }

    URLParam(String name, int intValue) {
        this.name = name;
        this.value = String.valueOf(intValue);
        this.intValue = intValue;
    }

    URLParam(String name, boolean boolValue) {
        this.name = name;
        this.value = String.valueOf(boolValue);
        this.boolValue = boolValue;
    }

    public String getName() {
        return name;
    }

    public long getLongValue() {
        return longValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public boolean isBoolValue() {
        return boolValue;
    }

    public String getValue() {
        return value;
    }
}
