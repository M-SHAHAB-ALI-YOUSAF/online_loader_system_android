package com.shahab12344.loader_system;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {

    private List<CustomListItem> itemList;
    private Context context;
    private FragmentManager fragmentManager;

    public CustomListAdapter(Context context, List<CustomListItem> itemList, FragmentManager fragmentManager) {
        this.context = context;
        this.itemList = itemList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item_show_request_to_driver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //+++++++++++++++++++++++++++++++++++++++++++setting values+++++++++++++++++++++++++++++++++
        CustomListItem item = itemList.get(position);
        holder.customerNameTextView.setText("Customer Name: " + item.getCustomerName());
        holder.pickupLocationTextView.setText("Pick Up: " + item.getPickupLocation());
        holder.dropoffLocationTextView.setText("DropOff: " + item.getDropoffLocation());
        holder.bookingCostTextView.setText("Cost: " + item.getBookingCost());
        holder.bookinghelpers.setText("Helpers: " + item.getBookingHelpers());

        String bookindid = item.getBookingid();
        String Phoneno = item.getCustomerName();

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookingStatus(bookindid, "Accepted", item);
            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookingStatus(bookindid, "Rejected", item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView customerNameTextView, pickupLocationTextView, dropoffLocationTextView, bookingCostTextView, bookinghelpers;
        Button acceptButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.text_view_customer_name);
            pickupLocationTextView = itemView.findViewById(R.id.text_view_pickup_location);
            dropoffLocationTextView = itemView.findViewById(R.id.text_view_dropoff_location);
            bookingCostTextView = itemView.findViewById(R.id.text_view_booking_cost);
            bookinghelpers = itemView.findViewById(R.id.text_view_booking_helpers);
            acceptButton = itemView.findViewById(R.id.button_accept);
            rejectButton = itemView.findViewById(R.id.button_reject);
        }
    }


//    +++++++++++++++++++++++++++++++++++++++update booking status++++++++++++++++++++++++++++++++++
    private void updateBookingStatus(String bookingId, String newStatus, CustomListItem item) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_UPDATE_BOOKING_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                String message = jsonObject.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                if (newStatus.equals("Accepted")) {
                                    navigateToNextFragment(item);
                                } else if (newStatus.equals("Rejected")) {
                                    removeItem(item);
                                }
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Booking_ID", bookingId);
                params.put("Booking_Status", newStatus);
                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void navigateToNextFragment(CustomListItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("bookingid",item.getBookingid());
        bundle.putString("customerphoneno",item.getCustomerPhoneNo());
        bundle.putString("customerName", item.getCustomerName());
        bundle.putString("pickupLocation", item.getPickupLocation());
        bundle.putString("dropoffLocation", item.getDropoffLocation());
        bundle.putString("bookingCost", item.getBookingCost());
        bundle.putString("bookingHelpers", item.getBookingHelpers());
        bundle.putString("CustomerID", item.getCustomerID());

        Customer_Detail_to_driver_after_Booking fragment = new Customer_Detail_to_driver_after_Booking();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.driver_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void removeItem(CustomListItem item) {
        itemList.remove(item);
        notifyDataSetChanged();
    }
}
