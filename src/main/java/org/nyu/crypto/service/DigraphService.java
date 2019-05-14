package org.nyu.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class DigraphService {

    private final int dimension = 27;

    private HashMap<String, Integer> initiateMapping() {

        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream frequencyStream = new ClassPathResource("mapping.json").getInputStream();
            mapping = mapper.readValue(frequencyStream, HashMap.class);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return mapping;
    }

    public int[][] getDigraphArray(String string) {

        int[][] frequency = new int[dimension][dimension];
        try{
        HashMap<String, Integer> mapping = initiateMapping();
        ArrayList<String> digraphs = new ArrayList<String>();
        for (int i = 0; i < string.length() - 2; i++) {
            digraphs.add(String.valueOf(string.charAt(i)) + String.valueOf(string.charAt(i + 1)));
            int row = mapping.get(String.valueOf(string.charAt(i)));
            int column = mapping.get(String.valueOf(string.charAt(i + 1)));
            frequency[row][column]++;
        }
        String[] vals = new String[dimension];
        mapping.keySet().toArray(vals);
        for (int i = 0; i < vals.length - 1; i++) {
            vals[i] = vals[i + 1];
        }
        vals[26] = " ";
        }catch(Exception e) {
            e.printStackTrace();
        }
        return frequency;
    }

    /**
     * This function takes a set of strings and calculates the digraph frequency
     * over the total number of strings;
     *
     * @param messages
     */
    public double[][] createFrequencyDigraph(String[] messages) {

        int[][] finalFrequencyDigraph = new int[dimension][dimension];
        double[][] finalFrequency = new double[dimension][dimension];
        int count_digraphs = 0;
        try {
            for (String message : messages) {
                finalFrequencyDigraph = addTwoMatrices(finalFrequencyDigraph, getDigraphArray(message));
                // TODO: Need to check why subtracting by 2 works but subtracting by 1 doesn't
                count_digraphs = count_digraphs + message.length() - 2;
            }
            for (int row = 0; row < dimension; row++) {
                for (int column = 0; column < dimension; column++) {
                    finalFrequency[row][column] = ((((double) finalFrequencyDigraph[row][column]) / count_digraphs)
                            * 100);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalFrequency;
    }

    public double[][] getFrequencyDigraph() {

        double[][] finalFrequency = new double[dimension][dimension];
        BufferedReader br = null;
        try {
            InputStream frequencyStream = new ClassPathResource("frequency_digraph.txt").getInputStream();
            //br = new BufferedReader(new FileReader(new File("resources/frequency_digraph.txt")));
            br = new BufferedReader(new InputStreamReader(frequencyStream));
            String value = br.readLine();
            int row = 0;
            while (null != value) {
                String[] split = value.split(" ");
                for (int loop = 0; loop < 27; loop++)
                    finalFrequency[row][loop] = Double.valueOf(split[loop]);
                value = br.readLine();
                row++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            br = null;
        }
        return finalFrequency;
    }

    public void printDigraph(int[][] digraph) {

        HashMap<String, Integer> mapping = initiateMapping();
        String[] vals = new String[dimension];
        mapping.keySet().toArray(vals);
        for (int i = 0; i < vals.length - 1; i++) {
            vals[i] = vals[i + 1];
        }
        vals[26] = " ";
        for (int i = 0; i < dimension; i++) {
            System.out.print(vals[i] + " ");
            for (int j = 0; j < dimension; j++) {
                System.out.print(digraph[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printDigraph(double[][] digraph) {

        HashMap<String, Integer> mapping = initiateMapping();
        DecimalFormat df = new DecimalFormat("#.0000");
        String[] vals = new String[dimension];
        mapping.keySet().toArray(vals);
        for (int i = 0; i < vals.length - 1; i++) {
            vals[i] = vals[i + 1];
        }
        vals[26] = " ";
        for (int i = 0; i < dimension; i++) {
            System.out.print(vals[i] + " ");
            for (int j = 0; j < dimension; j++) {
                System.out.print(df.format(digraph[i][j]) + " ");
            }
            System.out.println();
        }
    }

    /**
     * Add 2 integer 2x2 matrices
     *
     * @param orig
     * @param add
     * @return
     */
    private int[][] addTwoMatrices(int[][] orig, int[][] add) {

        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                orig[row][column] = orig[row][column] + add[row][column];
            }
        }
        return orig;
    }
}
