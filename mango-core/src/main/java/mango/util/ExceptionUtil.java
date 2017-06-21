package mango.util;

import mango.exception.RpcBizException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ExceptionUtil {

    /**
     * 判定是否是业务方的逻辑抛出的异常
     *
     * <pre>
     * 		true: 来自业务方的异常
     * 		false: 来自框架本身的异常
     * </pre>
     *
     * @param e
     * @return
     */
    public static boolean isBizException(Exception e) {
        return e instanceof RpcBizException;
    }
}
