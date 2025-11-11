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
import com.example.clothesshopproject.view.admin.AdminStockActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    // --- CHỨC NĂNG CHÍNH: KHỞI TẠO VÀ CẤU HÌNH ---
    private final Context context;
    private final List<AdminProduct> productList;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private final AdminApiService adminApiService;
    private final SessionManager sessionManager;

    public AdminProductAdapter(Context context, List<AdminProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.adminApiService = ApiClient.getClient(context).create(AdminApiService.class);
        this.sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    // --- CHỨC NĂNG CHÍNH: BIND DỮ LIỆU ---
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        AdminProduct product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvSKU.setText("SKU: " + product.getSku());

        // HIỂN THỊ DANH MỤC (BỔ SUNG tính năng mới)
        String categoryName = product.getPrimaryCategoryName();
        holder.tvCategory.setText("Danh mục: " + categoryName);

        // Xử lý giá
        if (product.getSalePrice() != null && product.getSalePrice().compareTo(product.getPrice()) < 0) {
            holder.tvPrice.setText("Giá KM: " + currencyFormat.format(product.getSalePrice()));
            holder.tvPrice.setTextColor(Color.RED);
        } else {
            holder.tvPrice.setText("Giá: " + currencyFormat.format(product.getPrice()));
            holder.tvPrice.setTextColor(Color.BLACK);
        }

        // Xử lý trạng thái hoạt động
        if (product.getIsActive() != null && product.getIsActive()) {
            holder.tvStatus.setText("Trạng thái: Hoạt động");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh
        } else {
            holder.tvStatus.setText("Trạng thái: Tạm dừng");
            holder.tvStatus.setTextColor(Color.GRAY);
        }

        // Tải ảnh bằng Glide
        String imageUrl = product.getImageUrl();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.imgThumbnail);
        } else {
            holder.imgThumbnail.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Hiển thị tồn kho
        Integer quantity = product.getQuantityInStock() != null ? product.getQuantityInStock() : 0;
        Integer reserved = product.getReservedStock() != null ? product.getReservedStock() : 0;
        Integer available = product.getAvailableStock() != null ? product.getAvailableStock() : 0;

        holder.tvQuantity.setText("Tổng: " + quantity);
        holder.tvReserved.setText("Dự trữ: " + reserved);
        holder.tvAvailable.setText("Có sẵn: " + available);

        // Cảnh báo tồn kho thấp
        if (available <= 5) {
            holder.tvAvailable.setTextColor(Color.RED);
        } else {
            holder.tvAvailable.setTextColor(Color.parseColor("#007BFF"));
        }

        // --- CHỨC NĂNG CHÍNH: XỬ LÝ SỰ KIỆN (ACTIONS) ---

        // Nút Sửa (Edit): Mở màn hình AdminProductActivity
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminProductActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });

        // Nút Quản lý Tồn kho (Manage Stock): Mở AdminStockActivity
        holder.btnManageStock.setOnClickListener(v -> {
            if (product.getId() == null) {
                Toast.makeText(context, "Lỗi: Không tìm thấy ID sản phẩm để quản lý tồn kho.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, AdminStockActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });

        // Nút Xóa (Delete): Hiển thị xác nhận xóa
        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(product, position);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Cập nhật danh sách và làm mới RecyclerView
    public void updateList(List<AdminProduct> newList) {
        this.productList.clear();
        this.productList.addAll(newList);
        notifyDataSetChanged();
    }

    // Hiển thị hộp thoại xác nhận xóa
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

    // Gọi API xóa sản phẩm (Delete API Call)
    private void performDelete(Long productId, int position) {
        String token = "Bearer " + sessionManager.getToken();
        adminApiService.deleteProduct(token, productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Xóa sản phẩm thành công.", Toast.LENGTH_SHORT).show();

                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());

                    if (context instanceof AdminProductListActivity) {
                        // Gọi loadProducts để cập nhật phân trang
                        ((AdminProductListActivity) context).loadProducts(0);
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

    // --- ViewHolder: Ánh xạ View ---
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvName, tvSKU, tvPrice, tvStatus;
        TextView tvCategory; // THÊM: TextView cho danh mục

        TextView tvQuantity, tvReserved, tvAvailable;

        Button btnEdit, btnDelete, btnManageStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_product_thumbnail);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvSKU = itemView.findViewById(R.id.tv_product_sku);

            // Ánh xạ TextView Danh mục
            tvCategory = itemView.findViewById(R.id.tv_product_category);

            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvStatus = itemView.findViewById(R.id.tv_product_status);

            // Ánh xạ các trường tồn kho
            tvQuantity = itemView.findViewById(R.id.tv_stock_quantity);
            tvReserved = itemView.findViewById(R.id.tv_stock_reserved);
            tvAvailable = itemView.findViewById(R.id.tv_stock_available);

            // Ánh xạ các nút hành động
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnManageStock = itemView.findViewById(R.id.btn_manage_stock);
        }
    }
}