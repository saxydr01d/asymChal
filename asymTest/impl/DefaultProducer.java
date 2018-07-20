package impl;

import impl.db.AutoCompleteTreeSet;
import interfaces.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

/**
 * Produces candidates from a scanner to be added to a database, can also read
 * from the database and get a list of potential autocomplete phrases
 *
 * @author Trevor Grabko
 */
public class DefaultProducer implements Producer<String>, StateChangeable, AutoCompleteProvider {

    /** possible commands, case insensitive and must be followed by ':' */
    private static final String TRAIN = "train";
    private static final String INPUT = "input";
    private static final String QUIT = "quit";
    private static final String PRINT = "print";

    /** scanner to read input */
    private Scanner scanner;

    /** compiled regexes */
    private static final Pattern split = Pattern.compile(":");
    private static final Pattern nonWordPatter = Pattern.compile("\\W");
    private static final Pattern splitPattern = Pattern.compile("\\s+");

    /** queue containing values to be consumed by a consumer and added to the db */
    private BlockingQueue<Candidate> queue;

    /** list of StateListener components, or components that listen for program state */
    private List<StateListener> listeners;

    /** true if the system is not quitting, false otherwise */
    private boolean run;

    /** db to retrieve the values from */
    private AutoCompleteDB db;

    /**
     *
     * @param queue quit to add trained phrases into
     * @param db db to get suggestions from
     */
    public DefaultProducer(BlockingQueue<Candidate> queue, AutoCompleteDB db) {
        this.queue = queue;
        this.scanner = new Scanner(System.in);
        this.listeners = new ArrayList<>();
        this.run = false;
        this.db = db;
    }

    /**
     * produce input that can be used to train the database
     * @return a string that can be parsed to train a autocomplete database
     */
    @Override
    public String produce() {
        System.out.print("Enter a command => ");
        return scanner.nextLine();
    }

    /**
     * used to parse input produced by the command line
     * @param parsable string containing a command and any extra parameters separated by a ':'
     */
    public void parseEntry(String parsable) {
        String[] entry = split.split(parsable);
        if(entry.length == 2) {
            switch(entry[0].toLowerCase()) {
                case TRAIN:
                    train(entry[1].trim().toLowerCase());
                    return;
                case INPUT:
                    List<Candidate> words = getWords(entry[1].trim().toLowerCase());
                    System.out.println(words.toString());
                    return;
                case QUIT:
                    doQuit();
                    return;
                case PRINT:
                    printTree(System.out);
                    return;
                default:
                    printError(entry[0]);
                    return;
            }
        }
        if(entry.length == 1 && entry[0].equals(QUIT)) {
            doQuit();
            return;
        } else if(entry.length == 1 && entry[0].equals(PRINT)) {
            printTree(System.out);
            return;
        }
        printError(parsable);
    }

    /**
     * notify any threads listening for a quit command
     */
    private void doQuit() {
        run = false;
        for(StateListener listener : listeners) {
            listener.stateChanged(new StateEvent(StateEvent.STATE.QUIT));
        }
    }

    /**
     * print an error message if the command couldn't be parsed
     * @param error erroneous command we received
     */
    private void printError(String error) {
        System.out.println("ERROR: " + error + " is not a valid recognized command");
        System.out.println("\tOptions <Train|Input|Quit|Print>:<parameter>");
    }

    @Override
    public void run() {
        run = true;
        try {
            while(run) {
                parseEntry(produce());
            }
        } catch(Exception e) {
            System.out.println("Exception in producer, exiting");
            doQuit();
        }
        System.out.println("Producer has finished");
    }

    /**
     *
     * @param fragment partial word autocomplete should suggest solutions for
     * @return a sorted list of words that start with the fragment
     *          provided where the sort is higher confidence, then alphabetical order.
     */
    @Override
    public List<Candidate> getWords(String fragment) {
        return db.getCandidates(new DefaultCandidate(fragment));
    }

    /**
     * debugging method
     * @param printWriter where to write the table to
     */
    private void printTree(PrintStream printWriter) {
        if(db instanceof AutoCompleteTreeSet) {
            ((AutoCompleteTreeSet)db).printTree(printWriter);
        }
    }

    /**
     * Splits a word into candidates and adds them to a queue to be
     * added to the database by a consumer sharing the queue
     *
     * @param passage sentence to parse and add all words to the db
     */
    @Override
    public void train(String passage) {
        for(String s : splitPattern.split(passage)) {
            //replace any non-word characters with an empty string
            s = nonWordPatter.matcher(s).replaceAll("");
            try {
                //it's possible that the split string was only non word
                //characters ignore if that's the case
                if(!s.equals("")) {
                    queue.put(new DefaultCandidate(s));
                }
            } catch(InterruptedException e) {
                System.out.println("Producer thread interrupted");
                doQuit();
            }
        }
    }

    @Override
    public void addStateListener(StateListener s) {
        listeners.add(s);
    }

    @Override
    public void removeStateListener(StateListener s) {
        listeners.remove(s);
    }
}
