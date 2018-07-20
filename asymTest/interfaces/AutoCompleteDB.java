package interfaces;

import java.util.List;

/**
 * a db that you can add and remove candidates to
 */
public interface AutoCompleteDB {

    /**
     * @param candidate adds candidates to the db
     */
    void addCandidate(Candidate candidate);

    /**
     * retrieves a list of candidates that contain
     * the candidates phrase
     * @param candidate phrase the candidates must start with
     * @return a list of candidates containing the phrase
     */
    List<Candidate> getCandidates(Candidate candidate);
}
