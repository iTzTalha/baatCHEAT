package com.example.baatcheat.Model;

public class User {
    private String id;
    private String username;
    private String phone;
    private String bio;
    private String imageUrl;
    private String status;
    private String search;
    private String contactName;

    public User(String id, String username, String phone, String bio, String imageUrl, String status, String search,String contactName) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
        this.contactName = contactName;
    }

    public User() {
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
