package com.example.clothesshopproject.api;
import com.example.clothesshopproject.model.AuthRequest;
import com.example.clothesshopproject.model.AuthResponse;
import com.example.clothesshopproject.model.ChangePasswordRequest;
import com.example.clothesshopproject.model.ForgotPasswordRequest;
import com.example.clothesshopproject.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
}
