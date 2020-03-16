package com.example.kioskprototype.Retrofit;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;
import io.reactivex.Observable;

public interface INodeJS {
    @POST("register")
    @FormUrlEncoded
    Observable <String> registerUser(@Field("name") String name,
                                     @Field("email") String email,
                                     @Field("password") String password,
                                     @Field("phone") String phone);

    @POST("login")
    @FormUrlEncoded
    Observable <String> loginUser(@Field("email") String email,
                                     @Field("password") String password);
}
