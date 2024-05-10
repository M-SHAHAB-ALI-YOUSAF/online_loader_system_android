package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class WishlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private SessionManager sessionManager;
    private WishlistAdapter adapter;
    private ProgressDialog progressDialog;

    private List<WishlistItem> wishlistItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        sessionManager = new SessionManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        wishlistItems = new ArrayList<>();
        adapter = new WishlistAdapter(getContext(), wishlistItems);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fetchDataFromServer();

        //backbutton code+++++++++++++++++++++++++++++++++++
        ImageView backbutton = view.findViewById(R.id.wishBack);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment wish = new Dashbaord_Fragment();
                FragmentManager wishManager = getFragmentManager();
                FragmentTransaction wishtransaction = wishManager.beginTransaction();
                wishtransaction.replace(R.id.bookingfragment, wish);
                wishtransaction.addToBackStack(null);
                wishtransaction.commit();
            }
        });
        return view;
    }

    private void fetchDataFromServer() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_customer_wishlist,
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
                                List<WishlistItem> driverList = new ArrayList<>();
                                for (int i = 0; i < driversArray.length(); i++) {
                                    JSONObject driverObject = driversArray.getJSONObject(i);
                                    String name = driverObject.getString("Driver_First_Name") + " " + driverObject.getString("Driver_Last_Name");
                                    String vehicle = driverObject.getString("Driver_Phone_No");
                                    String plateNumber = driverObject.getString("Wishlist_date");
                                    String imageURL = driverObject.getString("Driver_Profile_Image");

                                    driverList.add(new WishlistItem(imageURL, name, vehicle, plateNumber));
                                }

                                // Update the wishlistItems list and notify the adapter
                                wishlistItems.clear();
                                wishlistItems.addAll(driverList);
                                adapter.notifyDataSetChanged();
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
                params.put("Customer_ID", sessionManager.getUserId()); // Replace "" with the actual Vehicle Type
                // Add other parameters as needed
                return params;
            }
        };

        // Add the request to the request queue
        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
