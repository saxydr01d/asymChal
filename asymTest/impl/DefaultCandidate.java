package impl;

import interfaces.Candidate;

/**
 * default implementation of a candidate containing a word
 * and a confidence, in this implementation confidence is
 * determined by the number of times a word has been
 * trained.
 *
 * @author Trevor Grabko
 */
public class DefaultCandidate implements Candidate {

    private final String word;

    private int confidence = 1;

    public DefaultCandidate(String word) {
        this.word = word;
        confidence = 1;
    }

    public DefaultCandidate(String word, Integer confidence) {
        this.word = word;
        this.confidence = confidence;
    }

    /**
     * @return word this candidate represents
     */
    @Override
    public String getWord() {
        return word;
    }

    /**
     * @return number of times the word has been trained
     */
    @Override
    public Integer getConfidence() {
        return confidence;
    }

    /**
     * increment confidence as it has appeared again
     * @param newConfidence not used since this is a
     *                      rudimentary implementation
     *                      that just increases each
     *                      time it is trained
     */
    public void setConfidence(int newConfidence) {
        confidence++;
    }

    /**
     * candidates are equal if their words are the same
     * @param obj Candidate
     * @return if the parameter object is equal in name to this one
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DefaultCandidate && ((DefaultCandidate) obj).word.length() == word.length() &&
                ((DefaultCandidate) obj).word.equals(this.word);
    }

    /**
     * @return "&lt;word&gt;" (&lt;confidence&gt;)
     */
    @Override
    public String toString() {
        return "\"" + word + "\" (" + confidence + ")";
    }
}
