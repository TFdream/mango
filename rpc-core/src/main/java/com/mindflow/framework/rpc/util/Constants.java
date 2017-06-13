package com.mindflow.framework.rpc.util;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class Constants {

    public static final String FRAMEWORK_NAME = "rpc";

    public static final int MAX_FRAME_LENGTH = 1<<20;

    //头部信息的大小应该是 short+byte+long+int = 2+1+8+4 = 15
    public static final int HEADER_SIZE = 15;

    public static final short NETTY_MAGIC_TYPE = (short) 0x9F9F;

    public static final byte FLAG_REQUEST = 0x01;
    public static final byte FLAG_RESPONSE = 0x03;
    public static final byte FLAG_OTHER = (byte) 0xFF;


    public static final byte REQUEST_ONEWAY = 0x03;
    public static final byte REQUEST_SYNC = 0x05;
    public static final byte REQUEST_ASYNC = 0x07;
}
