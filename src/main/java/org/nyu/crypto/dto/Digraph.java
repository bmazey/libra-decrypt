package org.nyu.crypto.dto;

public class Digraph {

    private String plaintext;

    private int[][] digraph;

    public int[][] getDigraph() {
        return digraph;
    }

    public void setDigraph(int[][] digraph) {
        this.digraph = digraph;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(String plaintext) {
        this.plaintext = plaintext;
    }
}
