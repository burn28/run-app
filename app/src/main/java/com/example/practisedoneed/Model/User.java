package com.example.practisedoneed.Model;

public class User {

    private  String id ;
    private  String username ;
    private  String email ;
    private  String imageUrl ;
    private  String phone ;

    public User(String id, String username, String email, String imageUrl, String phone) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.phone = phone;
    }

    public User() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phoneNumber) {
        this.phone = phoneNumber;
    }
}
