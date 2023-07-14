package com.example.pantryinventory;

public class ItemData {
    private int imageResourceId;
    private String title;
    private String subtitle;

    public ItemData(int imageResourceId, String title, String subtitle) {
        this.imageResourceId = imageResourceId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}