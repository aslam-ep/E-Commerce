package com.hector.e_commerce;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private Context cx;
    private List<Product> productList;

    public ProductAdapter(Context cx, List<Product> productList) {
        this.cx = cx;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cx);
        View view = inflater.inflate(R.layout.list_layout,parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);

        holder.name.setText(p.getName());
        holder.spec.setText(p.getSpec());
        holder.price.setText(p.getPrice());

//        holder.img.setImageBitmap(p.getBitmap());
        Glide.with(cx)
                .load(p.getImgURL())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                .thumbnail(Glide.with(cx).load(R.drawable.ic_baseline_image_24))
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView name,spec,price;
        ImageView img;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.productName);
            spec = itemView.findViewById(R.id.productSpec);
            price = itemView.findViewById(R.id.productPrice);

            img = itemView.findViewById(R.id.imageView);
        }
    }
}
