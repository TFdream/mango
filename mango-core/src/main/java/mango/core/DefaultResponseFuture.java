package mango.core;

import mango.exception.RpcFrameworkException;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Ricky Fung
 */
public class DefaultResponseFuture<T> extends AbstractResponseFuture<T> {

    private volatile T result;
    private volatile Throwable err;
    private CountDownLatch latch;

    public DefaultResponseFuture(long timeoutInMillis) {
        super(timeoutInMillis);
    }

    @Override
    public T get() throws InterruptedException {
        if(!this.isDone()) {
            boolean wait = this.prepareForWait();
            if(wait) {
                this.latch.await();
            }
        }
        return returnResult();
    }

    @Override
    public boolean isSuccess() {
        return isDone() && err==null;
    }

    @Override
    public void setResult(T result) {
        synchronized(this) {
            if(!this.isDone()) {
                this.result = result;
                this.state = FutureState.DONE;
                if(this.latch != null) {
                    this.latch.countDown();
                }
            }
        }
    }

    @Override
    public void setFailure(Throwable throwable) {
        if(!(throwable instanceof IOException) && !(throwable instanceof SecurityException)) {
            throwable = new IOException(throwable);
        }

        synchronized(this) {
            if(!this.isDone()) {
                this.err = throwable;
                this.state = FutureState.DONE;
                if(this.latch != null) {
                    this.latch.countDown();
                }
            }
        }
    }

    private T returnResult() throws CancellationException {
        if(this.err != null) {
            if(this.state == FutureState.CANCELLED) {
                throw new CancellationException();
            } else {
                throw new RpcFrameworkException(this.err);
            }
        } else {
            return this.result;
        }
    }

    private boolean prepareForWait() {
        synchronized(this) {
            if(this.isDone()) {
                return false;
            } else {
                if(this.latch == null) {
                    this.latch = new CountDownLatch(1);
                }
                return true;
            }
        }
    }
}
