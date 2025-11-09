package com.example.clothesshopproject.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshopproject.R;
import com.bumptech.glide.Glide;
import com.example.clothesshopproject.model.admin.AdminProduct;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<AdminProduct> productList;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public AdminProductAdapter(Context context, List<AdminProduct> productList) {
        this.context = context;
        this.productList = productList;
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
            // TODO: Chuyển sang màn hình chỉnh sửa sản phẩm
            Toast.makeText(context, "Sửa sản phẩm ID: " + product.getId(), Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            // TODO: Gọi API xóa và xác nhận
            Toast.makeText(context, "Xóa sản phẩm ID: " + product.getId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
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