package com.example.pantryinventory;

public class ItemData {
    private int imageResourceId;
    private String foodName;
    private String expDate;

    public ItemData(int imageResourceId, String foodName, String subtitle) {
        this.imageResourceId = imageResourceId;
        this.foodName = foodName;
        this.expDate = subtitle;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getExpDate() {
        return expDate;
    }
}