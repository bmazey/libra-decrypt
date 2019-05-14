package org.nyu.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyu.crypto.dto.Key;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


@Service
public class Encryptor {

    private ObjectMapper mapper;

    public int[] encrypt(HashMap<String, ArrayList<Integer>> keyMap, String plaintext){

        Random r = new Random();
        int[] ciphertextArray = new int[plaintext.length()];


        for(int i = 0; i < plaintext.length(); i++) {

            if (plaintext.charAt(i) == ' ') {
                ArrayList<Integer> temp = keyMap.get("space");
                int cipher = temp.get(r.nextInt(temp.size()));
                ciphertextArray[i] = cipher;
                continue;
            }

            ArrayList<Integer> temp = keyMap.get(Character.toString(plaintext.charAt(i)));
            int cipher = temp.get(r.nextInt(temp.size()));
            ciphertextArray[i] = cipher;

        }

        return ciphertextArray;

    }

    public int[] encrypt(Key key, String plaintext) {
        mapper = new ObjectMapper();
        HashMap<String, ArrayList<Integer>> map = mapper.convertValue(key, HashMap.class);

        return encrypt(map, plaintext);
    }

    public int[] encryptMod(HashMap<String, ArrayList<Integer>> keyMap, String plaintext) {

        /**
         * this works differently, we do not pull a random number from the keyspace, instead we do ...
         *
         *                      cipher = plaintext[i] % key_list.length()
         */

        int[] ciphertextArray = new int[plaintext.length()];

        for(int i = 0; i < plaintext.length(); i++) {

            if (plaintext.charAt(i) == ' ') {
                ArrayList<Integer> temp = keyMap.get("space");
                int cipher = temp.get(i % temp.size());
                ciphertextArray[i] = cipher;
                continue;
            }

            ArrayList<Integer> temp = keyMap.get(Character.toString(plaintext.charAt(i)));

            int cipher = temp.get(i % temp.size());
            ciphertextArray[i] = cipher;

        }

        return ciphertextArray;
    }
}
