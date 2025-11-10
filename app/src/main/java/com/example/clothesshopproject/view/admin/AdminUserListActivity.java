package com.example.clothesshopproject.view.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.admin.AdminApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.UserResponse;
import com.example.clothesshopproject.model.admin.UserUpdateRoleRequest;
import com.example.clothesshopproject.model.admin.UserUpdateStatusRequest;
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.view.adapter.AdminUserAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserListActivity extends AppCompatActivity implements AdminUserAdapter.UserActionListener {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private AdminApiService adminApiService;
    private SessionManager sessionManager;

    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonClearSearch;

    private String currentKeyword = null;
    private String currentRoleName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        recyclerView = findViewById(R.id.recycler_view_users);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonClearSearch = findViewById(R.id.buttonClearSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);

        adminApiService = AdminApiClient.getClient(sessionManager.getToken()).create(AdminApiService.class);

        adapter = new AdminUserAdapter(this, new java.util.ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        buttonClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearch();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUsers(currentKeyword, currentRoleName);
    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();

        if (TextUtils.isEmpty(query)) {
            Toast.makeText(this, "Vui lòng nhập từ khóa (Tên hoặc Role).", Toast.LENGTH_SHORT).show();
            clearSearch();
            return;
        }

        // Cập nhật tham số tìm kiếm hiện tại
        currentKeyword = query;
        currentRoleName = query;

        fetchUsers(currentKeyword, currentRoleName);
    }

    private void clearSearch() {
        editTextSearch.setText("");
        currentKeyword = null;
        currentRoleName = null;
        fetchUsers(null, null);
    }

    private void fetchUsers(String keyword, String roleName) {
        // Gọi API với tham số keyword và roleName (nếu là null, Retrofit sẽ bỏ qua)
        adminApiService.getAllUsers(keyword, roleName).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserResponse> userList = response.body();
                    if (adapter == null) {
                        adapter = new AdminUserAdapter(AdminUserListActivity.this, userList, AdminUserListActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateList(userList);
                    }

                    String searchStatus = (keyword != null || roleName != null) ? "Tìm thấy " + userList.size() + " người dùng." : "Đã tải " + userList.size() + " người dùng.";
                    Toast.makeText(AdminUserListActivity.this, searchStatus, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminUserListActivity.this, "Không thể tải danh sách người dùng. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                Log.e("AdminUserListActivity", "Lỗi tải người dùng: " + t.getMessage());
                Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRoleUpdate(UserResponse user, String newRoleName) {
        UserUpdateRoleRequest request = new UserUpdateRoleRequest(newRoleName);

        adminApiService.updateRole(user.getId(), request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminUserListActivity.this, "Cập nhật Role thành công!", Toast.LENGTH_SHORT).show();
                    // Tải lại với các tham số tìm kiếm hiện tại
                    fetchUsers(currentKeyword, currentRoleName);
                } else {
                    Toast.makeText(AdminUserListActivity.this, "Lỗi: " + (response.errorBody() != null ? response.message() : "Unknown Error"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStatusUpdate(UserResponse user, boolean newStatus) {
        UserUpdateStatusRequest request = new UserUpdateStatusRequest(newStatus);

        adminApiService.updateStatus(user.getId(), request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminUserListActivity.this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    fetchUsers(currentKeyword, currentRoleName);
                } else {
                    Toast.makeText(AdminUserListActivity.this, "Lỗi: " + (response.errorBody() != null ? response.message() : "Unknown Error"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}