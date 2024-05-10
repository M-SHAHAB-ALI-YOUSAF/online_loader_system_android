package com.shahab12344.loader_system;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Customer_Detail_to_driver_after_Booking extends Fragment {
    String bookingid, phoneNumber, customerid, pickupLocation, dropoffLocation, cost;
    ImageView callCustomerImageView, messageCustomerImageView;
    private static final long CHECK_INTERVAL = 30 * 1000;
    private Handler handler;
    private SessionManager sessionManager;
    public Customer_Detail_to_driver_after_Booking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer__detail_to_driver_after__booking, container, false);

        TextView customerNameTextView = view.findViewById(R.id.text_view_customer_name);
        TextView pickupLocationTextView = view.findViewById(R.id.text_view_pickup_location);
        TextView dropoffLocationTextView = view.findViewById(R.id.text_view_dropoff_location);
        TextView helpersTextView = view.findViewById(R.id.text_view_helpers);
        TextView costTextView = view.findViewById(R.id.text_view_cost);
        sessionManager = new SessionManager(getContext());

        Bundle args = getArguments();
        if (args != null) {
            //++++++++++++++++++++++++++++++++++++++++++++++++++ Retrieve the values from the arguments
            String customerName = args.getString("customerName");
            pickupLocation = args.getString("pickupLocation");
            dropoffLocation = args.getString("dropoffLocation");
            String helpers = args.getString("bookingHelpers");
            cost = args.getString("bookingCost");
            bookingid = args.getString("bookingid");
            phoneNumber = args.getString("customerphoneno");
            customerid = args.getString("CustomerID");


            //+++++++ Update TextView elements with the retrieved values+++++++++++++++++++++++++
            customerNameTextView.setText("Customer Name: " + customerName);
            pickupLocationTextView.setText("Pickup: " + pickupLocation);
            dropoffLocationTextView.setText("Dropoff: " + dropoffLocation);
            helpersTextView.setText("Helpers: " + helpers);
            costTextView.setText("Cost: " + cost);
        }

        Button cancelbooking = view.findViewById(R.id.button_cancel_booking);
        cancelbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBookingStatus(bookingid, "Cancel");
            }
        });

        callCustomerImageView = view.findViewById(R.id.callcustomer);
        messageCustomerImageView = view.findViewById(R.id.cudtomer_message);

        // +++++++++++++++++Set click listeners for call and message icons++++++++++++++++++++++++++++
        callCustomerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                getActivity().startActivity(callIntent);
            }
        });

        messageCustomerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                getActivity().startActivity(messageIntent);
            }
        });


        handler = new Handler(Looper.getMainLooper());

        //+++++++++++++++++++++++++++++++++++ Start periodic check+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        startPeriodicCheck();

        return view;
    }

    private void  updateBookingStatus(String bookingid, String newStatus ){
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
                                if(newStatus.equals("Cancel")){
                                    sendtoDbRideHistory("Cancel");
                                }
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Booking_ID", bookingid);
                params.put("Booking_Status", newStatus);
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    //+++++++++++++++++++++++++++++++++++Send to sb ride history++++++++++++++++++++++++++
    private void sendtoDbRideHistory(String newStatus){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ride_history,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), "Booking "+newStatus+" successfully", Toast.LENGTH_SHORT).show();
                                Driver_Homepage_Fragment fragment = new Driver_Homepage_Fragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.driver_fragment, fragment);
                                fragmentTransaction.addToBackStack(null); // Replace with the container ID
                                fragmentTransaction.commit();

                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Customer_ID", customerid);
                params.put("Driver_ID", sessionManager.getUserId());
                params.put("Booking_ID", bookingid);
                params.put("Fare_Amount", cost);
                params.put("Pick_up", pickupLocation);
                params.put("Drop_off", dropoffLocation);
                params.put("Ride_Status", newStatus);
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    //+++++++++++++++++++++++++++++++++periodic change++++++++++++++++++++++++++++++++
    private void startPeriodicCheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkBookingStatus();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        }, CHECK_INTERVAL);
    }

    //++++++++++++++++++++++++++checkstatus+++++++++++++++++++++++++++++++++++++++++++
    private void checkBookingStatus(){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CHECKING_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                String bookingStatus = jsonObject.getString("status");
                                if (bookingStatus.equals("Cancel") || bookingStatus.equals("Complete")) {
                                    sendtoDbRideHistory(bookingStatus);
                                    Toast.makeText(getActivity(), "Booking is " + bookingStatus, Toast.LENGTH_SHORT).show();
                                    handler.removeCallbacksAndMessages(null);
                                    Driver_Homepage_Fragment fragment = new Driver_Homepage_Fragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.driver_fragment, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    return;
                                }
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Booking_ID", bookingid);
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}