package com.example.clothesshopproject.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
// Đã loại bỏ import AdminApiClient, AdminApiService, UserUpdateRoleRequest, UserUpdateStatusRequest
import com.example.clothesshopproject.model.admin.UserResponse;
import com.example.clothesshopproject.utils.SessionManager;

import java.util.List;
// Đã loại bỏ các import Retrofit không cần thiết

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private final Context context;
    private final List<UserResponse> userList;
    // Đã loại bỏ: private final AdminApiService adminApiService;
    private final SessionManager sessionManager;
    private final UserActionListener listener; // Thêm trường Listener

    public interface UserActionListener {
        void onRoleUpdate(UserResponse user, String newRoleName);
        void onStatusUpdate(UserResponse user, boolean newStatus);
    }

    // 2. CẬP NHẬT CONSTRUCTOR để nhận Listener
    public AdminUserAdapter(Context context, List<UserResponse> userList, UserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.sessionManager = new SessionManager(context);
        this.listener = listener; // Gán Listener
        // Đã loại bỏ logic khởi tạo AdminApiService
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserResponse user = userList.get(position);

        holder.tvFullName.setText("Name: " + (user.getFullName() != null ? user.getFullName() : "N/A"));
        holder.tvEmail.setText("Email: " + user.getEmail());
        holder.tvPhone.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));

        String roleName = user.getRole() != null ? user.getRole().getName() : "NO_ROLE";
        holder.tvRole.setText("Role: " + roleName);

        boolean isActive = user.getStatus() != null && user.getStatus();
        holder.tvStatus.setText(isActive ? "Status: Active" : "Status: Inactive");
        holder.tvStatus.setTextColor(isActive ? Color.GREEN : Color.RED);

        // Nút Cập nhật Trạng thái (Status)
        holder.btnToggleStatus.setText(isActive ? "Deactivate" : "Activate");

        // KIỂM TRA: Không cho phép tự vô hiệu hóa tài khoản đang đăng nhập
        if (sessionManager.getEmail() != null && sessionManager.getEmail().equals(user.getEmail())) {
            holder.btnToggleStatus.setEnabled(false);
            holder.btnToggleStatus.setAlpha(0.5f);
            holder.btnToggleStatus.setText(isActive ? "Current User" : "Activate (Self)");
        } else {
            holder.btnToggleStatus.setEnabled(true);
            holder.btnToggleStatus.setAlpha(1.0f);

            // 3. GỌI LISTENER (Status)
            holder.btnToggleStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStatusUpdate(user, !isActive);
                }
            });
        }

        // 3. GỌI LISTENER (Role)
        holder.btnUpdateRole.setOnClickListener(v -> showRoleUpdateDialog(user, roleName));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Đã loại bỏ các phương thức toggleUserStatus và updateUserRole (logic API)

    // Phương thức hiển thị Dialog cập nhật Role (gọi Listener)
    private void showRoleUpdateDialog(UserResponse user, String currentRole) {
        final String[] roles = {"USER", "ADMIN"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Role for " + user.getEmail());
        builder.setItems(roles, (dialog, which) -> {
            String newRoleName = roles[which];
            if (!newRoleName.equals(currentRole)) {
                // GỌI LISTENER
                if (listener != null) {
                    listener.onRoleUpdate(user, newRoleName);
                }
            } else {
                Toast.makeText(context, "Role is already " + currentRole, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFullName;
        final TextView tvEmail;
        final TextView tvPhone;
        final TextView tvRole;
        final TextView tvStatus;
        final Button btnToggleStatus;
        final Button btnUpdateRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tv_user_full_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvPhone = itemView.findViewById(R.id.tv_user_phone);
            tvRole = itemView.findViewById(R.id.tv_user_role);
            tvStatus = itemView.findViewById(R.id.tv_user_status);
            btnToggleStatus = itemView.findViewById(R.id.btn_toggle_status);
            btnUpdateRole = itemView.findViewById(R.id.btn_update_role);
        }
    }

    public void updateList(List<UserResponse> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }
}