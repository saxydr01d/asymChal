package interfaces;

/**
 * a thread that has a changeable state, it can have other
 * threads listen to its state
 */
public interface StateChangeable {
    void addStateListener(StateListener s);

    void removeStateListener(StateListener s);
}
