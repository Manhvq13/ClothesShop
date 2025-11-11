package com.example.clothesshopproject.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clothesshopproject.R;
import com.example.clothesshopproject.model.admin.UserResponse;
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.view.admin.AdminEditUserActivity;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private final Context context;
    private final List<UserResponse> userList;
    private final SessionManager sessionManager;
    private final UserActionListener listener;

    public interface UserActionListener {
        void onStatusUpdate(UserResponse user, boolean newStatus);
    }

    public AdminUserAdapter(Context context, List<UserResponse> userList, UserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.sessionManager = new SessionManager(context);
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
        UserResponse user = userList.get(position);

        String avatarUrl = user.getAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.ivAvatar);
        } else {

            holder.ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);
        }
        // ----------------------------------------

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

        boolean isCurrentUser = sessionManager.getEmail() != null && sessionManager.getEmail().equals(user.getEmail());

        if (isCurrentUser) {

            holder.btnToggleStatus.setEnabled(false);
            holder.btnToggleStatus.setAlpha(0.5f);
            holder.btnToggleStatus.setText("Current User");

        } else {
            // Logic cho các user khác
            holder.btnToggleStatus.setEnabled(true);
            holder.btnToggleStatus.setAlpha(1.0f);
            holder.btnToggleStatus.setText(isActive ? "Deactivate" : "Activate");

            // GỌI LISTENER (Status)
            holder.btnToggleStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStatusUpdate(user, !isActive);
                }
            });

        }

        // --- LOGIC NÚT EDIT DETAILS ---
        holder.btnEditDetails.setOnClickListener(v -> {

            if (user.getId() == null) {
                Toast.makeText(context, "Error: User ID is missing for this record.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, AdminEditUserActivity.class);

            intent.putExtra("USER_ID", user.getId());
            intent.putExtra("EMAIL", user.getEmail());
            intent.putExtra("FULL_NAME", user.getFullName());
            intent.putExtra("PHONE", user.getPhone());
            intent.putExtra("AVATAR", user.getAvatar());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }



    public static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFullName;
        final TextView tvEmail;
        final TextView tvPhone;
        final TextView tvRole;
        final TextView tvStatus;
        final Button btnToggleStatus;

        final Button btnEditDetails;
        final ImageView ivAvatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tv_user_full_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvPhone = itemView.findViewById(R.id.tv_user_phone);
            tvRole = itemView.findViewById(R.id.tv_user_role);
            tvStatus = itemView.findViewById(R.id.tv_user_status);
            btnToggleStatus = itemView.findViewById(R.id.btn_toggle_status);

            btnEditDetails = itemView.findViewById(R.id.btn_edit_details);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
        }
    }

    public void updateList(List<UserResponse> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }
}