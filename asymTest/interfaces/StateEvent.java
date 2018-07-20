package interfaces;

/**
 * an event signalling there has been a
 * change in the state of the thread
 */
public class StateEvent {

    public enum STATE{
        QUIT, START;
    }

    private STATE state;

    public StateEvent(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return state;
    }
}
