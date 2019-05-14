package org.nyu.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyu.crypto.dto.PutativeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Service
public class GuessKey {

    private HashMap<String, Integer> frequency;
    private ObjectMapper mapper;
    private final int keyspace = 106;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private DigraphService digraphService;

    public PutativeKey[] getKey(int[] cipher_text) {

        mapper = new ObjectMapper();
        frequency = new HashMap<String, Integer>();
        PutativeKey[] keyList = new PutativeKey[keyspace];
        boolean goodGuess = false;
        try {
            //File file = new File("resources/frequency.json");
            InputStream frequencyStream = new ClassPathResource("frequency.json").getInputStream();
            frequency = mapper.readValue(frequencyStream, HashMap.class);
            ArrayList<Integer> numbers = new ArrayList<Integer>(IntStream.range(0, keyspace).boxed().collect(toSet()));
            do {
                Collections.shuffle(numbers);
                // System.out.println(numbers);
                int counter = 0;
                for (String ind : frequency.keySet()) {
                    for (int i = 0; i < frequency.get(ind); i++) {
                        int val = numbers.get(counter++);
                        keyList[val] = new PutativeKey();
                        keyList[val].setAlphabet(ind);
                    }
                }
                if (!checkForBadGuess(cipher_text, keyList))
                    goodGuess = true;
            } while (!goodGuess);
            //System.out.println("Initial Guess Key");
            printKey(keyList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyList;
    }

    public void printKey(PutativeKey[] keyList) {

        int counter = 0;
        for (PutativeKey indKey : keyList) {
            System.out.print(counter + "," + indKey.getAlphabet() + "|");
            counter++;
        }
        System.out.println();
    }

    public void printKey(PutativeKey[] keyList, File file) {

        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            int counter = 0;
            for (PutativeKey indKey : keyList) {
                fw.append(counter + "," + indKey.getAlphabet() + "|");
                counter++;
            }
            fw.append("\\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fw)
                fw = null;
        }
    }

    public void printKey(PutativeKey[] keyList, String file) {

        printKey(keyList, new File(file));
    }

    public boolean checkForBadGuess(int[] ciphertext, PutativeKey[] keyList) {

        /**
         * Check if the first letter is being assigned as space. If it is rejects it
         * immediately
         */

        if (keyList[ciphertext[0]].getAlphabet().equalsIgnoreCase("space"))
            return true;
        else {
            HashMap<Integer, String> keysMap = new HashMap<Integer, String>();
            for (int loop = 0; loop < keyList.length; loop++) {
                keysMap.put(loop, keyList[loop].getAlphabet());
            }
            String plaintext = decryptor.decrypt(ciphertext, keysMap);
            for (int i = 0; i < plaintext.length() - 1; i++) {
                if (plaintext.charAt(i) == ' ' && plaintext.charAt(i + 1) == ' ')
                    return true;
            }
        }
        return false;
    }

    public boolean checkForBadGuessStrict(int[] ciphertext, PutativeKey[] keyList) {

        /**
         * Check if the first letter is being assigned as space. If it is rejects it
         * immediately
         */

        if (keyList[ciphertext[0]].getAlphabet().equalsIgnoreCase("space"))
            return true;
        else {
            HashMap<Integer, String> keysMap = new HashMap<Integer, String>();
            for (int loop = 0; loop < keyList.length; loop++) {
                keysMap.put(loop, keyList[loop].getAlphabet());
            }
            String plaintext = decryptor.decrypt(ciphertext, keysMap);
            int space = 0;
            for (int i = 0; i < plaintext.length() - 1; i++) {
                int space1 = 0;
                if (plaintext.charAt(i) == ' ' && plaintext.charAt(i + 1) == ' ')
                    return true;
                if (plaintext.charAt(i) == ' ') {
                    space1 = i;
                    if (space1 - space > 12)
                        return true;
                }
                space = space1;
            }
        }
        return false;
    }

    public void swapKey(int[] ciphertext, PutativeKey[] key, int distance, int[][] message_digraph) throws Exception {

        // System.out.println("\nDistance " + distance + " -- > ");
        PutativeKey[] tempKey = new PutativeKey[key.length];
        for (int loop = 0; loop < key.length; loop++) {
            tempKey[loop] = new PutativeKey();
        }
        tempKey = copyArray(key, tempKey);
        // calculate the initial value over here
        int initval = calculateScore(digraphService.getDigraphArray(decryptor.decrypt(ciphertext, key)), message_digraph);
        // System.out.println("initial value of the key " + initval);
        int swaps = 0;
        for (int i = 0; i < key.length - distance; i++) {
            // swap the key
            if (!key[i].getAlphabet().equals(key[i + distance].getAlphabet())) {
                String val = key[i].getAlphabet();
                key[i].setAlphabet(key[i + distance].getAlphabet());
                key[i + distance].setAlphabet(val);
                if (checkForBadGuess(ciphertext, key)) {
                    key = copyArray(tempKey, key);
                } else {
                    int score = calculateScore(digraphService.getDigraphArray(decryptor.decrypt(ciphertext, key)),
                            message_digraph);
                    if (score <= initval || score <= initval + 2) {
                        initval = score;
                        swaps++;
                        tempKey = copyArray(key, tempKey);
                    } else {
                        key = copyArray(tempKey, key);
                    }
                }
            }
        }
    }

    public void swapKey(int[] ciphertext, PutativeKey[] key, int distance, double[][] message_digraph,double d1,double d2)
            throws Exception {

        PutativeKey[] tempKey = new PutativeKey[key.length];
        for (int loop = 0; loop < key.length; loop++) {
            tempKey[loop] = new PutativeKey();
        }
        tempKey = copyArray(key, tempKey);
        String[] messages = new String[1];
        messages[0] = decryptor.decrypt(ciphertext, key);
        double initval = calculateScore(digraphService.createFrequencyDigraph(messages),
                message_digraph);
        int swaps = 0;
        for (int i = 0; i < key.length - distance; i++) {
            // swap the key
            if (!key[i].getAlphabet().equals(key[i + distance].getAlphabet())) {
                String val = key[i].getAlphabet();
                key[i].setAlphabet(key[i + distance].getAlphabet());
                key[i + distance].setAlphabet(val);
                if (checkForBadGuess(ciphertext, key)) {
                    key = copyArray(tempKey, key);
                } else {
                    messages = new String[1];
                    messages[0] = decryptor.decrypt(ciphertext, key);
                    double score = calculateScore(digraphService.createFrequencyDigraph(messages),
                            message_digraph);
                    //score <= initval+0.07 || score <= initval + 0.0059
                    //0.35276
                    if (score <= initval + 0.35276){
                        initval = score;
                        //System.out.println("*************************************"+d1);
                        swaps++;
                        tempKey = copyArray(key, tempKey);
                        //System.out.print(initval + "-->");
                    } else {
                        key = copyArray(tempKey, key);
                    }
                }
            }
        }
    }

    public int calculateScore(int[][] putativekey, int[][] original_message) {

        int sum = 0;
        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < 27; j++) {
                int val = putativekey[i][j] - original_message[i][j];
                if (val < 0) {
                    sum = sum - val;
                } else {
                    sum = sum + val;
                }
            }
        }
        return sum;
    }

    public double calculateScore(double[][] putativekey, double[][] original_message) {

        double sum = 0;
        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < 27; j++) {
                double val = putativekey[i][j] - original_message[i][j];
                if (val < 0) {
                    sum = sum - val;
                } else {
                    sum = sum + val;
                }
            }
        }
        return sum;
    }

    public PutativeKey[] copyArray(PutativeKey[] srcs, PutativeKey[] dests) {

        for (int loop = 0; loop < srcs.length; loop++) {
            dests[loop].setAlphabet(srcs[loop].getAlphabet());
        }
        return dests;
    }
}
