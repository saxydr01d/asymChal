package impl;

import interfaces.Candidate;

import java.util.Comparator;

public class TreeCandidateComparable implements Comparator<Candidate> {

    /**
     *
     * @param o1 candidate potentially being added to the tree
     * @param o2 candidate currently in the tree
     * @return 02 <= o1 <=o2
     */
    @Override
    public int compare(Candidate o1, Candidate o2) {
        if(o1 == null || o2 == null) {
            if(o1 == null && o2 != null) {
                return -1;
            } else {
                return 1;
            }
        }
        if(o1.getWord().equals(o2.getWord())) {
            return 0;
        }

        int size = (o1.getWord().length() <= o2.getWord().length()) ? o1.getWord().length() : o2.getWord().length();

        char[] o1Chars = o1.getWord().toCharArray();
        char[] o2Chars = o2.getWord().toCharArray();

        for(int i =0; i < size; i++) {
            if(o1Chars[i] != o2Chars[i]) {
                return o1Chars[i] - o2Chars[i];
            }
        }

        if(o1.getConfidence() - o2.getConfidence() == 0)
        {
            return o1.getWord().length() - o2.getWord().length();
        }
        return o1.getConfidence() - o2.getConfidence();
    }


}
