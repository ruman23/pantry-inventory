package com.example.pantryinventory;

public class ItemData {
    private String imageUrl;
    private String foodName;
    private String expDate;

    public ItemData() {
        // no-argument constructor
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
