package mango.core;

/**
 * @author Ricky Fung
 */
public abstract class AbstractResponseFuture<T> implements ResponseFuture<T> {

    protected volatile FutureState state = FutureState.NEW; //状态

    protected final long createTime = System.currentTimeMillis();//处理开始时间

    protected long timeoutInMillis;

    public AbstractResponseFuture(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    @Override
    public boolean isCancelled() {
        return this.state == FutureState.CANCELLED;
    }

    @Override
    public boolean isDone() {
        return this.state == FutureState.DONE;
    }

    @Override
    public boolean isTimeout() {
        return createTime + timeoutInMillis > System.currentTimeMillis();
    }
}
