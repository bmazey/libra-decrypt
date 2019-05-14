package org.nyu.crypto.dto;

public class Simulation {

    /**
     * should contain a Key, Plaintext, and Ciphertext
     * remember: no methods but setters and getters here
     */

    private Key key;
    private String message;
    private int[] ciphertext;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(int[] ciphertext) {
        this.ciphertext = ciphertext;
    }



}
