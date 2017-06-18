package com.mindflow.framework.rpc.serializer.fastjson;

import com.alibaba.fastjson.JSON;
import com.mindflow.framework.rpc.serializer.Serializer;
import com.mindflow.framework.rpc.util.StringUtils;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class FastjsonSerializer implements Serializer {

    @Override
    public byte[] serialize(Object msg) throws IOException {
        String jsonString = JSON.toJSONString(msg);
        return StringUtils.getBytes(jsonString);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> type) throws IOException {
        String jsonString = StringUtils.getString(data);
        return JSON.parseObject(jsonString, type);
    }
}
