package com.shahab12344.loader_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {
    private List<Booking> bookingList;

    public BookingHistoryAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.textViewDate.setText("Date: "+booking.getDate());
        holder.textViewPickUp.setText(booking.getPickUp());
        holder.textViewDropOff.setText(booking.getDropOff());
        holder.textViewPrice.setText("Cost: "+booking.getPrice());
        holder.textViewStatus.setText(booking.getStatus());

        if (booking.getStatus().equalsIgnoreCase("Cancel")) {
            holder.textViewStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewPickUp, textViewDropOff, textViewPrice, textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewPickUp = itemView.findViewById(R.id.textViewPickUp);
            textViewDropOff = itemView.findViewById(R.id.textViewDropOff);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
