package org.nyu.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyu.crypto.dto.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;


@Service
public class KeyGenerator {

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    private HashMap<String, ArrayList<Integer>> key;

    private ObjectMapper mapper;

    @Value("${key.space}")
    private int keyspace;

    @Value("${space.frequency}")
    private int spaceFrequency;

    public void setKey(HashMap<String, ArrayList<Integer>> key) {
        this.key = key;
    }

    public Key generateKeyDto(){
        mapper = new ObjectMapper();
        HashMap<String, ArrayList<Integer>> map = generateKey();
        Key key = mapper.convertValue(map, Key.class);
        return key;
    }

    public HashMap<String, ArrayList<Integer>> generateKey() {

        HashMap<String, Integer> map = frequencyGenerator.generateFrequency();
        ArrayList<Integer> numbers = new ArrayList<>(IntStream.range(0, keyspace).boxed().collect(toSet()));
        HashMap<String, ArrayList<Integer>> result = new HashMap<>();
        int partition=0;

        Collections.shuffle(numbers);

        for(String key: map.keySet()) {
            result.put(key,new ArrayList<> (numbers.subList(partition, partition+map.get(key))));
            partition=partition+map.get(key);
        }
        return result;
    }
  
    public HashMap<String, ArrayList<Integer>> generatePutativeKey(int[] ciphertext) {
      
        HashMap<String, Integer> map = frequencyGenerator.generateFrequency();
        HashMap<String, ArrayList<Integer>> putativeKey = new HashMap<>();
        ArrayList<Integer> numbers = new ArrayList<>(IntStream.range(0, keyspace).boxed().collect(toSet()));
        HashSet<Integer> whitelist = new HashSet<>();
        HashSet<Integer> blacklist = new HashSet<>();
        HashSet<Integer> bValue = new HashSet<>();

        // The first 3 characters cannot be space as the shortest word possible is of length 3
        blacklist.add(ciphertext[0]); //first character
        blacklist.add(ciphertext[1]); //second character
        blacklist.add(ciphertext[2]); //third character


        // We add all the other characters of the ciphertext to a "whitelist", all the possible space values
        for (int i = 0; i < ciphertext.length; i++){
            if(!blacklist.contains(ciphertext[i])){
                whitelist.add(ciphertext[i]);
            }
        }

        // If 2 consecutive characters are equal, we assume there is a high possibility that it is a "b"
        for(int i = 0; i < ciphertext.length - 1; i++) {
            if (ciphertext[i] == ciphertext[i + 1]) {
                bValue.add(ciphertext[i]);
                blacklist.add(ciphertext[i]);
                whitelist.remove(ciphertext[i]);
            }
        }

        ArrayList<Integer> possibleSpaceValues = new ArrayList<>(whitelist);
        ArrayList<Integer> spaceValues = new ArrayList<>();

        // Restricting the numbers of possible space values
        // If a character is a space, the 3 following characters as well as the 3 preceding characters cannot be space
        // Because the shortest word possible in the dictionary is of length 3
        int spaceTemp = 0;
        while(spaceTemp != spaceFrequency) {
            Collections.shuffle(possibleSpaceValues);
            spaceValues = new ArrayList<>(possibleSpaceValues.subList(0, spaceFrequency));
            spaceTemp = 0;
            for (int i = 3; i < ciphertext.length - 3; i++){
                if (spaceValues.contains(ciphertext[i])){
                    if (spaceValues.contains(ciphertext[i + 1]) ||
                            spaceValues.contains(ciphertext[i + 2]) ||
                            spaceValues.contains(ciphertext[i + 3])){
                        break;
                    }
                    else if (spaceValues.contains(ciphertext[i - 1]) ||
                            spaceValues.contains(ciphertext[i - 2]) ||
                            spaceValues.contains(ciphertext[i - 3])){
                        break;
                    }
                    else
                        spaceTemp++;

                }
                if (spaceTemp == spaceFrequency)
                    break;
            }
        }

        numbers.removeAll(spaceValues);

        ArrayList<Integer> bNum = new ArrayList<>(bValue);

        // If we end up with several possible "b"
        // we pick one randomly among them
        if (bValue.size() > 1){
            ArrayList<Integer> bTemp = new ArrayList<>(bValue);
            Collections.shuffle(bTemp);
            bNum.clear();
            bNum.addAll(bTemp.subList(0, 1));
            numbers.removeAll(bNum);
        }

        // If there is no existing guess for "b"
        // we pick one randomly from the list of number minus the chosen spaceValues
        if (bValue.size() == 0){
            ArrayList<Integer> bTemp = new ArrayList<>(numbers);
            Collections.shuffle(bTemp);
            bNum.clear();
            bNum.addAll(bTemp.subList(0, 1));
            numbers.removeAll(bNum);
        }

        // If there is only one guess for "b"
        if (bValue.size() == 1)
            numbers.removeAll(bNum);

        // The 86 numbers left are shuffled randomly one more time before being assigned to the putative key
        ArrayList<Integer> leftNum = new ArrayList<>(numbers);
        Collections.shuffle(leftNum);

        int partition = 0;

        for(String key: map.keySet()) {
            if (!key.equals("space") && !key.equals("b")) {
                putativeKey.put(key, new ArrayList<>(leftNum.subList(partition, partition + map.get(key))));
                partition = partition + map.get(key);
            }
        }

        putativeKey.put("space", spaceValues);

        putativeKey.put("b", bNum);

        return putativeKey;
    }

    public void printKey(HashMap<String, ArrayList<Integer>> key) {
        for (String val: key.keySet()) {
            ArrayList<Integer> list = key.get(val);
            System.out.println(val + " : " + Arrays.toString(list.toArray()));
        }
    }
}
