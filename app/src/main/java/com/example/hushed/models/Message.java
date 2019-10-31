package com.example.hushed.models;

import java.util.UUID;

/** Model for holding information about a single message sent over the chat app. */
public class Message {

    /** Message content. May be encrypted. */
    public final String content;
    /** ID of sender. */
    public final String sender;
    /** ID of message itself */
    public final String id;
    /** Timestamp message was sent at */
    public final long time;

    // Even though fields are public, method references can be cleaner sometimes.
    public String getContent() { return content; }
    public String getSender() { return sender; }
    public String getId() { return id; }
    public long getTime() { return time; }

    /** Constructs a message with the given content/sender,
     * and with a new ID and current timestamp */
    public Message(String content, String sender) {
        this.content = content;
        this.sender = sender;
        id = UUID.randomUUID().toString();
        time = System.currentTimeMillis();
    }
    /** Constructs a message with the given content/partner/id/timestamp */
    public Message(String content, String sender, String id, long time) {
        this.content = content;
        this.sender = sender;
        this.id = id;
        this.time = time;
    }

    public int compareByTime(Message other) {
        if (time < other.time) { return -1; }
        if (time > other.time) { return  1; }
        return 0;
    }



}
