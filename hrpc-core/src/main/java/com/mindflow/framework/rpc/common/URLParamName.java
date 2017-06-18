package com.mindflow.framework.rpc.common;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public enum URLParamName {
    /** version **/
    version("version", "1.0.0"),
    /** group **/
    group("group", "default_rpc"),

    /** request timeout **/
    requestTimeout("requestTimeout", 200),
    /** connect timeout **/
    connectTimeout("connectTimeout", 1000),
    /** service min worker threads **/
    minWorkerThread("minWorkerThread", 20),
    /** service max worker threads **/
    maxWorkerThread("maxWorkerThread", 200),


    /**zookeeper**/
    registrySessionTimeout("registrySessionTimeout", 60*1000),
    registryConnectTimeout("registryConnectTimeout", 2000),

    /** serialize **/
    serializer("serializer", "protostuff"),
    /** codec **/
    codec("codec", "hrpc");

    private String name;
    private String value;
    private long longValue;
    private int intValue;
    private boolean boolValue;

    URLParamName(String name, String value) {
        this.name = name;
        this.value = value;
    }

    URLParamName(String name, long longValue) {
        this.name = name;
        this.value = String.valueOf(longValue);
        this.longValue = longValue;
    }

    URLParamName(String name, int intValue) {
        this.name = name;
        this.value = String.valueOf(intValue);
        this.intValue = intValue;
    }

    URLParamName(String name, boolean boolValue) {
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
