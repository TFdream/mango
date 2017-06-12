package com.mindflow.framework.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ricky Fung
 */
public class RpcContext {
    private Map<Object, Object> attribute = new HashMap<Object, Object>();
    private Request request;
    private Response response;
    private String requestId;

    private static final ThreadLocal<RpcContext> localContext = new ThreadLocal<RpcContext>() {
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return localContext.get();
    }

    public static void reset() {
        localContext.remove();
    }

    public void putAttribute(Object key, Object value){
        attribute.put(key, value);
    }

    public Object getAttribute(Object key) {
        return attribute.get(key);
    }

    public void revomeAttribute(Object key){
        attribute.remove(key);
    }
}
