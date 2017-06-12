package com.mindflow.framework.rpc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultRequest implements Request {
    private Long requestId;
    private String className;
    private String methodName;
    private Object[] args;
    private Class<?>[] parameterTypes;
    private Map<String, String> attachments;

    @Override
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
        return args;
    }

    public void setArguments(Object[] args) {
        this.args = args;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
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
