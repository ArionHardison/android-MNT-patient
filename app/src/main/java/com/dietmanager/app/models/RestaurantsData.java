
package com.dietmanager.app.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantsData {

    @SerializedName("shops")
    @Expose
    private List<Shop> shops;
    @SerializedName("banners")
    @Expose
    private List<Banner> banners;
    @SerializedName("favorite_cuisine_shop")
    @Expose
    private List<Shop> favouriteCuisines;
    @SerializedName("freedelivery_shops")
    @Expose
    private List<Shop> freeDeliveryShops;

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<Shop> getFavouriteCuisines() {
        return favouriteCuisines;
    }

    public void setFavouriteCuisines(List<Shop> favouriteCuisines) {
        this.favouriteCuisines = favouriteCuisines;
    }

    public List<Shop> getFreeDeliveryShops() {
        return freeDeliveryShops;
    }

    public void setFreeDeliveryShops(List<Shop> freeDeliveryShops) {
        this.freeDeliveryShops = freeDeliveryShops;
    }
}
