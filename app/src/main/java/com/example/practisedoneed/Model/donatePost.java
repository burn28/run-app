package com.example.practisedoneed.Model;

public class donatePost {

    private  String id;
    private  String image;
    private  String donator;
    private  String title;
    private  String description;
    private  String quantity;
    private  String location;
    private  String category;

    public donatePost(String id, String image, String donator, String title, String description, String quantity, String location, String category) {
        this.id = id;
        this.image = image;
        this.donator = donator;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.location = location;
        this.category = category;
    }

    public donatePost(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDonator() {
        return donator;
    }

    public void setDonator(String donator) {
        this.donator = donator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
