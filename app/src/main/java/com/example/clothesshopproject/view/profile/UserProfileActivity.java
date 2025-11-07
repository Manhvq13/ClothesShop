package com.example.clothesshopproject.view.profile;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.ApiService;
import com.example.clothesshopproject.view.auth.ChangePasswordActivity;
import com.example.clothesshopproject.view.auth.LoginActivity;
import com.example.clothesshopproject.model.User;
import com.example.clothesshopproject.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UserProfileActivity extends AppCompatActivity {
    private ApiService apiService;
    private SessionManager sessionManager;
    private TextView tvName, tvEmail, tvPhone, tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        if (sessionManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = ApiClient.getClient(this).create(ApiService.class);

        tvName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnChangePass = findViewById(R.id.btnToChangePassword);

        btnLogout.setOnClickListener(v -> {
            sessionManager.clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnChangePass.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));

        loadProfile();
    }

    private void loadProfile() {
        apiService.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvName.setText(user.getFullName());
                    tvEmail.setText(user.getEmail());
                    tvPhone.setText(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật");
                    tvAddress.setText(user.getAddress() != null ? user.getAddress() : "Chưa cập nhật");
                } else {
                    Toast.makeText(UserProfileActivity.this,
                            "Không tải được thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
