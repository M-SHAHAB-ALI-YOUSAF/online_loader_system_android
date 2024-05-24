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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Driver_History_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    TextView noDataTextView;
    String role, url;

    public Driver_History_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver__history_, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noDataTextView = view.findViewById(R.id.noDataTextView);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        sessionManager = new SessionManager(getContext());

        // +++++++++++++++++++++++++++ fetchDataFromServer method+++++++++++++++++++++++++++++
        fetchDataFromServer();

        //++++++++++++++++=back button+++++++++++++++++++++++++++++++++++++++++++++++++++++
        ImageView backbutton = view.findViewById(R.id.historyBack);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getRole().equals("Driver")){
                    Fragment history = new Driver_Homepage_Fragment();
                    FragmentTransaction historytransaction = getFragmentManager().beginTransaction();
                    historytransaction.replace(R.id.driver_fragment, history);
                    historytransaction.addToBackStack(null);
                    historytransaction.commit();
                }
                else{
                    Fragment History = new Dashbaord_Fragment();
                    FragmentManager HistoryManager = getFragmentManager();
                    FragmentTransaction Historytransaction = HistoryManager.beginTransaction();
                    Historytransaction.replace(R.id.bookingfragment, History);
                    Historytransaction.addToBackStack(null);
                    Historytransaction.commit();
                }
            }
        });
        return view;
    }


    private void fetchDataFromServer() {

        if(sessionManager.getRole().equals("Driver")){
             role = "Driver_ID";
             url = Constants.URL_driver_history;
        }
        if(sessionManager.getRole()
                .equals("Customer")){
             role = "Customer_ID";
             url = Constants.URL_customer_history;
        }
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            if (response == null || response.isEmpty()) {
                                Toast.makeText(getActivity(), "Empty response received", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                JSONArray driversArray = jsonObject.getJSONArray("ride_history");
                                List<Booking> bookingList = new ArrayList<>();
                                for (int i = 0; i < driversArray.length(); i++) {
                                    JSONObject driverObject = driversArray.getJSONObject(i);
                                    String Date = driverObject.getString("Date");
                                    String Pickup = driverObject.getString("Pick_up");
                                    String drop_off = driverObject.getString("Drop_off");
                                    String cost = driverObject.getString("Fare_Amount");
                                    String Status = driverObject.getString("Ride_Status");

                                    bookingList.add(new Booking(Date, Pickup, drop_off, cost, Status));
                                }
                                if (bookingList.isEmpty()) {
                                    noDataTextView.setVisibility(View.VISIBLE);
                                } else {
                                    noDataTextView.setVisibility(View.GONE);
                                }
                                adapter = new BookingHistoryAdapter( bookingList);
                                recyclerView.setAdapter(adapter);
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
                        // Handle error cases
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(role, sessionManager.getUserId());
                return params;
            }
        };

        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
