package com.dietmanager.app.models;

/**
 * Created by Prasanth on 16-12-2019
 */
public class CategoryModel {
    String name;
    boolean isSelected;

    public CategoryModel(String name,boolean selected){
        this.name=name;
        this.isSelected=selected;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
