package com.example.clothesshopproject.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
import com.example.clothesshopproject.model.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final UserActionListener listener;

    public interface UserActionListener {
        void onRoleUpdate(User user, String newRoleName);
        void onStatusUpdate(User user, boolean newStatus);
    }

    public AdminUserAdapter(Context context, List<User> userList, UserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserId.setText("ID: " + user.getId());
        holder.tvUserEmail.setText("Email: " + user.getEmail());
        holder.tvUserRole.setText("Role: " + (user.getRole() != null ? user.getRole().getName() : "N/A"));
        holder.tvUserStatus.setText("Active: " + (user.getStatus() ? "Yes" : "No"));

        // Nút cập nhật Role
        holder.btnUpdateRole.setOnClickListener(v -> {
            // Logic chuyển đổi Role: Nếu đang là USER thì chuyển thành ADMIN và ngược lại
            String currentRole = user.getRole() != null ? user.getRole().getName() : "USER";
            String newRole = currentRole.equalsIgnoreCase("USER") ? "ADMIN" : "USER";
            listener.onRoleUpdate(user, newRole);
        });

        // Nút cập nhật Status (Active/Inactive)
        holder.btnToggleStatus.setText(user.getStatus() ? "DEACTIVATE" : "ACTIVATE");
        holder.btnToggleStatus.setOnClickListener(v -> {
            boolean newStatus = !user.getStatus();
            listener.onStatusUpdate(user, newStatus);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvUserEmail, tvUserRole, tvUserStatus;
        Button btnUpdateRole, btnToggleStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserRole = itemView.findViewById(R.id.tv_user_role);
            tvUserStatus = itemView.findViewById(R.id.tv_user_status);
            btnUpdateRole = itemView.findViewById(R.id.btn_update_role);
            btnToggleStatus = itemView.findViewById(R.id.btn_toggle_status);
        }
    }
}