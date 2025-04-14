package com.example.appfood.Domain;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class Foods implements Serializable {
    @PropertyName("Id")
    public int Id;
    @PropertyName("Title")
    public String Title;
    @PropertyName("Description")
    public String Description;
    @PropertyName("ImagePath")
    public String ImagePath;
    @PropertyName("LocationId")
    public int LocationId;
    @PropertyName("Price")
    public double Price;
    @PropertyName("PriceId")
    public int PriceId;
    @PropertyName("Star")
    public double Star;
    @PropertyName("TimeId")
    public int TimeId;
    @PropertyName("TimeValue")
    public int TimeValue;
    @PropertyName("BestFood")
    public boolean BestFood;

    @PropertyName("CategoryId")
    public int CategoryId;
    private int numberInCart;

    @Override
    public String toString() {
        return "Foods{" +
                "Id=" + Id +
                ", Title='" + Title + '\'' +
                ", Description='" + Description + '\'' +
                ", ImagePath='" + ImagePath + '\'' +
                ", LocationId=" + LocationId +
                ", Price=" + Price +
                ", PriceId=" + PriceId +
                ", Star=" + Star +
                ", TimeId=" + TimeId +
                ", TimeValue=" + TimeValue +
                ", BestFood=" + BestFood +
                ", CategoryId=" + CategoryId +
                ", numberInCart=" + numberInCart +
                '}';
    }

    public Foods(int Id, String Title, String Description,
                 String ImagePath, int LocationId, double Price,
                 int priceId, double star, int timeId, int timeValue,
                 boolean BestFood, int categoryId, int numberInCart) {
        this.Id = Id;
        this.Title = Title;
        this.Description = Description;
        this.ImagePath = ImagePath;
        this.LocationId = LocationId;
        this.Price = Price;
        this.PriceId = priceId;
        this.Star = star;
        this.TimeId = timeId;
        this.TimeValue = timeValue;
        this.BestFood = BestFood;
        this.CategoryId = categoryId;
        this.numberInCart = numberInCart;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }
    @PropertyName("Id")
    public int getId() {
        return Id;
    }

    @PropertyName("Id")
    public void setId(int id) {
        Id = id;
    }

    @PropertyName("Title")
    public String getTitle() {
        return Title;
    }

    @PropertyName("Title")
    public void setTitle(String title) {
        Title = title;
    }

    @PropertyName("Description")
    public String getDescription() {
        return Description;
    }

    @PropertyName("Description")
    public void setDescription(String description) {
        Description = description;
    }

    @PropertyName("ImagePath")
    public String getImagePath() {
        return ImagePath;
    }

    @PropertyName("ImagePath")
    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    @PropertyName("LocationId")
    public int getLocationId() {
        return LocationId;
    }

    @PropertyName("LocationId")
    public void setLocationId(int locationId) {
        LocationId = locationId;
    }

    @PropertyName("Price")
    public double getPrice() {
        return Price;
    }

    @PropertyName("Price")
    public void setPrice(double price) {
        Price = price;
    }

    @PropertyName("PriceId")
    public int getPriceId() {
        return PriceId;
    }

    @PropertyName("PriceId")
    public void setPriceId(int priceId) {
        PriceId = priceId;
    }

    @PropertyName("Star")
    public double getStar() {
        return Star;
    }

    @PropertyName("Star")
    public void setStar(double star) {
        Star = star;
    }

    @PropertyName("TimeId")
    public int getTimeId() {
        return TimeId;
    }

    @PropertyName("TimeId")
    public void setTimeId(int timeId) {
        TimeId = timeId;
    }

    @PropertyName("TimeValue")
    public int getTimeValue() {
        return TimeValue;
    }

    @PropertyName("TimeValue")
    public void setTimeValue(int timeValue) {
        TimeValue = timeValue;
    }

    @PropertyName("BestFood")
    public boolean isBestFood() {
        return BestFood;
    }

    @PropertyName("BestFood")
    public void setBestFood(boolean bestFood) {
        BestFood = bestFood;
    }

    @PropertyName("CategoryId")
    public int getCategoryId() {
        return CategoryId;
    }

    @PropertyName("CategoryId")
    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }
    public Foods() {
    }


}
