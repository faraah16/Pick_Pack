package com.example.pick_pack.Model;

public class CartItem {

    private String name;        // Red Cube / Green Cube / Blue Cube
    private String size;        // S / M / L
    private String material;    // Bois / Carton / Metal / Plastique
    private int quantity;
    private double price;       // PRIX UNITAIRE
    private int imageResId;

    public CartItem(
            String name,
            String size,
            String material,
            int quantity,
            double price,
            int imageResId
    ) {
        this.name = name;
        this.size = size;
        this.material = material;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
    }

    // ===== GETTERS =====
    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getMaterial() {
        return material;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    // ✅ TOTAL = prix unitaire × quantité
    public double getTotalPrice() {
        return price * quantity;
    }

    // ===== SETTERS =====
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
