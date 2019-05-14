package org.nyu.crypto.service;

import org.nyu.crypto.dto.PutativeKey;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;


@Service
public class Decryptor {

    /**
     * This is a simple decryption strategy which requires the original key
     * @param map
     * @param ciphertext
     * @return
     */
    public String decrypt(HashMap<String, ArrayList<Integer>> map, int[] ciphertext) {
        StringBuilder plaintext = new StringBuilder();

        for(int i = 0; i < ciphertext.length; i++) {

            for(String key : map.keySet()) {
                ArrayList<Integer> values = map.get(key);
                if (values.contains(ciphertext[i])) {
                    if (key.equals("space")) plaintext.append(" ");
                    else plaintext.append(key);
                }
            }
        }

        return plaintext.toString();
    }

    /**
     * Variations to decrypt key with key Map being sent in multiple ways
     *
     * @param ciphertext
     * @param keyMap
     * @return
     */
    public String decrypt(int[] ciphertext, HashMap<Integer, String> keyMap) {

        StringBuilder plaintextguess = new StringBuilder();
        for (int ind : ciphertext) {
            if (keyMap.get(ind).equalsIgnoreCase("space"))
                plaintextguess.append(" ");
            else
                plaintextguess.append(keyMap.get(ind));
        }
        return plaintextguess.toString();
    }

    public String decrypt(int[] ciphertext, PutativeKey[] keyList) {

        HashMap<Integer, String> keysMap = new HashMap<Integer, String>();
        for (int loop = 0; loop < keyList.length; loop++) {
            keysMap.put(loop, keyList[loop].getAlphabet());
        }
        return decrypt(ciphertext, keysMap);
    }

}
