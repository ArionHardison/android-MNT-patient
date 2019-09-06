package com.oyola.app.build.api;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.oyola.app.MyApplication;
import com.oyola.app.build.configure.BuildConfigure;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.oyola.app.helper.SharedHelper;


/**
 * Created by tamil@appoets.com on 30-08-2017.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        OkHttpClient client = getClient();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfigure.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .addNetworkInterceptor(new AddHeaderInterceptor())
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build();
        client.connectionPool().evictAll();
        return client;
    }


    public static class AddHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("X-Requested-With", "XMLHttpRequest");
            builder.addHeader("Authorization", "" + SharedHelper.getKey((Context) MyApplication.getContext(),"access_token"));

            Log.e("access_token", SharedHelper.getKey((Context) MyApplication.getContext(),"access_token"));

            return chain.proceed(builder.build());
        }
    }

}
