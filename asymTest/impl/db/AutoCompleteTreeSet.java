package impl.db;

import impl.DefaultCandidate;
import impl.TreeCandidateComparable;
import interfaces.AutoCompleteDB;
import interfaces.Candidate;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementation of a database containing possible autocomplete
 * solutions. Uses a tree sorted in alphabetical order then
 * probability of solution and length last
 *
 * @author Trevor Grabko
 */
public class AutoCompleteTreeSet implements AutoCompleteDB {

    /** treeset containing a tree of autocomplete candidates and their viability */
    private TreeSet<Candidate> treeSet;
    /** read write lock to keep the tree from receiving multiple writes at once */
    private ReentrantReadWriteLock lock;
    private ReentrantReadWriteLock.ReadLock rdLock;
    private ReentrantReadWriteLock.WriteLock wrtLock;
    /** reference to this db */
    private static AutoCompleteTreeSet singleton;

    /**
     * creates this new db
     */
    private AutoCompleteTreeSet() {
        treeSet = new TreeSet<>(new TreeCandidateComparable());
        lock = new ReentrantReadWriteLock();
        rdLock = lock.readLock();
        wrtLock = lock.writeLock();
    }

    /**
     * @return a copy of this db
     */
    public static AutoCompleteTreeSet getDefault() {
        if(singleton == null) {
            singleton = new AutoCompleteTreeSet();
        }
        return singleton;
    }

    /**
     * @param candidate Candidate to add to the database, or
     *                  if it already exist, increase its
     *                  relevance
     */
    @Override
    public void addCandidate(Candidate candidate) {
        try
        {
            wrtLock.lock();
            //add the candidate to the tree
            if(!treeSet.add(candidate)) {
                try {
                    //if the candidate already exists in the tree
                    //increase its confidence
                    rdLock.lock();
                    treeSet.floor(candidate).setConfidence(1);
                } finally {
                    rdLock.unlock();
                }
            }
        }finally {
            wrtLock.unlock();
        }
    }

    /**
     * retrieves all the candidates in the tree that start with
     * the phrase in the candidate object.
     * @param candidate candidate containing a starting phrase
     * @return a list of possible candidates, sorted by confidence
     *          and alphabetical order
     */
    @Override
    public List<Candidate> getCandidates(Candidate candidate) {
        final List<Candidate> candidates = new ArrayList<>();
        try{
            //create a copy of the word as a char array that will act as the lower bound
            char[] word = candidate.getWord().toCharArray();
            //increment the last character by one
            word[word.length - 1]++;
            //create a candidate with a word where the final character is n+1 of the previous
            Candidate toCandidate = new DefaultCandidate(new String(word));
            rdLock.lock();
            //retrieve all candidates between these two inclusive
            SortedSet<Candidate> c = treeSet.subSet(candidate, true, toCandidate, true);
            //create a clone of all the possible candidates
            c.forEach( i -> candidates.add(new DefaultCandidate(i.getWord(), i.getConfidence())));
        } finally {
            rdLock.unlock();
        }
        //sort the new set of candidates
        candidates.sort(Comparator.comparingInt(Candidate::getConfidence).reversed());
        //return a sorted set of candidates by 1 confidence, then alphabet
        return candidates;
    }

    /**
     * debugging method, prints out tree
     * @param writer where to print the tree to
     */
    public void printTree(PrintStream writer) {
        for(Candidate c : treeSet) {
            writer.println(c.toString());
        }
    }
}
