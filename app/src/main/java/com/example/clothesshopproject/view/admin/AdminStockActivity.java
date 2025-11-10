package com.example.clothesshopproject.view.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.StockResponse;
import com.example.clothesshopproject.model.admin.StockUpdateRequest;
import com.example.clothesshopproject.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStockActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductId;
    private TextView tvCurrentQuantity, tvCurrentReserved, tvCurrentAvailable;
    private EditText etNewQuantity;
    private Button btnUpdateStock;
    private ProgressBar progressBar;
    private Button btnBackToProducts;
    private Long productId;
    private AdminApiService adminApiService;
    private SessionManager sessionManager;
    private StockResponse currentStockData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stock);

        tvProductName = findViewById(R.id.tv_stock_product_name);
        tvProductId = findViewById(R.id.tv_stock_product_id);
        tvCurrentQuantity = findViewById(R.id.tv_current_quantity);
        tvCurrentReserved = findViewById(R.id.tv_current_reserved);
        tvCurrentAvailable = findViewById(R.id.tv_current_available);
        etNewQuantity = findViewById(R.id.et_new_quantity);
        btnUpdateStock = findViewById(R.id.btn_update_stock);
        progressBar = findViewById(R.id.progress_bar);
        btnBackToProducts = findViewById(R.id.btn_back_to_products);
        adminApiService = ApiClient.getClient(this).create(AdminApiService.class);
        sessionManager = new SessionManager(this);

        // Lấy ID sản phẩm từ Intent
        productId = getIntent().getLongExtra("PRODUCT_ID", -1L);
        if (productId == -1L) {
            Toast.makeText(this, "Lỗi: Không có ID sản phẩm.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProductId.setText("ID Sản phẩm: " + productId);
        fetchStockDetails();

        btnUpdateStock.setOnClickListener(v -> updateStockQuantity());
        btnBackToProducts.setOnClickListener(v -> finish());
    }

    private void fetchStockDetails() {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        adminApiService.getStockByProductId(token, productId).enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentStockData = response.body();
                    displayStockDetails(currentStockData);
                } else {
                    Toast.makeText(AdminStockActivity.this, "Lỗi tải tồn kho. Code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminStockActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayStockDetails(StockResponse stock) {
        tvProductName.setText("Sản phẩm: " + (stock.getProductName() != null ? stock.getProductName() : "Đang tải..."));
        tvCurrentQuantity.setText(String.valueOf(stock.getQuantity()));
        tvCurrentReserved.setText(String.valueOf(stock.getReserved()));
        tvCurrentAvailable.setText(String.valueOf(stock.getAvailable()));

        etNewQuantity.setText(String.valueOf(stock.getQuantity()));
    }

    private void updateStockQuantity() {
        if (currentStockData == null) {
            Toast.makeText(this, "Không có dữ liệu tồn kho để cập nhật.", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityStr = etNewQuantity.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            etNewQuantity.setError("Không được để trống.");
            return;
        }

        int newQuantity;
        try {
            newQuantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            etNewQuantity.setError("Số lượng không hợp lệ.");
            return;
        }

        if (newQuantity < 0) {
            etNewQuantity.setError("Số lượng không thể âm.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Lấy Token và chuẩn bị Request DTO
        String token = "Bearer " + sessionManager.getToken();

        StockUpdateRequest request = new StockUpdateRequest();
        request.setProductId(productId);
        request.setQuantity(newQuantity);

        adminApiService.updateStockQuantity(token, request).enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentStockData = response.body();
                    displayStockDetails(currentStockData);
                    Toast.makeText(AdminStockActivity.this, "Cập nhật tồn kho thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(AdminStockActivity.this,
                                "Cập nhật thất bại. Mã lỗi: " + response.code() + ". Chi tiết: " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                        Toast.makeText(AdminStockActivity.this, "Cập nhật thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminStockActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}