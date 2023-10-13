package com.shahab12344.loader_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DriverInfoAdapter extends RecyclerView.Adapter<DriverInfoAdapter.ViewHolder> {
    private Context context;
    private List<DriverRequstModel> driverList;

    private FragmentManager fragmentManager;

    public DriverInfoAdapter(Context context, List<DriverRequstModel> driverList, FragmentManager fragmentManager) {
        this.context = context;
        this.driverList = driverList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drive_request_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DriverRequstModel driverInfo = driverList.get(position);
        holder.driverPictureImageView.setImageResource(driverInfo.getDriverPictureResource());
        holder.driverNameTextView.setText(driverInfo.getDriverName());
        holder.carModelTextView.setText(driverInfo.getCarModel());
        holder.carNumberTextView.setText(driverInfo.getCarNumber());
        holder.ridePriceTextView.setText(driverInfo.getRidePrice());


        Button acceptButton = holder.itemView.findViewById(R.id.acceptButton);
        Button rejectButton = holder.itemView.findViewById(R.id.rejectButton);

        // Set click listeners for the buttons
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Booking_detail_Fragment newFragment = new Booking_detail_Fragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.bookingfragment, newFragment); // Replace R.id.fragment_container with your actual container ID
                transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
                transaction.commit();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle reject button click
                Toast.makeText(view.getContext(), "Reject button clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView driverPictureImageView;
        TextView driverNameTextView;
        TextView carModelTextView;
        TextView carNumberTextView;
        TextView ridePriceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            driverPictureImageView = itemView.findViewById(R.id.driverPictureImageView);
            driverNameTextView = itemView.findViewById(R.id.driverNameTextView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            carNumberTextView = itemView.findViewById(R.id.carNumberTextView);
            ridePriceTextView = itemView.findViewById(R.id.ridePriceTextView);
        }
    }
}

