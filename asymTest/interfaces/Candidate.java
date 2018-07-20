package interfaces;

/**
 * models an autocomplete candidate containing a word
 * and the probability the confidence that it is the
 * proper autocomplete word.
 */
public interface Candidate extends Cloneable{

    /**
     * @return word for this candidate
     */
    String getWord();

    /**
     * @return confidence this is the word you want
     */
    Integer getConfidence();

    /**
     * @param newConfidence sets the new confidence
     */
    void setConfidence(int newConfidence);
}
