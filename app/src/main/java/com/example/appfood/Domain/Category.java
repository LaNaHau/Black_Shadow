package com.example.appfood.Domain;

public class Category {
    private int Id;
    private String ImagePath;
    private String Name;

    @Override
    public String toString() {
        return "Category{" +
                "Id=" + Id +
                ", ImagePath='" + ImagePath + '\'' +
                ", Name='" + Name + '\'' +
                '}';
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Category() {
    }

    public Category(String name, String imagePath, int id) {
        Name = name;
        ImagePath = imagePath;
        Id = id;
    }
}
