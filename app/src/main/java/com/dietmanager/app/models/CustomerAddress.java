package com.dietmanager.app.models;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddress {
    String header;
    List<CAddress> addresses = new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<CAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<CAddress> addresses) {
        this.addresses = addresses;
    }
}
