package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_driver_request extends Fragment {
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    private DriverInfoAdapter adapter;
    private ProgressDialog progressDialog;
    Bundle args;
    private String customerId; // Assuming you have access to customer ID

    public fragment_driver_request() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_request, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.driverRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        sessionManager = new SessionManager(getContext());

        //bundle
        args = getArguments();
        if (args != null) {
            String pickup = args.getString("pickup");
            String destination = args.getString("destination");
            String helpers = args.getString("helpers");
            String cost = args.getString("cost");

            String vehicleType = args.getString("vehicleType");


            // Now you have the data, you can use it to fetch the data from the server
            fetchDataFromServer(vehicleType); // Pass the vehicle type here
        } else {
            Toast.makeText(getActivity(),"Something went wrong", Toast.LENGTH_LONG).show();
        }

        // Fetch data from server
//        fetchDataFromServer("Medium"); // Pass the vehicle type here

        return view;
    }

    private void fetchDataFromServer(String vehicleType) {
        fetchWishlistDataFromServer(sessionManager.getUserId()); // Corrected parameter name

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_available_Drivers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            // Check if the response is empty or null
                            if (response == null || response.isEmpty()) {
                                Toast.makeText(getActivity(), "Empty response received", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Parse the response as JSON
                            JSONObject jsonObject = new JSONObject(response);

                            // Check if the response contains the "error" key
                            if (!jsonObject.getBoolean("error")) {
                                // Retrieve the available drivers array from the JSON object
                                JSONArray driversArray = jsonObject.getJSONArray("available_drivers");

                                // Process the driver data
                                List<DriverRequstModel> driverList = new ArrayList<>();
                                for (int i = 0; i < driversArray.length(); i++) {
                                    JSONObject driverObject = driversArray.getJSONObject(i);
                                    String driverId = driverObject.getString("Driver_ID");
                                    String name = driverObject.getString("Driver_First_Name") + " " + driverObject.getString("Driver_Last_Name");
                                    String vehicle = driverObject.getString("Vehicle_type");
                                    String plateNumber = driverObject.getString("Vehicle_number");
                                    String cost = driverObject.getString("Vehicle_Model");
                                    String imageURL = driverObject.getString("Driver_Profile_Image");

                                    driverList.add(new DriverRequstModel(driverId, imageURL, name, vehicle, plateNumber, cost));
                                }

                                // Create and set the adapter
                                adapter = new DriverInfoAdapter(getContext(), driverList, getFragmentManager(), args);
                                recyclerView.setAdapter(adapter);
                            } else {
                                // If the "error" key is present, display the error message
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // If an exception occurs while parsing JSON, log the error and display a toast
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // Handle error cases
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Add parameters to the request
                params.put("vehicle_Type", vehicleType); // Replace "" with the actual Vehicle Type
                // Add other parameters as needed
                return params;
            }
        };

        // Add the request to the request queue
        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void fetchWishlistDataFromServer(String customerId) {
        StringRequest wishlistRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_fetch_wishlist,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                JSONArray wishlistArray = jsonObject.getJSONArray("available_data");
                                for (int i = 0; i < wishlistArray.length(); i++) {
                                    JSONObject wishlistObject = wishlistArray.getJSONObject(i);
                                    String driverId = wishlistObject.getString("Driver_ID");
                                    markDriverAsWishlist(driverId);
                                }
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged(); // Notify adapter after marking drivers as wishlisted
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
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Customer_ID", customerId); // Corrected parameter name
                return params;
            }
        };

        RequestHandler.getInstance(getActivity()).addToRequestQueue(wishlistRequest);
    }

    private void markDriverAsWishlist(String driverId) {
        if (adapter != null) {
            List<DriverRequstModel> driverList = adapter.getDriverList();
            for (DriverRequstModel driver : driverList) {
                if (driver.getDriverId().equals(driverId)) {
                    driver.setInWishlist(true);
                    break;
                }
            }
        }
    }

}