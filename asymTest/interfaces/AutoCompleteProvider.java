package interfaces;

import java.util.List;

/**
 * a provider that provides and trains an autocomplete db
 */
public interface AutoCompleteProvider {

    /**
     *
     * @param fragment word to get suggestions for
     * @return a list of possible autocomplete phrases
     */
    List<Candidate> getWords(String fragment);

    /**
     * @param passage passage to train to the database
     */
    void train(String passage);
}
