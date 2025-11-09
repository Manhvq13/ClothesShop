package com.example.clothesshopproject.api.admin;



import com.example.clothesshopproject.model.admin.AdminProduct;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
}