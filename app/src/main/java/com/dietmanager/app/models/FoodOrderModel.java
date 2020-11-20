package com.dietmanager.app.models;

import java.util.ArrayList;
import java.util.List;

public class FoodOrderModel {

    String header;
    List<FoodOrder> orders = new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<FoodOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<FoodOrder> orders) {
        this.orders = orders;
    }

}
