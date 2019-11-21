package com.example.baatcheat.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean seen;

    public Chat(String sender, String receiver, String message, boolean seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.seen = seen;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
