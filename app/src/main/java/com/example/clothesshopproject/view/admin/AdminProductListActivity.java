package com.example.clothesshopproject.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.AdminProduct;
import com.example.clothesshopproject.model.admin.PageResponse;
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.view.adapter.AdminProductAdapter;
import com.example.clothesshopproject.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductListActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchView searchView;
    private Spinner spinnerSort;
    private Button btnPrevPage;
    private Button btnNextPage;

    // API & Data
    private AdminApiService adminApiService;
    private SessionManager sessionManager;
    private AdminProductAdapter adapter;
    private final List<AdminProduct> productList = new ArrayList<>();

    // --- TRẠNG THÁI HIỆN TẠI CỦA FILTER VÀ PHÂN TRANG ---
    private String currentSearchName = "";
    private Integer currentCategoryId = null; // null: không lọc
    private String currentSortBy = "price";  // Mặc định sắp xếp theo giá
    private int currentPage = 0;
    private int totalPages = 0;
    private final int pageSize = 10;
    // ---


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Không có quyền truy cập Admin.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_admin_product_list);

        // Ánh xạ View
        recyclerView = findViewById(R.id.rv_product_list);
        progressBar = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);
        spinnerSort = findViewById(R.id.spinner_sort_by);
        btnPrevPage = findViewById(R.id.btn_prev_page);
        btnNextPage = findViewById(R.id.btn_next_page);

        adminApiService = ApiClient.getClient(this).create(AdminApiService.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter
        adapter = new AdminProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        setupSearchView();
        setupSortSpinner();
        setupPaginationControls();

        // Tải dữ liệu lần đầu
        loadProducts(currentPage);

        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProductActivity.class);
            startActivity(intent);
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchName = query.trim();
                currentPage = 0;
                loadProducts(currentPage);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !currentSearchName.isEmpty()) {
                    currentSearchName = "";
                    currentPage = 0;
                    loadProducts(currentPage);
                }
                return false;
            }
        });
    }

    private void setupSortSpinner() {
        // Mặc định sắp xếp theo giá (price)
        String[] sortOptions = {"price"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortBy = sortOptions[position];
                currentPage = 0;
                loadProducts(currentPage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupPaginationControls() {
        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 0) {
                loadProducts(currentPage - 1);
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                loadProducts(currentPage + 1);
            }
        });
    }

    /**
     * Phương thức chính để gọi API tải danh sách sản phẩm với các tham số Filter/Search/Sort/Pagination
     * @param page Số trang cần tải (bắt đầu từ 0)
     */
    public void loadProducts(int page) {
        progressBar.setVisibility(View.VISIBLE);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Phiên đăng nhập hết hạn.", Toast.LENGTH_LONG).show();
            return;
        }

        // Đảm bảo tham số 'name' là null nếu chuỗi rỗng, không phải là chuỗi rỗng
        String nameForApi = currentSearchName.isEmpty() ? null : currentSearchName;

        Call<PageResponse<AdminProduct>> call = adminApiService.getAdminProducts(
                "Bearer " + token,
                nameForApi, // String hoặc null
                currentCategoryId, // Integer hoặc null
                currentSortBy,
                page,
                pageSize
        );

        call.enqueue(new Callback<PageResponse<AdminProduct>>() {
            @Override
            public void onResponse(Call<PageResponse<AdminProduct>> call, Response<PageResponse<AdminProduct>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<AdminProduct> pageResponse = response.body();

                    currentPage = pageResponse.getNumber();
                    totalPages = pageResponse.getTotalPages();

                    adapter.updateList(pageResponse.getContent());

                    updatePaginationButtonStatus();

                } else {
                    String message = "Lỗi tải dữ liệu. Mã lỗi: " + response.code();
                    if (response.code() == 403) {
                        message = "Không có quyền truy cập Admin.";
                    }
                    Toast.makeText(AdminProductListActivity.this, message, Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Response Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PageResponse<AdminProduct>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_FAILURE", "Call Failed: " + t.getMessage());
                Toast.makeText(AdminProductListActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updatePaginationButtonStatus() {
        btnPrevPage.setEnabled(currentPage > 0);
        btnNextPage.setEnabled(currentPage < totalPages - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isAdmin()) {
            loadProducts(currentPage);
        }
    }

    public void setCategoryFilter(Integer categoryId) {
        this.currentCategoryId = categoryId;
        this.currentPage = 0;
        loadProducts(currentPage);
    }
}