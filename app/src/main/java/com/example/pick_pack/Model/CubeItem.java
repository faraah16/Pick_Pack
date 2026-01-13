package com.example.pick_pack.Model;

public class CubeItem {

    private String name;
    private String description;
    private int imageResId;
    private double basePrice;
    private boolean outOfStock;

    public CubeItem(String name, String description, int imageResId, double basePrice, boolean outOfStock) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
        this.basePrice = basePrice;
        this.outOfStock = outOfStock;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public boolean isOutOfStock() {
        return outOfStock;
    }
}
