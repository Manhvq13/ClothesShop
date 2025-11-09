package com.example.clothesshopproject.view.admin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.User;
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

        // ĐÃ SỬA: Thay thế getRetrofitInstance() bằng getClient(this)
        adminApiService = ApiClient.getClient(this).create(AdminApiService.class);
        sessionManager = new SessionManager(this);

        fetchUsers();
    }

    private void fetchUsers() {
        // ĐÃ SỬA: Thay thế getAuthToken() bằng getToken()
        String token = "Bearer " + sessionManager.getToken();

        adminApiService.getAllUsers(token).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new AdminUserAdapter(AdminUserListActivity.this, response.body(), AdminUserListActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminUserListActivity.this, "Không thể tải danh sách người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================================================
    //               IMPLEMENTATION OF USER ACTIONS
    // =========================================================

    @Override
    public void onRoleUpdate(User user, String newRoleName) {
        // ĐÃ SỬA: Thay thế getAuthToken() bằng getToken()
        String token = "Bearer " + sessionManager.getToken();
        UserUpdateRoleRequest request = new UserUpdateRoleRequest(newRoleName);

        adminApiService.updateRole(token, user.getId(), request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminUserListActivity.this, "Cập nhật Role thành công!", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại list
                    fetchUsers();
                } else {
                    Toast.makeText(AdminUserListActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStatusUpdate(User user, boolean newStatus) {
        // ĐÃ SỬA: Thay thế getAuthToken() bằng getToken()
        String token = "Bearer " + sessionManager.getToken();
        UserUpdateStatusRequest request = new UserUpdateStatusRequest(newStatus);

        adminApiService.updateStatus(token, user.getId(), request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminUserListActivity.this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    fetchUsers();
                } else {
                    Toast.makeText(AdminUserListActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}