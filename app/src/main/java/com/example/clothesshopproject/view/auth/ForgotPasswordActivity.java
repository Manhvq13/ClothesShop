package com.example.clothesshopproject.view.auth;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clothesshopproject.R;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.ApiService;
import com.example.clothesshopproject.model.ForgotPasswordRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ForgotPasswordActivity extends AppCompatActivity {
    private ApiService apiService;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        edtEmail = findViewById(R.id.edtEmailForgot);
        Button btnSubmit = findViewById(R.id.btnSubmitForgot);

        btnSubmit.setOnClickListener(v -> handleForgot());
    }

    private void handleForgot() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Nhập email đã đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.forgotPassword(new ForgotPasswordRequest(email))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Đã gửi mail đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Email không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
