package com.dietmanager.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Prasanth on 17-10-2019.
 */
public class CuisinesModel {

    @SerializedName("favourite")
    List<Integer> integerList;

    public List<Integer> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<Integer> integerList) {
        this.integerList = integerList;
    }
}
