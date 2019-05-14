package org.nyu.crypto.service.strategy;

import org.nyu.crypto.service.DictionaryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class Digrapher {

    @Value("${alphabet.length}")
    private int alphabet;

    @Value("${charset.length}")
    private int charset;

    @Value("${key.space}")
    private int keyspace;

    @Value("${space.value}")
    private int spaceval;

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    // we use this to calculate the initial 27 x 27 dictionary digraph matrix
    public double[][] computeDictionaryDigraph() {

        double[][] digraph = new double[charset][charset];

        String[] words = dictionaryGenerator.generateDictionaryDto().getWords();

        int digraphs = 0;
        for (String word : words) {
            // add a space before and after each word
            word = " " + word + " ";
            digraphs += word.length() - 1;
            for (int i = 0; i < word.length() - 1; i++) {
                digraph[convert(word.charAt(i))][convert(word.charAt(i + 1))] += 1;
            }
        }

        for (int i = 0; i < digraph.length; i++) {
            for (int j = 0; j < digraph[i].length; j++) {
                digraph[i][j] = (digraph[i][j] / digraphs) * 100;
            }
        }

        return digraph;
    }

    // we use this method to compute the 27 x 27 putative plaintext digraph
    public double[][] computePutativeDigraph(String text) {

        double[][] putative = new double[charset][charset];
        int digraphs = text.length() - 1;

        // again, no need to check the last value
        for (int i = 0; i < text.length() - 1; i++) {
            putative[convert(text.charAt(i))][convert(text.charAt(i + 1))] += 1;
        }

        for (int i = 0; i < putative.length; i++) {
            for (int j = 0; j < putative[i].length; j++) {
                putative[i][j] = (putative[i][j] / digraphs) * 100;
            }
        }

        return putative;
    }

    // this method computes a 106 x 106 digraph matrix of the ciphertext
    public double[][] computeCipherDigraph(int[] ciphertext) {

        int digraphs = ciphertext.length - 1;

        double[][] cipher = new double[keyspace][keyspace];

        // we don't have to check the last value, so we stop at length - 1
        for (int i = 0; i < ciphertext.length - 1; i++) {
            cipher[ciphertext[i]][ciphertext[i + 1]] += 1;
        }

        // convert to percentage
        for (int i = 0; i < cipher.length; i++) {
            for (int j = 0; j < cipher[i].length; j++) {
                cipher[i][j] = (cipher[i][j] / digraphs) * 100;
            }
        }

        return cipher;
    }

    // TODO - this might be our culprit
    private int convert(char c) {
        if (c == ' ') return spaceval;
        else return (c - 'a');
    }

}
