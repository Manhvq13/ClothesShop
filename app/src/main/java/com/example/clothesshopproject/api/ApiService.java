package com.example.clothesshopproject.api;
import com.example.clothesshopproject.model.AuthRequest;
import com.example.clothesshopproject.model.AuthResponse;
import com.example.clothesshopproject.model.ChangePasswordRequest;
import com.example.clothesshopproject.model.ForgotPasswordRequest;
import com.example.clothesshopproject.model.User;
import com.example.clothesshopproject.model.admin.AdminProduct;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("api/auth/forgot-password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/users/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequest request);

    @GET("api/users/me")
    Call<User> getProfile();

    @GET("api/admin/products")
    Call<List<AdminProduct>> getAdminProductList(@Header("Authorization") String token);

    @DELETE("api/admin/products/{id}")
    Call<Void> deleteProduct(@Header("Authorization") String token, @Path("id") Long id);
}
