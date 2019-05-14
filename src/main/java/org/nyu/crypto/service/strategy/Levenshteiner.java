package org.nyu.crypto.service.strategy;

import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.DictionaryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service
public class Levenshteiner {

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    @Autowired
    private Decryptor decryptor;

    private Logger logger = LoggerFactory.getLogger(Levenshteiner.class);

    public HashMap<String, ArrayList<Integer>> distanceSwap(HashMap<String, ArrayList<Integer>> key,
                                                            int[] ciphertext) {
        // get the putative plaintext
        String text = decryptor.decrypt(key, ciphertext);

        // generate a list of words from the dictionary
        String[] words = dictionaryGenerator.generateDictionaryDto().getWords();

        // split the putative into "words"
        String[] putatives = text.split(" ");

        int max = Integer.MAX_VALUE;
        int size = 0;
        String original = "";
        String swap = "";

        for (String putative: putatives) {
            int currentSize = putative.length();
            for(String word: words) {
                int i = calculate(word, putative);

                // if the score is 0 it's a perfect match ... ignore it!
                if (i == 0) continue;

                // FIXME - do we actually care about taking the largest all the time?
                if (i <= max && currentSize >= size) {
                    max = i;
                    size = currentSize;
                    swap = word;
                    original = putative;
                }
            }
        }

        logger.info("original putative: " + original + " | swap word: " + swap);

        // TODO - align by LCS and swap! don't forget to add space swap at beginning and end ...

        return key;
    }


    private int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

}
