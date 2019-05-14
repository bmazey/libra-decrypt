package org.nyu.crypto.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.nyu.crypto.dto.Dictionary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;


@Service
public class DictionaryGenerator {

    public Dictionary generateDictionaryDto() {
        Dictionary dictionary = new Dictionary();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream dictionaryStream = new ClassPathResource("dictionary.json").getInputStream();
            dictionary = objectMapper.readValue(dictionaryStream, Dictionary.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dictionary;

    }

    public Dictionary generateDictionaryDto(int dictionaryLength){
        Dictionary dictionary = generateDictionaryDto();

        ArrayList<String> shuffledWords =  new ArrayList<String>(Arrays.asList(dictionary.getWords()));
        Collections.shuffle(shuffledWords);

        ArrayList<String> shuffledWordsSubList=new ArrayList<String>(shuffledWords.subList(0, dictionaryLength));

        // SEE https://stackoverflow.com/questions/174093/toarraynew-myclass0-or-toarraynew-myclassmylist-size
        dictionary.setWords(shuffledWordsSubList.toArray(new String[0]));

        return dictionary;
    }
}
