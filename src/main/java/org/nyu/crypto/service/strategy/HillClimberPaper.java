package org.nyu.crypto.service.strategy;

import org.nyu.crypto.dto.ClimbPaper;
import org.nyu.crypto.dto.PutativeKey;
import org.nyu.crypto.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HillClimberPaper {

    @Autowired
    private MessageGenerator messageGenerator;

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private Decryptor decrypt;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private GuessKey guessKey;

    @Autowired
    private DigraphService digraphService;

    public ClimbPaper climbPlaintextDigraph(int[] ciphertext, int[][] plaintext) {

            int[] cipher = ciphertext;
            PutativeKey[] keyGuess = guessKey.getKey(cipher);
            System.out.print(cipher);
            for (int i = 0; i < 50; i++) {
                int guessvalue = guessKey.calculateScore(digraphService.getDigraphArray(decrypt.decrypt(cipher, keyGuess)),
                        plaintext);
                // Changed the distance value from 26 to 106
                for (int distance = 1; distance < 106; distance++) {
                    try {
                        guessKey.swapKey(cipher, keyGuess, distance, plaintext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                guessvalue = guessKey.calculateScore(digraphService.getDigraphArray(decrypt.decrypt(cipher, keyGuess)),
                        plaintext);
                if (guessvalue == 0)
                    break;
            }
            String putative = decrypt.decrypt(cipher, keyGuess);
            ClimbPaper climber = new ClimbPaper();
            climber.setCiphertext(ciphertext);
            climber.setPutative(putative);

            // TODO - add key to DTO?
            return climber;
    }

    public String climbDictionaryFrequencyDigraph(int[] ciphertext) {
        String message = messageGenerator.generateMessage();
        int[] cipher = encryptor.encrypt(keyGenerator.generateKey(), message);
        PutativeKey[] keyGuess = guessKey.getKey(cipher);
        double d2=0.0;
        double d1=0.35276;
        for (int i = 0; i < 250; i++) {
            String[] carry = new String[1];
            carry[0] = decrypt.decrypt(cipher, keyGuess);
            double guessvalue = guessKey.calculateScore(digraphService.createFrequencyDigraph(carry),
                    digraphService.getFrequencyDigraph());
            // Changed the distance value from 26 to 106

            for (int distance = 1; distance < 106; distance++) {
                try {
                    guessKey.swapKey(cipher, keyGuess, distance, digraphService.getFrequencyDigraph(),d1,0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            carry[0] = decrypt.decrypt(cipher, keyGuess);
            guessvalue = guessKey.calculateScore(digraphService.createFrequencyDigraph(carry),
                    digraphService.getFrequencyDigraph());
        }
        return decrypt.decrypt(cipher, keyGuess);
    }
}
