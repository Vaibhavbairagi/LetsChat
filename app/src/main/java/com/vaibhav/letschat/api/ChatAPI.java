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
    Call<StatusResponse> updateFCMToken(@Field("token") String token);

    @FormUrlEncoded
    @POST("/api/callUser")
    Call<StatusResponse> callUser(@Field("receiverFCMToken") String receiverFCMToken, @Field("callType") int type, @Field("callerName") String callerName );
}
