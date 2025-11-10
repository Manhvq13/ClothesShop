package com.example.clothesshopproject.view.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent; // Thêm import này nếu bạn muốn chuyển hướng người dùng khi không có token

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.admin.AdminApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.UserResponse;
import com.example.clothesshopproject.model.admin.UserUpdateRequest;
import com.example.clothesshopproject.utils.SessionManager; // Import SessionManager

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditUserActivity extends AppCompatActivity {

    private static final String TAG = "AdminEditUserActivity";
    private AdminApiService adminApiService;
    private SessionManager sessionManager; // Khai báo SessionManager
    private Long userId;
    private String userEmail;
    private TextView tvEmail;
    private EditText etFullName;
    private EditText etPhoneNumber;
    private Button btnSaveDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_user);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);
        String authToken = sessionManager.getToken();

        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Authentication required. Please log in again.", Toast.LENGTH_LONG).show();

            finish();
            return;
        }

        adminApiService = AdminApiClient.getClient(authToken).create(AdminApiService.class);

        tvEmail = findViewById(R.id.tv_edit_user_email);
        etFullName = findViewById(R.id.et_full_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnSaveDetails = findViewById(R.id.btn_save_details);

        loadUserData();

        btnSaveDetails.setOnClickListener(v -> saveUserDetails());
    }

    private void loadUserData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("USER_ID");
            userEmail = extras.getString("EMAIL", "Email N/A");
            String fullName = extras.getString("FULL_NAME");
            String phone = extras.getString("PHONE");

            tvEmail.setText("Email: " + userEmail);
            etFullName.setText(fullName != null ? fullName : "");
            etPhoneNumber.setText(phone != null ? phone : "");

            Log.d(TAG, "Loaded User ID: " + userId + ", Email: " + userEmail);
        } else {
            Toast.makeText(this, "Error: User data missing.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có dữ liệu cần thiết
        }
    }

    private void saveUserDetails() {
        if (userId == null) {
            Toast.makeText(this, "Error: User ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newFullName = etFullName.getText().toString().trim();
        String newPhone = etPhoneNumber.getText().toString().trim();

        if (newFullName.isEmpty()) {
            Toast.makeText(this, "Full Name is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng UserUpdateRequest với dữ liệu mới
        UserUpdateRequest request = new UserUpdateRequest(newFullName, newPhone);

        // Gọi API PUT /api/v1/admin/users/{userId} để cập nhật thông tin người dùng
        adminApiService.updateUserDetails(userId, request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminEditUserActivity.this, "Details updated successfully!", Toast.LENGTH_SHORT).show();
                    // Đánh dấu kết quả thành công để AdminUserListActivity có thể refresh dữ liệu
                    setResult(RESULT_OK);
                    finish(); // Đóng Activity sau khi cập nhật thành công
                } else if (response.code() == 404) {
                    Toast.makeText(AdminEditUserActivity.this, "Update failed: User not found.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "User not found for update, ID: " + userId);
                } else {
                    Toast.makeText(AdminEditUserActivity.this, "Update failed: Server responded with " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Update failed, Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(AdminEditUserActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "API Error during update", t);
            }
        });
    }
}