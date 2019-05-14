package org.nyu.crypto.dto;

public class ClimbSample {

    private int[] ciphertext;

    private int[][] digraph;

    public int[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(int[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    public int[][] getDigraph() {
        return digraph;
    }

    public void setDigraph(int[][] digraph) {
        this.digraph = digraph;
    }
}
