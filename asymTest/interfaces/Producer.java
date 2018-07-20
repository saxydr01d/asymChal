package interfaces;

/**
 * produces objects of type t to be consumed by another thread
 * usually these are added to a queue in the producer and read
 * from the queue on the consumer side.
 *
 * @param <T> type of object to produce
 */
public interface Producer<T> extends Runnable {

    T produce();
}
