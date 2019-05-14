package org.nyu.crypto.service.strategy;


import org.apache.commons.lang3.SerializationUtils;
import org.nyu.crypto.dto.Climb;
import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.FrequencyGenerator;
import org.nyu.crypto.service.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Service
public class HillClimber {

    @Value("${key.space}")
    private int keyspace;

    @Value("${charset.length}")
    private int charset;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private Digrapher digrapher;

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private Levenshteiner levenshteiner;

    private final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                                        "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "space"};

    private Random random = new Random();

    private Logger logger = LoggerFactory.getLogger(HillClimber.class);

    /**
     * ok - it's late, but i actually had a flash of insight and want to implement tomorrow, along with heuristic
     * optimal key (a separate method to pre-optimize key)
     *
     * 1. compute and iterate over the ciphertext digraph
     * 2. for each element in the ciphertext digraph, find the closest element in the perfect plaintext digraph
     * 3. take the row # and column # (0 - 105) of the ciphertext digraph element, find what characters in the
     *      putative key the row / column values are currently assigned to, and swap the ciphertext digraph row /
     *      columns values into the respective putative keyspaces
     *          *how do you know which ones to give up?* for all combinations of a.list and b.list, look up
     *          cipher[a][b] and take the row / column # of the lowest scoring value (least frequent)
     * 4. recompute the putative digraph and score it against the perfect plaintext digraph
     * 5. the new score is lower, keep the key; if it's higher, unswap and continue
     *
     */

    public Climb climb(int[] ciphertext, double[][] plaintext) {

        Climb climb = new Climb();
        climb.setCiphertext(ciphertext);

        // start by generating a random key
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        // TODO - apply optimal heuristic key guess strategy as well
        // here it is!
        // HashMap<String, ArrayList<Integer>> key = keyGenerator.generatePutativeKey(ciphertext);

        climb.setInitialKey(key);

        // logger.info("initial key: ");
        // keyGenerator.printKey(key);

        // compute ciphertext digraph
        double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        // create a deep copy
        HashMap<String, ArrayList<Integer>> result = SerializationUtils.clone(key);

        // TODO - why 12 rounds?
        for (int i = 0; i < 12; i++) {
            result = climbHill(result, plaintext, cipher, ciphertext);
        }

        levenshteiner.distanceSwap(result, ciphertext);

        // build Climb dto
        climb.setPutativeKey(result);
        climb.setPutative(decryptor.decrypt(result, ciphertext));
        return climb;
    }

    private HashMap<String, ArrayList<Integer>> climbHill(HashMap<String, ArrayList<Integer>> key,
                                                           double[][] plaintext, double[][] cipher, int[] ciphertext) {

        // create a deep copy

        // we start by computing the initial putative digraph
        String putativeText = decryptor.decrypt(key, ciphertext);
        double[][] putative = digrapher.computePutativeDigraph(putativeText);

        //compute our initial score
        double score = score(plaintext, putative);
        // logger.info("initial score: " + score);

        // next we iterate over the ciphertext digraph to find the closest % match to the plaintext digraph
        for (int i = 0; i < cipher.length; i++) {
            for (int j = 0; j < cipher[i].length; j++) {
                // choose two random letters
                String firstLetter = "";
                String secondLetter = "";
                while (firstLetter.equals(secondLetter)) {
                    firstLetter = alphabet[random.nextInt(alphabet.length)];
                    secondLetter = alphabet[random.nextInt(alphabet.length)];
                }

                // TODO - find biggest offenders in putative vs dictionary digraph and swap with each other!
                // get two random numbers from the letters' keyspace and swap them
                Integer k = key.get(firstLetter).get(random.nextInt(key.get(firstLetter).size()));
                Integer n = key.get(secondLetter).get(random.nextInt(key.get(secondLetter).size()));

                // logger.info(firstLetter + " : " + k + " <-> " + secondLetter + " : " + n);
                key = swap(key, firstLetter, secondLetter, k, n);

                // compute the new score
                putativeText = decryptor.decrypt(key, ciphertext);
                putative = digrapher.computePutativeDigraph(putativeText);
                double current = score(plaintext, putative);

                // if the new score is greater than the old score, unswap
                if (current > score) {
                    // logger.info("unswap!");
                    key = swap(key, firstLetter, secondLetter, n, k);
                    // keyGenerator.printKey(result);
                    continue;
                }

                score = current;
                // logger.info("updated score: " + score);
                // keyGenerator.printKey(result);
            }
        }
        // logger.info("final key: ");
        // keyGenerator.printKey(result);
        return key;
    }

    // we use a key to track associations in the digraph matrix
    private Optional<String> getLetterAssociation(HashMap<String, ArrayList<Integer>> map, Integer x) {
        for (String key : map.keySet()) {
            ArrayList<Integer> list = map.get(key);
            if (list.contains(x)) return Optional.of(key);
        }
        return Optional.empty();
    }

    // given two numbers and two letters, swap the keyspace a <-> x and b <-> y
    private HashMap<String, ArrayList<Integer>> swap(HashMap<String, ArrayList<Integer>> map,
                                                     String a, String b, Integer x, Integer y) {

        // if the two letters are the same, swapping will not affect the result
        if (a.equals(b)) return map;

        // can't swap a number with itself
        if (x.intValue() == y.intValue()) return map;

        // assert that the lists contain the expected values
        ArrayList<Integer> alist = map.get(a);
        assert alist.contains(x);

        ArrayList<Integer> blist = map.get(b);
        assert blist.contains(y);

        // perform the swap ...
        alist.remove(x);
        alist.add(y);
        map.put(a, alist);

        blist.remove(y);
        blist.add(x);
        map.put(b, blist);

        return map;
    }

    // method to score the abs val difference
    // public for testing purposes
    public double score(double[][] dictionary, double[][] putative) {
        double score = 0;
        for(int i = 0; i < dictionary.length; i++) {
            for (int j = 0; j < dictionary[i].length; j++) {
                score += Math.abs(dictionary[i][j] - putative[i][j]);
            }
        }
        return score;
    }

    private String convert(int i) {
        assert i <= alphabet.length;
        return alphabet[i];
    }

}
