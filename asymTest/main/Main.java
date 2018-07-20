package main;

import impl.DefaultProducer;
import impl.db.AutoCompleteConsumer;
import impl.db.AutoCompleteTreeSet;
import interfaces.AutoCompleteDB;
import interfaces.Candidate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class Main {

    /**
     *
     * @param args none
     */
    public static void main(String[] args) {
        //queue to add candidates to
        BlockingQueue<Candidate> queue = new LinkedTransferQueue<>();
        //intialize the db
        AutoCompleteDB db = AutoCompleteTreeSet.getDefault();
        //create the consumer and producers
        AutoCompleteConsumer consumer = new AutoCompleteConsumer(queue, db);
        DefaultProducer producer = new DefaultProducer(queue, db);
        //have the consumer thread listen for changes in running from the producer
        producer.addStateListener(consumer.getStateListener());
        //run the consumer thread on it's own thread
        Thread thread1 = new Thread(consumer, "Consumer 1");
        thread1.start();
        //run the producer on this thread
        producer.run();
    }
}
