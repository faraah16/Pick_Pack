package com.example.pick_pack;

public class OrderItem {
    public String name;
    public String material;
    public int quantity;

    public OrderItem(String name, String material, int quantity) {
        this.name = name;
        this.material = material;
        this.quantity = quantity;
    }
}
