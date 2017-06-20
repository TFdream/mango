package mango.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultRequest implements Serializable, Request {

    private static final long serialVersionUID = 7478520607109127572L;

    private Long requestId;
    private String interfaceName;
    private String methodName;
    private Object[] arguments;
    private Class<?>[] parameterTypes;
    private byte type;  //请求类型
    private Map<String, String> attachments;

    @Override
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    @Override
    public Map<String, String> getAttachments() {
        return attachments != null ? attachments : Collections.EMPTY_MAP;
    }

    @Override
    public String getAttachment(String key) {
        return attachments.get(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        return attachments.containsKey(key) ? attachments.get(key) : defaultValue;
    }

    @Override
    public void setAttachment(String key, String value) {
        if(attachments==null){
            attachments = new HashMap<>();
        }
        attachments.put(key, value);
    }

}
