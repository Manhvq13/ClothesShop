package com.example.clothesshopproject.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.AdminProduct;
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.MainActivity;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductActivity extends AppCompatActivity {

    private EditText etSku, etName, etPrice, etSalePrice;
    private CheckBox cbIsActive;
    private Button btnSave;
    private TextView tvTitle;
    private ProgressBar progressBar;

    private Long productId = null;
    private AdminApiService adminApiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // KIỂM TRA QUYỀN ADMIN (DỰA TRÊN ROLE LƯU CỤC BỘ)
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền chỉnh sửa sản phẩm.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_admin_product);

        initViews();
        adminApiService = ApiClient.getClient(this).create(AdminApiService.class);

        // Kiểm tra chế độ
        productId = getIntent().getLongExtra("PRODUCT_ID", -1L);
        if (productId != -1L) {
            tvTitle.setText("Chỉnh sửa Sản phẩm (ID: " + productId + ")");
            btnSave.setText("Lưu Thay Đổi");
            fetchProductDetails(productId);
        } else {
            tvTitle.setText("Thêm Sản phẩm Mới");
            btnSave.setText("Tạo Sản Phẩm");
        }

        btnSave.setOnClickListener(v -> validateAndSaveProduct());
    }

    private void initViews() {
        // Giả định ID từ layout activity_admin_product.xml
        tvTitle = findViewById(R.id.tv_admin_product_title);
        etSku = findViewById(R.id.et_product_sku);
        etName = findViewById(R.id.et_product_name);
        etPrice = findViewById(R.id.et_product_price);
        etSalePrice = findViewById(R.id.et_product_sale_price);
        cbIsActive = findViewById(R.id.cb_product_is_active);
        btnSave = findViewById(R.id.btn_save_product);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void fetchProductDetails(Long id) {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();
        adminApiService.getProductById(token, id).enqueue(new Callback<AdminProduct>() {
            @Override
            public void onResponse(Call<AdminProduct> call, Response<AdminProduct> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    populateFields(response.body());
                } else {
                    Toast.makeText(AdminProductActivity.this, "Không tải được chi tiết sản phẩm. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminProduct> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminProductActivity.this, "Lỗi kết nối khi tải chi tiết.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(AdminProduct product) {
        etSku.setText(product.getSku());
        etName.setText(product.getName());
        etPrice.setText(product.getPrice().toPlainString());
        if (product.getSalePrice() != null) {
            etSalePrice.setText(product.getSalePrice().toPlainString());
        }
        cbIsActive.setChecked(product.isActive() != null ? product.isActive() : false);
    }

    private void validateAndSaveProduct() {
        String sku = etSku.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String salePriceStr = etSalePrice.getText().toString().trim();
        boolean isActive = cbIsActive.isChecked();

        if (sku.isEmpty() || name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ SKU, Tên và Giá.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            BigDecimal salePrice = salePriceStr.isEmpty() ? null : new BigDecimal(salePriceStr);

            AdminProduct productToSave = new AdminProduct();
            if (productId != -1L) {
                productToSave.setId(productId);
            }
            productToSave.setSku(sku);
            productToSave.setName(name);
            productToSave.setPrice(price);
            productToSave.setSalePrice(salePrice);
            productToSave.setActive(isActive);

            performSaveApiCall(productToSave);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá tiền không hợp lệ.", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSaveApiCall(AdminProduct productToSave) {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        String token = "Bearer " + sessionManager.getToken();

        Call<AdminProduct> call;

        if (productId != -1L) {
            // UPDATE (PUT)
            call = adminApiService.updateProduct(token, productId, productToSave);
        } else {
            // CREATE (POST)
            call = adminApiService.createProduct(token, productToSave);
        }

        call.enqueue(new Callback<AdminProduct>() {
            @Override
            public void onResponse(Call<AdminProduct> call, Response<AdminProduct> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful()) {
                    String action = (productId != -1L) ? "cập nhật" : "tạo mới";
                    Toast.makeText(AdminProductActivity.this, "Đã " + action + " sản phẩm thành công!", Toast.LENGTH_LONG).show();

                    // Quay lại màn hình danh sách và refresh
                    Intent intent = new Intent(AdminProductActivity.this, AdminProductListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e("API_SAVE", "Response Code: " + response.code());
                    Toast.makeText(AdminProductActivity.this, "Lỗi khi lưu sản phẩm. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AdminProduct> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e("API_FAILURE", "Save Failed: " + t.getMessage());
                Toast.makeText(AdminProductActivity.this, "Lỗi kết nối mạng.", Toast.LENGTH_LONG).show();
            }
        });
    }
}