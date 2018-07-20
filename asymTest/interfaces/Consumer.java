package interfaces;

/**
 * consumes objects from a queue and adds them to a database
 *
 * @param <T> object to be added to the database
 */
public interface Consumer<T> extends Runnable {
    void consume(T object);
}
