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
import com.example.clothesshopproject.model.admin.Category; // THÊM: Import Category model
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.view.adapter.AdminProductAdapter;
import com.example.clothesshopproject.MainActivity;

import java.util.ArrayList;
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
    private Spinner spinnerCategoryFilter; // THÊM: Spinner cho Category
    private Button btnPrevPage;
    private Button btnNextPage;

    // API & Data
    private AdminApiService adminApiService;
    private SessionManager sessionManager;
    private AdminProductAdapter adapter;
    private final List<AdminProduct> productList = new ArrayList<>();
    private final List<Category> categoriesList = new ArrayList<>(); // THÊM: Danh sách Categories

    // --- TRẠNG THÁI HIỆN TẠI CỦA FILTER VÀ PHÂN TRANG ---
    private String currentSearchName = "";
    private Integer currentCategoryId = null; // THAY ĐỔI: ID danh mục hiện tại
    private String currentSortBy = "price,asc";
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
        spinnerCategoryFilter = findViewById(R.id.spinner_category_filter); // THÊM: Ánh xạ Category Spinner
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
        // setupCategoryFilter() được gọi sau khi fetchCategories()

        // THAY ĐỔI: Chỉ tải dữ liệu sau khi lấy danh mục
        fetchCategories();

        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProductActivity.class);
            startActivity(intent);
        });
    }

    // --- CHỨC NĂNG MỚI: Tải Categories ---
    private void fetchCategories() {
        progressBar.setVisibility(View.VISIBLE);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            loadProducts(currentPage); // Vẫn tải sản phẩm nếu lỗi token
            return;
        }

        adminApiService.getAllCategories("Bearer " + token).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // Thêm tùy chọn "Tất cả" (ID = null) vào đầu danh sách
                    categoriesList.add(new Category(null, "Tất cả danh mục"));
                    categoriesList.addAll(response.body());
                    populateCategorySpinner();
                } else {
                    Log.e("API_CATEGORY", "Lỗi tải danh mục. Code: " + response.code());
                    loadProducts(currentPage);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_CATEGORY_FAILURE", "Call Failed: " + t.getMessage());
                loadProducts(currentPage);
            }
        });
    }

    // --- CHỨC NĂNG MỚI: Populate Spinner Categories ---
    private void populateCategorySpinner() {
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryFilter.setAdapter(categoryAdapter);
        spinnerCategoryFilter.setSelection(0); // Chọn "Tất cả"

        setupCategoryFilter(); // Thiết lập Listener sau khi có dữ liệu
        loadProducts(currentPage); // Bắt đầu tải sản phẩm
    }

    // --- CHỨC NĂNG MỚI: Setup Category Filter Listener ---
    private void setupCategoryFilter() {
        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);

                // Lấy Category ID (null nếu là "Tất cả danh mục")
                currentCategoryId = selectedCategory.getId();
                currentPage = 0; // Reset trang
                loadProducts(currentPage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    // --------------------------------------------------


    private void setupSortSpinner() {
        // Tùy chọn sẽ hiển thị trên UI (lấy từ strings.xml)
        String[] sortOptionsDisplay = getResources().getStringArray(R.array.sort_price_options);

        // Tùy chọn sẽ gửi lên API (field,direction)
        String[] sortOptionsApi = {"price,asc", "price,desc"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortOptionsDisplay);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        // Đặt giá trị mặc định cho Spinner (Tăng dần)
        spinnerSort.setSelection(0);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortBy = sortOptionsApi[position]; // Lấy giá trị API (price,asc hoặc price,desc)
                currentPage = 0; // Reset trang
                loadProducts(currentPage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
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


    public void loadProducts(int page) {
        progressBar.setVisibility(View.VISIBLE);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Phiên đăng nhập hết hạn.", Toast.LENGTH_LONG).show();
            return;
        }

        String nameForApi = currentSearchName.isEmpty() ? null : currentSearchName;

        Call<PageResponse<AdminProduct>> call = adminApiService.getAdminProducts(
                "Bearer " + token,
                nameForApi,
                currentCategoryId, // SỬA: Truyền Category ID để lọc
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
        if (sessionManager.isAdmin() && categoriesList.isEmpty()) {
            // Tải danh mục nếu chưa có (khi Activity vừa tạo hoặc bị destroy)
            fetchCategories();
        } else if (sessionManager.isAdmin()) {
            // Tải sản phẩm nếu đã có danh mục (khi quay lại từ AdminProductActivity)
            loadProducts(currentPage);
        }
    }

    public void setCategoryFilter(Integer categoryId) {
        this.currentCategoryId = categoryId;
        this.currentPage = 0;
        loadProducts(currentPage);
    }
}