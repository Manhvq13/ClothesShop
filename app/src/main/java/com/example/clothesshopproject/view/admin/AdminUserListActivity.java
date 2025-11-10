package com.example.clothesshopproject.view.admin;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);
        adminApiService = AdminApiClient.getClient(sessionManager.getToken()).create(AdminApiService.class);

        adapter = new AdminUserAdapter(this, new java.util.ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fetchUsers();
    }

    private void fetchUsers() {
        // Gọi API mà không cần thêm "Bearer " vì đã được thêm trong AdminApiClient Interceptor
        // Sửa lỗi: Gọi API getAllUsers() không truyền token
        adminApiService.getAllUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            // Sửa lỗi: Thay thế List<User> bằng List<UserResponse>
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ĐÃ SỬA: Cập nhật adapter đã khởi tạo
                    if (adapter == null) {
                        adapter = new AdminUserAdapter(AdminUserListActivity.this, response.body(), AdminUserListActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateList(response.body());
                    }
                } else {
                    Toast.makeText(AdminUserListActivity.this, "Không thể tải danh sách người dùng. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================================================
    //               IMPLEMENTATION OF USER ACTIONS
    // =========================================================

    @Override
    // Sửa lỗi: Thay thế User bằng UserResponse
    public void onRoleUpdate(UserResponse user, String newRoleName) {
        // Không cần thêm "Bearer " vào token vì AdminApiClient đã xử lý
        UserUpdateRoleRequest request = new UserUpdateRoleRequest(newRoleName);

        // Sửa lỗi: Thay thế User bằng UserResponse
        adminApiService.updateRole(user.getId(), request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminUserListActivity.this, "Cập nhật Role thành công!", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại list
                    fetchUsers();
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
    // Sửa lỗi: Thay thế User bằng UserResponse
    public void onStatusUpdate(UserResponse user, boolean newStatus) {
        // Không cần thêm "Bearer " vào token vì AdminApiClient đã xử lý
        UserUpdateStatusRequest request = new UserUpdateStatusRequest(newStatus);

        // Sửa lỗi: Thay thế User bằng UserResponse
        adminApiService.updateStatus(user.getId(), request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminUserListActivity.this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    fetchUsers();
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