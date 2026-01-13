package com.example.pick_pack;

import java.util.List;

public class TrackedOrder {
    public String orderId;
    public String clientName;
    public String phone;
    public String address;
    public String deliveryType;
    public String paymentMethod;
    public double total;
    public String status; // Pending / Preparing / Delivered
    public List<OrderItem> items;
}
