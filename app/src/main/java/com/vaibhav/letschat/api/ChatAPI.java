package com.vaibhav.letschat.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface ChatAPI {

    @POST("/api/createAccessToken")
    Call<String> generateNewAccessToken(@Field("userID") String userID);
}
