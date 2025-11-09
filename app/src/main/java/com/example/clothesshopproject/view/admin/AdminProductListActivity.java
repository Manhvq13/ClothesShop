package com.example.clothesshopproject.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.AdminProduct;
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.view.adapter.AdminProductAdapter;
import com.example.clothesshopproject.MainActivity;

import java.util.List;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AdminApiService adminApiService;
    private SessionManager sessionManager;
    private AdminProductAdapter adapter;
    private List<AdminProduct> productList = Collections.emptyList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // KIỂM TRA QUYỀN ADMIN (DỰA TRÊN ROLE LƯU CỤC BỘ)
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Không có quyền truy cập Admin.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_admin_product_list);

        recyclerView = findViewById(R.id.rv_product_list);
        progressBar = findViewById(R.id.progress_bar);

        adminApiService = ApiClient.getClient(this).create(AdminApiService.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchAdminProducts();

        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProductActivity.class);
            startActivity(intent);
        });
    }

    public void fetchAdminProducts() {
        progressBar.setVisibility(View.VISIBLE);

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Phiên đăng nhập hết hạn.", Toast.LENGTH_LONG).show();
            return;
        }

        adminApiService.getAllProductsForAdmin("Bearer " + token).enqueue(new Callback<List<AdminProduct>>() {
            @Override
            public void onResponse(Call<List<AdminProduct>> call, Response<List<AdminProduct>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    if (adapter == null) {
                        adapter = new AdminProductAdapter(AdminProductListActivity.this, productList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateList(productList);
                    }

                } else {
                    String message = "Lỗi tải dữ liệu. Mã lỗi: " + response.code();
                    if (response.code() == 403) { // 403 Forbidden từ Backend (AdminAuthService)
                        message = "Không có quyền truy cập. Vui lòng đăng nhập lại với tài khoản Admin.";
                    }
                    Toast.makeText(AdminProductListActivity.this, message, Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Response Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<AdminProduct>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_FAILURE", "Call Failed: " + t.getMessage());
                Toast.makeText(AdminProductListActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isAdmin()) {
            fetchAdminProducts();
        }
    }
}