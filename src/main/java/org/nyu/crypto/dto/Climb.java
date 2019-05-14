package org.nyu.crypto.dto;

import java.util.ArrayList;
import java.util.HashMap;

public class Climb {

    private HashMap<String, ArrayList<Integer>> initialKey;

    private HashMap<String, ArrayList<Integer>> putativeKey;

    private int[] ciphertext;

    private String putative;

    public HashMap<String, ArrayList<Integer>> getInitialKey() {
        return initialKey;
    }

    public void setInitialKey(HashMap<String, ArrayList<Integer>> initialKey) {
        this.initialKey = initialKey;
    }

    public HashMap<String, ArrayList<Integer>> getPutativeKey() {
        return putativeKey;
    }

    public void setPutativeKey(HashMap<String, ArrayList<Integer>> putativeKey) {
        this.putativeKey = putativeKey;
    }

    public int[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(int[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    public String getPutative() {
        return putative;
    }

    public void setPutative(String putative) {
        this.putative = putative;
    }

}
