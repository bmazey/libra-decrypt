package org.nyu.crypto.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;

@Service
public class FrequencyGenerator {

    public HashMap<String, Integer> generateFrequency() {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Integer> frequency = new HashMap<>();
        try {
            InputStream frequencyStream = new ClassPathResource("frequency.json").getInputStream();

            // see if this warning can be fixed ...
            frequency = mapper.readValue(frequencyStream, HashMap.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return frequency;
    }

    public String[] generateAlphabet() {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Integer> frequency = new HashMap<>();
        try {
            InputStream frequencyStream = new ClassPathResource("frequency.json").getInputStream();

            // see if this warning can be fixed ...
            frequency = mapper.readValue(frequencyStream, HashMap.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return frequency.keySet().stream().toArray(String[]::new);
    }
}
