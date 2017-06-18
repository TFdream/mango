package com.mindflow.framework.rpc.config;

/**
 * @author Ricky Fung
 */
public class RegistryConfig {
    private String protocol;
    private String address;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
