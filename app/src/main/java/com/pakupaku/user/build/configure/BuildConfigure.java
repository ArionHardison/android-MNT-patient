package com.pakupaku.user.build.configure;

/**
 * Created by Tamil on 9/22/2017.
 */

public class BuildConfigure {

    /*  Live Mode*/
//    public static String BASE_URL = "http://foodie.appoets.co/";
//    public static String CLIENT_SECRET = "D05WfB9aCBPCel6St5lOl2Cc1hqBwYoudmqxX7Ti";
//    public static final String PUBNUB_PUBLISH_KEY = "pub-c-7bce9b6e-c9ec-44ad-98c2-35eafbbcacb1";
//    public static final String PUBNUB_SUBSCRIBE_KEY = "sub-c-046e0baa-bb01-11e7-b0ca-ee8767eb9c7d";

    /*   Dev Mode*/
    public static String BASE_URL = "https://pakupakunet.com/";

    public static String CLIENT_SECRET = "2YAAgHbafBwJosrkHVH6qP8srupA3zaHfDA06Ul2";
    public static String CLIENT_ID = "4";

    //Pubnub for Chat
    public static  String PUBNUB_PUBLISH_KEY = "pub-c-97296c9c-1fab-4d99-9d8f-37193407ae7e";
    public static  String PUBNUB_SUBSCRIBE_KEY = "sub-c-68a1a30a-17d2-11e9-b43f-7a31102fe3bb";
    public static   String PUBNUB_CHANNEL_NAME = "21";

    //Stripe for card payment
    public static String STRIPE_PK = "pk_test_I9MhBjA2wYUtmReL3Tc0HM9L";


}
