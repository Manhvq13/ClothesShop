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
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
import com.bumptech.glide.Glide;
import com.example.clothesshopproject.api.ApiClient;
import com.example.clothesshopproject.api.admin.AdminApiService;
import com.example.clothesshopproject.model.admin.AdminProduct;
import com.example.clothesshopproject.utils.SessionManager;
import com.example.clothesshopproject.view.admin.AdminProductActivity;
import com.example.clothesshopproject.view.admin.AdminProductListActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<AdminProduct> productList;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // Khai báo các dịch vụ API và Session cần thiết
    private final AdminApiService adminApiService;
    private final SessionManager sessionManager;

    public AdminProductAdapter(Context context, List<AdminProduct> productList) {
        this.context = context;
        this.productList = productList;
        // Khởi tạo các dịch vụ
        this.adminApiService = ApiClient.getClient(context).create(AdminApiService.class);
        this.sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        AdminProduct product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvSKU.setText("SKU: " + product.getSku());

        // Xử lý giá và giá khuyến mãi
        if (product.getSalePrice() != null && product.getSalePrice().compareTo(product.getPrice()) < 0) {
            holder.tvPrice.setText("Giá KM: " + currencyFormat.format(product.getSalePrice()));
            holder.tvPrice.setTextColor(Color.RED);
            // Thêm giá gốc có gạch ngang nếu có TextView tương ứng
        } else {
            holder.tvPrice.setText("Giá: " + currencyFormat.format(product.getPrice()));
            holder.tvPrice.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        // Xử lý trạng thái hoạt động
        if (product.isActive() != null && product.isActive()) {
            holder.tvStatus.setText("Trạng thái: Hoạt động");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh
        } else {
            holder.tvStatus.setText("Trạng thái: Tạm dừng");
            holder.tvStatus.setTextColor(Color.GRAY);
        }

        // Tải ảnh bằng Glide (Giả định bạn đã thêm thư viện Glide)
        String imageUrl = product.getImageUrl();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.imgThumbnail);
        } else {
            holder.imgThumbnail.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Xử lý sự kiện nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            // Đã sửa: Chuyển sang màn hình chỉnh sửa sản phẩm và truyền ID
            Intent intent = new Intent(context, AdminProductActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });

        // Xử lý sự kiện nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            // Đã sửa: Gọi API xóa và xác nhận
            showDeleteConfirmationDialog(product, position);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Phương thức bổ sung để cập nhật danh sách từ Activity
    public void updateList(List<AdminProduct> newList) {
        this.productList.clear();
        this.productList.addAll(newList);
        notifyDataSetChanged();
    }

    // Phương thức xử lý hiển thị dialog xác nhận xóa
    private void showDeleteConfirmationDialog(AdminProduct product, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm: " + product.getName() + "? Thao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    performDelete(product.getId(), position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Phương thức gọi API xóa sản phẩm
    private void performDelete(Long productId, int position) {
        String token = "Bearer " + sessionManager.getToken();
        adminApiService.deleteProduct(token, productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Xóa sản phẩm thành công.", Toast.LENGTH_SHORT).show();

                    // Cập nhật danh sách cục bộ
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());

                    // Gọi refresh lại danh sách từ Activity để đảm bảo đồng bộ với server
                    if (context instanceof AdminProductListActivity) {
                        ((AdminProductListActivity) context).fetchAdminProducts();
                    }

                } else {
                    Toast.makeText(context, "Lỗi khi xóa sản phẩm. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối mạng khi xóa.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvName, tvSKU, tvPrice, tvStatus;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_product_thumbnail);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvSKU = itemView.findViewById(R.id.tv_product_sku);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvStatus = itemView.findViewById(R.id.tv_product_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}