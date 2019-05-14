package org.nyu.crypto.dto;


import javax.persistence.*;
import java.util.UUID;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 500)
    private String message;

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }
}
