package com.example.eatfood.Model;

public class Catagory {
    private String image ;
    private String name;

    public Catagory() {
    }

    public Catagory(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
