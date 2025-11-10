package com.example.clothesshopproject.view.admin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.admin.AdminApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.StockResponse;
import com.example.clothesshopproject.model.admin.StockUpdateRequest;
import com.example.clothesshopproject.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStockActivity extends AppCompatActivity {

    private static final String TAG = "AdminStockActivity";
    private Long productId;

    private TextView tvProductName;
    private TextView tvQuantity;
    private TextView tvReserved;
    private TextView tvAvailable;
    private EditText etNewQuantity;
    private Button btnUpdateStock;

    private AdminApiService adminApiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stock);

        productId = getIntent().getLongExtra("PRODUCT_ID", -1L);
        if (productId == -1L) {
            Toast.makeText(this, "Product ID not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Khởi tạo views
        tvProductName = findViewById(R.id.tv_product_name);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvReserved = findViewById(R.id.tv_reserved);
        tvAvailable = findViewById(R.id.tv_available);
        etNewQuantity = findViewById(R.id.et_new_quantity);
        btnUpdateStock = findViewById(R.id.btn_update_stock);

        // Khởi tạo API và SessionManager
        sessionManager = new SessionManager(this);
        // Sửa lỗi: Gọi sessionManager.getToken() thay vì .getAuthToken()
        adminApiService = AdminApiClient.getClient(sessionManager.getToken()).create(AdminApiService.class);

        // Load dữ liệu tồn kho ban đầu
        loadStockData();

        // Thiết lập sự kiện click cho nút cập nhật
        btnUpdateStock.setOnClickListener(v -> updateStock());
    }

    private void loadStockData() {
        adminApiService.getStockByProductId(productId).enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayStock(response.body());
                } else {
                    Toast.makeText(AdminStockActivity.this, "Failed to load stock. Code: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to load stock: " + (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));
                }
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                Toast.makeText(AdminStockActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed: ", t);
            }
        });
    }

    private void displayStock(StockResponse stock) {
        tvProductName.setText("Product: " + stock.getProductName());
        tvQuantity.setText("Quantity (Current Stock): " + stock.getQuantity());
        tvReserved.setText("Reserved (In Orders): " + stock.getReserved());
        tvAvailable.setText("Available (For Sale): " + stock.getAvailable());
        etNewQuantity.setText(String.valueOf(stock.getQuantity()));
    }

    private void updateStock() {
        String newQuantityStr = etNewQuantity.getText().toString().trim();
        if (newQuantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter a new quantity.", Toast.LENGTH_SHORT).show();
            return;
        }
        int newQuantity;
        try {
            newQuantity = Integer.parseInt(newQuantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format.", Toast.LENGTH_SHORT).show();
            return;
        }

        StockUpdateRequest request = new StockUpdateRequest(newQuantity);

        btnUpdateStock.setEnabled(false); // Disable button to prevent multiple clicks
        adminApiService.updateStockQuantity(productId, request).enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                btnUpdateStock.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminStockActivity.this, "Stock updated successfully!", Toast.LENGTH_SHORT).show();
                    displayStock(response.body()); // Cập nhật lại UI với dữ liệu mới
                } else {
                    String errorMsg = "Update failed. Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            // Cố gắng lấy thông báo lỗi từ body nếu có
                            errorMsg += " (Error: " + response.errorBody().string() + ")";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: ", e);
                    }
                    Toast.makeText(AdminStockActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Update failed error body: " + (response.errorBody() != null ? response.errorBody().toString() : "No error body"));
                }
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                btnUpdateStock.setEnabled(true);
                Toast.makeText(AdminStockActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed: ", t);
            }
        });
    }
}