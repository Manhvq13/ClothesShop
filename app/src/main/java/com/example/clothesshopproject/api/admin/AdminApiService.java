package com.example.clothesshopproject.api.admin;

import com.example.clothesshopproject.model.User;
import com.example.clothesshopproject.model.admin.AdminProduct;
import com.example.clothesshopproject.model.admin.ImageUploadResponse;
import com.example.clothesshopproject.model.admin.PageResponse;
import com.example.clothesshopproject.model.admin.ProductImageResponse;
import com.example.clothesshopproject.model.admin.StockResponse;
import com.example.clothesshopproject.model.admin.StockUpdateRequest;
import com.example.clothesshopproject.model.admin.UserResponse;
import com.example.clothesshopproject.model.admin.UserUpdateRequest;
import com.example.clothesshopproject.model.admin.UserUpdateRoleRequest;
import com.example.clothesshopproject.model.admin.UserUpdateStatusRequest;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminApiService {

    @GET("api/admin/products")
    Call<List<AdminProduct>> getAllProductsForAdmin(@Header("Authorization") String token);

    @GET("api/admin/products/{id}")
    Call<AdminProduct> getProductById(@Header("Authorization") String token, @Path("id") Long id);
    @DELETE("api/admin/products/{id}")
    Call<Void> deleteProduct(@Header("Authorization") String token, @Path("id") Long id);

    @POST("api/admin/products")
    Call<AdminProduct> createProduct(@Header("Authorization") String token, @Body AdminProduct product);

    @PUT("api/admin/products/{id}")
    Call<AdminProduct> updateProduct(@Header("Authorization") String token, @Path("id") Long id, @Body AdminProduct product);

    @GET("admin/users")
    Call<List<User>> getAllUsers(@Header("Authorization") String authToken);

    @PUT("admin/users/{userId}/role")
    Call<User> updateRole(
            @Header("Authorization") String authToken,
            @Path("userId") long userId,
            @Body UserUpdateRoleRequest request
    );

    @PUT("admin/users/{userId}/status")
    Call<User> updateStatus(
            @Header("Authorization") String authToken,
            @Path("userId") long userId,
            @Body UserUpdateStatusRequest request
    );

    @GET("api/v1/admin/stock/{productId}")
    Call<StockResponse> getStockByProductId(@Path("productId") Long productId);

    // ĐÃ SỬA: Thay thế Long productId bằng String token
    @PUT("api/v1/admin/stock")
    Call<StockResponse> updateStockQuantity(
            @Header("Authorization") String token,
            @Body StockUpdateRequest request
    );

    @GET("admin/users")
    Call<List<UserResponse>> getAllUsers(
            @Query("keyword") String keyword,
            @Query("roleName") String roleName
    );

    @GET("api/admin/products")
    Call<PageResponse<AdminProduct>> getAdminProducts(
            @Header("Authorization") String token,
            @Query("name") String name,
            @Query("categoryId") Integer categoryId,
            @Query("sortBy") String sortBy,
            @Query("page") int page,
            @Query("size") int size
    );
    @PUT("admin/users/{userId}/role")
    Call<UserResponse> updateRole(
            @Path("userId") Long userId,
            @Body UserUpdateRoleRequest request
    );

    @PUT("admin/users/{userId}/status")
    Call<UserResponse> updateStatus(
            @Path("userId") Long userId,
            @Body UserUpdateStatusRequest request
    );
    @Multipart
    @POST("admin/images/upload")
    Call<ImageUploadResponse> uploadImage(@Part MultipartBody.Part file);

    @Multipart
    @POST("admin/products/{productId}/images")
    Call<ProductImageResponse> uploadProductImage(
            @Path("productId") Long productId,
            @Part MultipartBody.Part file
    );
    @PUT("admin/users/{userId}")
    Call<UserResponse> updateUserDetails(
            @Path("userId") Long userId,
            @Body UserUpdateRequest request
    );
}