package com.oyola.app.helper;

import android.location.Location;

import com.oyola.app.models.AddCart;
import com.oyola.app.models.Address;
import com.oyola.app.models.AddressList;
import com.oyola.app.models.Card;
import com.oyola.app.models.Cart;
import com.oyola.app.models.CartAddon;
import com.oyola.app.models.Category;
import com.oyola.app.models.Cuisine;
import com.oyola.app.models.DisputeMessage;
import com.oyola.app.models.Order;
import com.oyola.app.models.Otp;
import com.oyola.app.models.Product;
import com.oyola.app.models.Shop;
import com.oyola.app.models.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tamil on 9/22/2017.
 */

public class GlobalData {

    public static String hashcode = "";
    public Otp otpModel = null;

    public static double latitude;
    public static double longitude;
    public static String addressHeader = "";
    public static Location CURRENT_LOCATION = null;

    /*------------Filter--------*/
    public static boolean isPureVegApplied = false;
    public static boolean isOfferApplied = false;
    public static boolean shouldContinueService = false;
    public static ArrayList<Integer> cuisineIdArrayList = null;
    public static ArrayList<Card> cardArrayList;
    public static boolean isCardChecked = false;
    public static String loginBy = "manual";
    public static String name, email, access_token, mobileNumber, imageUrl;
    public static String address = "";
    public static int addCartShopId = 0;
    public static User profileModel = null;
    public static Address selectedAddress = null;
    public static Order isSelectedOrder = null;
    public static Product isSelectedProduct = null;
    public static Cart isSelctedCart = null;
    public static List<CartAddon> cartAddons = null;
    public static AddCart addCart = null;

    public static List<Shop> shopList;
    public static List<Cuisine> cuisineList;
    public static List<Category> categoryList = null;
    public static List<Order> onGoingOrderList;
    public static List<DisputeMessage> disputeMessageList;
    public static List<Order> pastOrderList;
    public static AddressList addressList = null;
    public static List<String> ORDER_STATUS = Arrays.asList("ORDERED", "RECEIVED", "ASSIGNED",
            "PROCESSING", "REACHED", "PICKEDUP", "ARRIVED","SCHEDULED","PICKUP_USER","READY",
            "COMPLETED","CANCELLED","'SEARCHING'");
    public static Shop selectedShop;
    public static DisputeMessage isSelectedDispute;

    public static int otpValue = 0;
    public static String mobile = "";
    public static String currencySymbol = "";
    public static String currency = "";
    public static String privacy = "";
    public static String terms = "";
    public static int notificationCount = 0;

    //Search Fragment
    public static List<Shop> searchShopList;
    public static List<Product> searchProductList;

    public static ArrayList<HashMap<String, String>> foodCart;

    private static final GlobalData ourInstance = new GlobalData();

    public static GlobalData getInstance() {
        return ourInstance;
    }

    public static NumberFormat getNumberFormat() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        numberFormat.setCurrency(Currency.getInstance("INR"));
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat;
    }

    private GlobalData() {
    }

    public static double roundoff(double data) {
        double value = Math.round(data);
        return value;
    }

}
