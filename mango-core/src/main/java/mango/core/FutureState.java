package mango.core;

/**
 * @author Ricky Fung
 */
public enum FutureState {
    /** the task is doing **/
    NEW(0),
    /** the task is done **/
    DONE(1),
    /** ths task is cancelled **/
    CANCELLED(2);

    private int state;

    FutureState(int state) {
        this.state = state;
    }
}
