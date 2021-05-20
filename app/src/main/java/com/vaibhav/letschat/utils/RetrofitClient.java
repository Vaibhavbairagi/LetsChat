package com.vaibhav.letschat.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "https://aqueous-basin-30393.herokuapp.com";
    public static Retrofit retrofit;

    public static Retrofit getInstance(){
        if (retrofit==null) {
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)   //This is the only mandatory call on Builder object.
                    .addConverterFactory(GsonConverterFactory.create()) // Convertor library used to convert response into POJO
                    .build();
        }
        return retrofit;
    }
}
