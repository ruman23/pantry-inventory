package com.example.pantryinventory;

public class ItemData {
    private int imageResourceId;
    private String foodName;
    private String expDate;

    public ItemData() {
        // no-argument constructor
    }

    public ItemData(int imageResourceId, String foodName, String expDate) {
        this.imageResourceId = imageResourceId;
        this.foodName = foodName;
        this.expDate = expDate;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
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
