package com.shahab12344.loader_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private Context context;
    private List<WishlistItem> wishlistItems;

    public WishlistAdapter(Context context, List<WishlistItem> wishlistItems) {
        this.context = context;
        this.wishlistItems = wishlistItems;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_item, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistItem item = wishlistItems.get(position);

        holder.driverNameTextView.setText("Name: " +item.getDriverName());
        holder.driverContactTextView.setText("Contact: " +item.getDriverContact());
        holder.dateTextView.setText("Date: " +item.getDate());

        // Load image using Glide library
        Glide.with(context)
                .load("http://10.0.2.2/Cargo_Go/v1/" +item.getImageUrl())
                .into(holder.driverPictureImageView);
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView driverPictureImageView;
        TextView driverNameTextView, driverContactTextView, dateTextView;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            driverPictureImageView = itemView.findViewById(R.id.driverPictureImageView);
            driverNameTextView = itemView.findViewById(R.id.DriverNameTextView);
            driverContactTextView = itemView.findViewById(R.id.DriverContactTextView);
            dateTextView = itemView.findViewById(R.id.DateTextView);
        }
    }
}
