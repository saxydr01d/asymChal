package impl.db;

import interfaces.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * listens to a queue of {@link Candidate} objects to be added to an {@link AutoCompleteDB}.
 * Runs as its own thread until it receives a {@link StateEvent} stating it should quit
 *
 * @author Trevor Grabko
 */
public class AutoCompleteConsumer implements Consumer<Candidate> {

    /** queue containing candidates to add to the db */
    private BlockingQueue<Candidate> queue;
    /** db to add candidates to */
    private AutoCompleteDB db;
    /** whether or not this consumer should run */
    private boolean run;
    /** listening for changes to the state of the program */
    private StateListener listener;

    /**
     * create a new consumer that adds candidates to a db
     * @param queue queue that will contain potential candidates
     * @param db db to add candidates to
     */
    public AutoCompleteConsumer(BlockingQueue<Candidate> queue, AutoCompleteDB db) {
        this.queue = queue;
        this.db = db;
        run = false;
    }

    /**
     * listens for changes to an {@link BlockingQueue} and adds those
     * new objects to a database while the thread is allowed to run
     */
    public void consumer() {
        try
        {
            while(run) {
                //poll for candidates, timing out every 1 seconds
                Candidate c = queue.poll(1, TimeUnit.SECONDS);
                if(c != null) {
                    consume(c);
                }
            }
            System.out.println( Thread.currentThread().getName() + " has finished");
        } catch(InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " Interrupted");
        }
    }

    /**
     * @param consumable object to add to the database
     */
    @Override
    public void consume(Candidate consumable) {
        db.addCandidate(consumable);
    }

    @Override
    public void run() {
        //start the consumer when the thread runs
        run = true;
        consumer();
    }

    /**
     * @return a statelistener for this thread
     */
    public StateListener getStateListener() {
        if(listener == null) {
            listener = evt -> {
                if(evt.getState() == StateEvent.STATE.QUIT) {
                    run = false;
                }
            };
        }
        return listener;
    }
}
