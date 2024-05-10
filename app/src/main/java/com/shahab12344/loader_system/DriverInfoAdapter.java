package com.shahab12344.loader_system;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverInfoAdapter extends RecyclerView.Adapter<DriverInfoAdapter.ViewHolder> {
    private Context context;
    private List<DriverRequstModel> driverList;
    private SessionManager sessionManager;
    private FragmentManager fragmentManager;
    private Bundle mBundle;
    private String bookingId;

    private boolean isRequestSent = false; // Flag to track if request is sent
    private boolean isRequestAccepted = false; // Flag to track if request is accepted

    public DriverInfoAdapter(Context context, List<DriverRequstModel> driverList, FragmentManager fragmentManager, Bundle bundle) {
        this.context = context;
        this.driverList = driverList;
        this.fragmentManager = fragmentManager;
        mBundle = bundle;
        sessionManager = new SessionManager(context); // Initialize SessionManager with context
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

        // Load image using Glide
        Glide.with(context)
                .load("http://10.0.2.2/Cargo_Go/v1/" + driverInfo.getDriverPictureResource())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.driverPictureImageView);

        holder.driverNameTextView.setText(driverInfo.getDriverName());
        holder.carModelTextView.setText("Type: " + driverInfo.getCarModel());
        holder.carNumberTextView.setText("Number: " + driverInfo.getCarNumber());
        holder.ridePriceTextView.setText("Model: " + driverInfo.getRidePrice());
        String driverId = driverInfo.getDriverId(); // Get driver id for this specific item

        // Check if the driver is in the wishlist and change the background color accordingly
        holder.itemView.setSelected(driverInfo.isInWishlist());
        holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.border_with_color));

        // Set click listener for the accept button
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRequestSent) {
                    // Move the initialization of pickup, destination, helpers, cost, and vehicleType here
                    String pickup = mBundle.getString("pickup");
                    String destination = mBundle.getString("destination");
                    String helpers = mBundle.getString("helpers");
                    String cost = mBundle.getString("cost");
                    String vehicleType = mBundle.getString("vehicleType");
                    // Send booking request
                    bookingRequest(pickup, destination, helpers, cost, vehicleType, driverId); // Pass driverId to bookingRequest method

                    // Disable sending requests for 30 seconds
                    isRequestSent = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isRequestSent = false;
                            // Remove the driver from the list after 30 seconds if no response is received
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                driverList.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                // Here you can also remove the driver from the database if needed
                            }
                        }
                    }, 30000); // 30 seconds

                } else {
                    Toast.makeText(context, "Please wait for 30 seconds before sending another request.", Toast.LENGTH_SHORT).show();
                }
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
        Button acceptButton;

        public ViewHolder(View itemView) {
            super(itemView);
            driverPictureImageView = itemView.findViewById(R.id.driverPictureImageView);
            driverNameTextView = itemView.findViewById(R.id.driverNameTextView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            carNumberTextView = itemView.findViewById(R.id.carNumberTextView);
            ridePriceTextView = itemView.findViewById(R.id.ridePriceTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }

    // Method to retrieve the driver list
    public List<DriverRequstModel> getDriverList() {
        return driverList;
    }

    // Method to send booking request
    private void bookingRequest(String pickup, String destination, String helpers, String cost, String vehicleType, String driverId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_booking,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("message")) {
                                String message = jsonObject.getString("message");
                                Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject.has("booking_id")) {
                                bookingId = jsonObject.getString("booking_id");
                                checkBookingStatus(driverId, bookingId);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            Toast.makeText(context.getApplicationContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error cases
                        Toast.makeText(context.getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Customer_ID", sessionManager.getUserId()); // Replace "" with the actual Customer ID
                params.put("Driver_ID", driverId); // Replace "" with the actual Driver ID
                params.put("Pickup_Location", pickup); // Replace "" with the actual Pickup Location
                params.put("Dropoff_Location", destination); // Replace "" with the actual Drop-off Location
                params.put("Helpers", helpers); // Replace "" with the actual Helpers
                params.put("Total_Cost", cost); // Replace "" with the actual Total Cost
                params.put("vehicle_Type", vehicleType); // Replace "" with the actual Vehicle Type
                params.put("Booking_Status", "Requested"); // Replace "" with the actual Booking Status
                return params;
            }
        };

        // Add the request to the request queue
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Method to check booking status
    private void checkBookingStatus(String driverId, String bookingId) {
        // Start a background thread to check the status
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isRequestAccepted) {
                    makeStatusCheckRequest(driverId, bookingId); // Pass driverId to makeStatusCheckRequest method
                    try {
                        Thread.sleep(5000); // Check every 5 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Method to make status check request to the server
    private void makeStatusCheckRequest(String driverId, String bookingId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_check_status, // Update with your PHP endpoint for checking status
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("status") && jsonObject.has("booking_id")) {
                                String status = jsonObject.getString("status");
                                // Check the status
                                if (status.equals("Accepted")) {
                                    // Update UI or perform any action
                                    isRequestAccepted = true;
                                    Toast.makeText(context.getApplicationContext(), "Requested is accepted: ", Toast.LENGTH_SHORT).show();

                                    // Navigate to the next fragment
                                    navigateToNextFragment(bookingId);
                                } else if (status.equals("Rejected") && !isRequestSent) {
                                    // Request is rejected, show toast message only if request was not already sent
                                    isRequestSent = false; // Allow sending another request
                                    Toast.makeText(context.getApplicationContext(), "Requested is rejected. Please request another driver.", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Request is pending, do nothing
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error cases
                        Toast.makeText(context.getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Pass the customer ID and driver ID as parameters
                params.put("Customer_ID", sessionManager.getUserId());
                params.put("Driver_ID", driverId);
                params.put("Booking_ID", bookingId);
                return params;
            }
        };

        // Add the request to the request queue
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Method to navigate to the next fragment upon request acceptance
    private void navigateToNextFragment(String bookingId) {
        // Create a bundle to pass the booking ID to the next fragment
        Bundle bundle = new Bundle();
        bundle.putString("booking_id", bookingId);

        // Create an instance of the next fragment and set arguments
        Booking_detail_Fragment nextFragment = new Booking_detail_Fragment();
        nextFragment.setArguments(bundle);

        // Navigate to the next fragment using FragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.bookingfragment, nextFragment)
                .addToBackStack(null)
                .commit();
    }
}
