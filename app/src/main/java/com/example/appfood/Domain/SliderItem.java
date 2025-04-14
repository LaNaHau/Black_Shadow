package com.example.appfood.Domain;

public class SliderItem {
    private String image;

    public SliderItem() {
    }

    @Override
    public String toString() {
        return "SliderItem{" +
                "image='" + image + '\'' +
                '}';
    }

    public SliderItem(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

