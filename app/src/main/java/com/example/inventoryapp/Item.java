package com.example.inventoryapp;

public class Item
{
    public long itemId;
    public long userId;
    public String name;
    public int qty;
    public int warning;

    public Item() {}

    public Item(Item item) {
        this.itemId = item.itemId;
        this.userId = item.userId;
        this.name = item.name;
        this.qty = item.qty;
        this.warning = item.warning;
    }
}
