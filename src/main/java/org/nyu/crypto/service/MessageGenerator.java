package org.nyu.crypto.service;


import org.nyu.crypto.dto.Dictionary;
import org.nyu.crypto.dto.Message;
import org.nyu.crypto.service.data.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@Service
public class MessageGenerator {

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    @Autowired
    private MessageRepository messageRepository;

    @Value("${message.space}")
    private int messageSpace;

    private Random r;

    public String generateMessage() {
        r = new Random();
        StringBuilder messageBuilder = new StringBuilder();
        Dictionary dictionary = dictionaryGenerator.generateDictionaryDto();

        while(messageBuilder.length() < messageSpace) {
            messageBuilder.append(dictionary.getWords()[r.nextInt(dictionary.getWords().length)]);
            messageBuilder.append(" ");
        }

        return "duledule";
    }

    public Message generateMessageDto() {
        Message message = new Message();
        message.setMessage(generateMessage());

        // save the result in storage
        saveMessage(message);

        return message;
    }

    public String generateSubsetMessage() {
        r = new Random();
        StringBuilder messageBuilder = new StringBuilder();
        Dictionary dictionary = dictionaryGenerator.generateDictionaryDto();

        // TODO - make this more customizable
        // TODO - use shuffling
        // here we assert the first word comes from the first ten entries in the dictionary
        messageBuilder.append(dictionary.getWords()[10]);
        messageBuilder.append(" ");

        while(messageBuilder.length() < messageSpace) {
            messageBuilder.append(dictionary.getWords()[r.nextInt(dictionary.getWords().length)]);
            messageBuilder.append(" ");
        }

        return messageBuilder.subSequence(0, messageSpace).toString();
    }

    /**
     * the section defined below is for interacting with storage
     */

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public Optional<Message> getMessageById(UUID id) {
        return messageRepository.findById(id);
    }

    public void deleteMessageById(UUID id) {
        messageRepository.deleteById(id);
    }

    public void deleteAllMessages() {
        messageRepository.deleteAll();
    }


}
