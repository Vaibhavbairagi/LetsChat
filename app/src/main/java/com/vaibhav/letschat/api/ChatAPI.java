package com.vaibhav.letschat.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChatAPI {

    @FormUrlEncoded
    @POST("/api/createAccessToken")
    Call<AccessTokenResponse> generateNewAccessToken(@Field("userID") String userID);

    @FormUrlEncoded
    @POST("/api/updateFCMToken")
    Call<UpdateFCMTokenResponse> updateFCMToken(@Field("token") String token);

}
