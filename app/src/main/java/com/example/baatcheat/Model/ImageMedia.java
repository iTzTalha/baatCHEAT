package com.example.baatcheat.Model;

public class ImageMedia {
    private String sender;
    private String receiver;
    private String imageUrl;
    private boolean seen;

    public ImageMedia(String sender, String receiver, String imageUrl, boolean seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.imageUrl = imageUrl;
        this.seen = seen;
    }

    public ImageMedia() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
