package com.dietmanager.app.build.api;

/**
 * Created by tamil@appoets.com on 30-08-2017.
 */

import com.dietmanager.app.models.CAddress;
import com.dietmanager.app.models.CuisinesModel;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.AddMoney;
import com.dietmanager.app.models.Address;
import com.dietmanager.app.models.Card;
import com.dietmanager.app.models.ChangePassword;
import com.dietmanager.app.models.ClearCart;
import com.dietmanager.app.models.Cuisine;
import com.dietmanager.app.models.CustomerAddress;
import com.dietmanager.app.models.DisputeMessage;
import com.dietmanager.app.models.Favorite;
import com.dietmanager.app.models.FavoriteList;
import com.dietmanager.app.models.FoodOrder;
import com.dietmanager.app.models.ForgotPassword;
import com.dietmanager.app.models.LoginModel;
import com.dietmanager.app.models.Message;
import com.dietmanager.app.models.Order;
import com.dietmanager.app.models.Otp;
import com.dietmanager.app.models.PlaceOrderResponse;
import com.dietmanager.app.models.PromotionResponse;
import com.dietmanager.app.models.Promotions;
import com.dietmanager.app.models.RegisterModel;
import com.dietmanager.app.models.ResetPassword;
import com.dietmanager.app.models.RestaurantsData;
import com.dietmanager.app.models.SaveCustomerAddress;
import com.dietmanager.app.models.Search;
import com.dietmanager.app.models.ShopDetail;
import com.dietmanager.app.models.SubscriptionList;
import com.dietmanager.app.models.User;
import com.dietmanager.app.models.WalletHistory;
import com.dietmanager.app.models.food.FoodItem;
import com.dietmanager.app.models.food.FoodResponse;
import com.dietmanager.app.models.timecategory.TimeCategoryItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {


    /*-------------USER--------------------*/

    @GET("api/user/profile")
    Call<User> getProfile(@QueryMap HashMap<String, String> params);


    @FormUrlEncoded
    @POST("api/user/chat/push")
    Call<Object> chatPost(@FieldMap HashMap<String, String> paramss);

    @Multipart
    @POST("api/user/profile")
    Call<User> updateProfileWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part filename);

    @FormUrlEncoded
    @POST("api/user/otp")
    Call<Otp> postOtp(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/register")
    Call<RegisterModel> postRegister(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("oauth/token")
    Call<LoginModel> postLogin(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/social/login")
    Call<LoginModel> postSocialLogin(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/forgot/password")
    Call<ForgotPassword> forgotPassword(@Field("phone") String mobile);

    @FormUrlEncoded
    @POST("api/user/reset/password")
    Call<ResetPassword> resetPassword(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/profile/password")
    Call<ChangePassword> changePassword(@FieldMap HashMap<String, String> params);


    @GET("api/user/notification")
    Call<FavoriteList> getNotification();

    /*-------------SHOP--------------------*/

    @GET("api/user/shops")
    Call<RestaurantsData> getshops(@QueryMap HashMap<String, String> params);

    @GET("api/user/categories")
    Call<ShopDetail> getCategories(@QueryMap HashMap<String, String> params);


    /*-------------CUISINE--------------------*/
    @GET("api/user/cuisines")
    Call<List<Cuisine>> getcuCuisineCall();

    /*-------------CART--------------------*/

    @FormUrlEncoded
    @POST("api/user/cart")
    Call<AddCart> postAddCart(@FieldMap HashMap<String, String> params);

    @GET("api/user/cart")
    Call<AddCart> getViewCart();

    @GET("api/user/apply/promocode?")
    Call<AddCart> getViewCartPromocode(@Query("promocode_id") String promocode_id);

    @GET("api/user/clear/cart")
    Call<ClearCart> clearCart();

    @FormUrlEncoded
    @POST("api/user/order")
    Call<Order> postCheckout(@FieldMap HashMap<String, String> params);

    /*-------------ADDRESS--------------------*/

    @GET("api/user/address")
    Call<List<Address>> getAddresses();

    @GET("api/user/customer/address")
    Call<List<Address>> getCustomerAddresses();

    @POST("api/user/address")
    Call<Address> saveAddress(@Body Address address,@Query("update") String update);

    @POST("api/user/customer/address")
    Call<SaveCustomerAddress> saveCustomerAddress(@Body Address address);

    @GET("api/user/unsubscribe")
    Call<Message> unsubscribe();

    @PATCH("api/user/address/{id}")
    Call<Address> updateAddress(@Path("id") int id, @Body Address address);

    @DELETE("api/user/address/{id}")
    Call<Message> deleteAddress(@Path("id") int id);

    /*-------------FAVORITE--------------------*/

    @FormUrlEncoded
    @POST("api/user/favorite")
    Call<Favorite> doFavorite(@Field("shop_id") int shop_id);

    @DELETE("api/user/favorite/{id}")
    Call<Favorite> deleteFavorite(@Path("id") int id);

    @GET("api/user/favorite")
    Call<FavoriteList> getFavoriteList();

    /*-------------ORDER--------------------*/

    @GET("api/user/ongoing/order")
    Call<List<Order>> getOngoingOrders();

    @GET("api/user/requests")
    Call<List<FoodOrder>> getOngoingFoodOrders();

    @GET("api/user/request/{id}")
    Call<List<FoodOrder>> getParticularOrders(@Path("id") int id);

   /* @GET("api/user/order/{id}")
    Call<Order> getParticularOrders(@Path("id") int id);*/

    @GET("api/user/order")
    Call<List<Order>> getPastOders();

    @DELETE("api/user/order/{id}")
    Call<Order> cancelOrder(@Path("id") int id, @Query("reason") String reason);


    @FormUrlEncoded
    @POST("api/user/order/{id}")
    Call<Order> updateOrder(@Path("id") int id, @FieldMap HashMap<String, String> params);

    @GET("api/user/respond/invite")
    Call<Message> acceptOrRejectDietitian(@Query("accept") int accept);

    @DELETE("api/user/order/{id}")
    Call<Order> completeOrder(@Path("id") int id, @Query("reason") String reason);

    @FormUrlEncoded
    @POST("api/user/rating")
    Call<Message> rate(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("/api/user/order/{id}/rate")
    Call<Message> chefRate(@Path("id") int id,@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/reorder")
    Call <AddCart> reOrder(@FieldMap HashMap<String, String> params);

     /*-------------DISPUTE--------------------*/

    @GET("api/user/disputehelp")
    Call<List<DisputeMessage>> getDisputeList();

    @FormUrlEncoded
    @POST("api/user/dispute")
    Call<Order> postDispute(@FieldMap HashMap<String, String> params);


    /*-------------SEARCH--------------------*/
    @GET("api/user/search")
    Call<Search> getSearch(@QueryMap HashMap<String,String> params);

    /*-----------------------WALLET-----------------------*/
    @GET("api/user/wallet")
    Call<List<WalletHistory>> getWalletHistory();

    @GET("api/user/wallet/promocode")
    Call<List<Promotions>> getWalletPromoCode();

    @FormUrlEncoded
    @POST("api/user/wallet/promocode")
    Call<PromotionResponse> applyWalletPromoCode(@Field("promocode_id") String id);

    @FormUrlEncoded
    @POST("api/user/wallet/promocode")
    Call<PromotionResponse> applyWalletPromoCode(@Field("promocode_id") String id,@Field("promocode_code") String promocode_code);

    @GET("api/user/apply/promocode")
    Call<AddCart> applyPromocode(@Query("promocode_id") String param1);


    @GET("json?")
    Call<ResponseBody> getResponse(@Query("latlng") String param1, @Query("key") String param2);

    /*-------------PAYMENT--------------------*/
    @GET("api/user/card")
    Call<List<Card>> getCardList();

    @FormUrlEncoded
    @POST("api/user/card")
    Call<Message> addCard(@Field("stripe_token") String stripeToken);

    @FormUrlEncoded
    @POST("api/user/add/money")
    Call<AddMoney> addMoney(@FieldMap HashMap<String, String> params);

    @DELETE("api/user/card/{id}")
    Call<Message> deleteCard(@Path("id") int id);

    @FormUrlEncoded
    @POST("send/otp")
    Call<Otp> sendOtp(@FieldMap HashMap<String, String> params);

    @GET("api/user/favorite-cuisines")
    Call<List<Cuisine>> getCuisines();

//    @FormUrlEncoded
    @POST("api/user/profile")
//    Call<Object> updateCuisines(@FieldMap() Map<String, Integer> mMap);
    Call<Object> updateCuisines(@Body() CuisinesModel mMap);

   /* subscription*/

    @GET("api/user/subscription")
    Call<List<SubscriptionList>> getsubscription();

   /* home*/
   @GET("api/user/time/category")
   Call<List<TimeCategoryItem>> getTimeCategory();

    @GET("api/user/diet/meal")
    Call<List<FoodItem>> getFood(@Query("day") Integer param1, @Query("category_id") Integer param2);

    @FormUrlEncoded
    @POST("api/user/subscription")
    Call<Otp> postsubscribe(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/place/order")
    Call<PlaceOrderResponse> placeorder(@FieldMap HashMap<String, String> params);

}
