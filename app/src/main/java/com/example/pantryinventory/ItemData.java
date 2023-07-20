package com.example.pantryinventory;

import java.io.Serializable;

public class ItemData implements Serializable {
    private String imageUrl;
    private String foodName;
    private String expDate;

    // Assume there is no argument constructor and getter setter here

    public ItemData() {
    }

    public ItemData(String imageUrl, String foodName, String expDate) {
        this.imageUrl = imageUrl;
        this.foodName = foodName;
        this.expDate = expDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
