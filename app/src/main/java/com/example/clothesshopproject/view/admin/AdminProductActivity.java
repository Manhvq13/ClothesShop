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
import com.example.clothesshopproject.model.admin.StockResponse;
import com.example.clothesshopproject.model.admin.StockUpdateRequest;
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.MainActivity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductActivity extends AppCompatActivity {

    private EditText etSku, etName, etPrice, etSalePrice;
    private EditText etDescription, etShortDescription;
    private EditText etImageUrl, etImageAltText;
    private EditText etQuantity;
    private CheckBox cbIsActive;
    private Button btnSave;
    private TextView tvTitle;
    private ProgressBar progressBar;

    private Long productId = null;
    private AdminApiService adminApiService;
    private SessionManager sessionManager;

    private Integer newQuantityForStock = null;
    private Long savedProductId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

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
        tvTitle = findViewById(R.id.tv_admin_product_title);
        etSku = findViewById(R.id.et_product_sku);
        etName = findViewById(R.id.et_product_name);
        etQuantity = findViewById(R.id.et_inventory_quantity);

        etDescription = findViewById(R.id.et_product_description);
        etShortDescription = findViewById(R.id.et_product_short_description);

        etImageUrl = findViewById(R.id.et_image_url);
        etImageAltText = findViewById(R.id.et_image_alt_text);

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

        // Đổ dữ liệu Tồn kho
        if (product.getQuantityInStock() != null) {
            etQuantity.setText(String.valueOf(product.getQuantityInStock()));
        }

        // Đổ dữ liệu Description
        if (product.getDescription() != null) {
            etDescription.setText(product.getDescription());
        }
        if (product.getShortDescription() != null) {
            etShortDescription.setText(product.getShortDescription());
        }

        // Đổ dữ liệu ảnh (Chỉ lấy ảnh đầu tiên)
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            AdminProduct.ProductImage firstImage = product.getImages().get(0);
            etImageUrl.setText(firstImage.getUrl());
            // Kiểm tra và đổ dữ liệu Alt Text
            if (firstImage.getAltText() != null) {
                etImageAltText.setText(firstImage.getAltText());
            }
        }

        etPrice.setText(product.getPrice().toPlainString());
        if (product.getSalePrice() != null) {
            etSalePrice.setText(product.getSalePrice().toPlainString());
        }
        cbIsActive.setChecked(product.isActive() != null ? product.isActive() : false);
    }

    private void validateAndSaveProduct() {
        String sku = etSku.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String shortDescription = etShortDescription.getText().toString().trim();

        String imageUrl = etImageUrl.getText().toString().trim();
        String imageAltText = etImageAltText.getText().toString().trim();

        String quantityStr = etQuantity.getText().toString().trim();

        String priceStr = etPrice.getText().toString().trim();
        String salePriceStr = etSalePrice.getText().toString().trim();
        boolean isActive = cbIsActive.isChecked();

        if (sku.isEmpty() || name.isEmpty() || priceStr.isEmpty() || description.isEmpty() || imageUrl.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ SKU, Tên, Giá, Mô tả, URL Ảnh và Số lượng tồn kho.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (shortDescription.length() > 512) {
            Toast.makeText(this, "Mô tả ngắn không được vượt quá 512 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            BigDecimal salePrice = salePriceStr.isEmpty() ? null : new BigDecimal(salePriceStr);

            Integer quantity = Integer.parseInt(quantityStr);

            if (quantity < 0) {
                Toast.makeText(this, "Số lượng tồn kho không hợp lệ (Không thể âm).", Toast.LENGTH_SHORT).show();
                return;
            }

            newQuantityForStock = quantity;
            savedProductId = productId;

            AdminProduct productToSave = new AdminProduct();
            if (productId != -1L) {
                productToSave.setId(productId);
            }

            productToSave.setSku(sku);
            productToSave.setName(name);
            productToSave.setDescription(description);
            productToSave.setShortDescription(shortDescription);

            AdminProduct.ProductImage newImage = new AdminProduct.ProductImage(imageUrl, imageAltText);
            productToSave.setImages(Collections.singletonList(newImage));

            productToSave.setPrice(price);
            productToSave.setSalePrice(salePrice);
            productToSave.setActive(isActive);

            performSaveApiCall(productToSave);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá tiền hoặc Số lượng tồn kho không hợp lệ.", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSaveApiCall(AdminProduct productToSave) {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        String token = "Bearer " + sessionManager.getToken();

        Call<AdminProduct> call;

        if (productId != -1L) {
            // BƯỚC 1A: UPDATE (PUT) thông tin sản phẩm
            call = adminApiService.updateProduct(token, productId, productToSave);
        } else {
            // BƯỚC 1B: CREATE (POST) sản phẩm mới
            call = adminApiService.createProduct(token, productToSave);
        }

        call.enqueue(new Callback<AdminProduct>() {
            @Override
            public void onResponse(Call<AdminProduct> call, Response<AdminProduct> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String action = (productId != -1L) ? "cập nhật" : "tạo mới";
                    AdminProduct savedProduct = response.body();

                    // Lấy ID sản phẩm đã lưu (cần thiết cho thao tác CREATE)
                    if (productId == -1L && savedProduct.getId() != null) {
                        savedProductId = savedProduct.getId();
                    }

                    // --- BƯỚC 2: GỌI API CẬP NHẬT TỒN KHO ---
                    if (savedProductId != null && newQuantityForStock != null) {
                        performStockUpdate(savedProductId, newQuantityForStock, action);
                    } else {
                        // Nếu không tìm thấy ID hoặc Quantity bị null (lỗi logic)
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(AdminProductActivity.this, "Đã " + action + " sản phẩm thành công (Lỗi cập nhật Tồn kho do thiếu ID).", Toast.LENGTH_LONG).show();
                        navigateToProductList();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("API_SAVE", "Error parsing error body: " + e.getMessage());
                    }
                    Log.e("API_SAVE", "Response Code: " + response.code() + ", Body: " + errorBody);
                    Toast.makeText(AdminProductActivity.this, "Lỗi lưu Sản phẩm: " + response.code() + (errorBody.isEmpty() ? "" : " (" + errorBody + ")"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AdminProduct> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                String errorMessage = "Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Server không phản hồi.");
                Log.e("API_FAILURE", "Save Failed: " + t.getMessage());
                Toast.makeText(AdminProductActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Thêm phương thức mới để gọi API cập nhật Stock ---
    private void performStockUpdate(Long id, Integer quantity, String productAction) {
        String token = "Bearer " + sessionManager.getToken();
        StockUpdateRequest request = new StockUpdateRequest();
        request.setProductId(id);
        request.setQuantity(quantity);

        adminApiService.updateStockQuantity(token, request).enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                String actionMessage = productAction.substring(0, 1).toUpperCase() + productAction.substring(1);

                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductActivity.this, actionMessage + " sản phẩm và Cập nhật Tồn kho thành công!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdminProductActivity.this, actionMessage + " sản phẩm thành công, nhưng LỖI CẬP NHẬT TỒN KHO. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
                navigateToProductList();
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                String actionMessage = productAction.substring(0, 1).toUpperCase() + productAction.substring(1);

                Toast.makeText(AdminProductActivity.this, actionMessage + " sản phẩm thành công, nhưng LỖI KẾT NỐI KHI CẬP NHẬT TỒN KHO: " + t.getMessage(), Toast.LENGTH_LONG).show();
                navigateToProductList();
            }
        });
    }

    private void navigateToProductList() {
        Intent intent = new Intent(AdminProductActivity.this, AdminProductListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}