package com.shahab12344.loader_system;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ComplaintFragment extends Fragment {

    Button btn_submit_complaint;
    TextInputLayout complaintDescriptionLayout;
    private BookingSessionManager bookingSessionManager;
    TextInputEditText complaintDescriptionEditText;
    ImageView back_to_home;
    String complaintDescription;
    String selectedComplaintType;

    public ComplaintFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint, container, false);

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++Initialize views and components
        Spinner spinner = view.findViewById(R.id.complaint_type);
        String[] complaintTypes = getResources().getStringArray(R.array.driver_complaints);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, complaintTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // +++++++++++++++++++++++++++++++++++++++++++++Initialize TextInputLayout and EditText
        complaintDescriptionLayout = view.findViewById(R.id.complaint_text);
        complaintDescriptionEditText = view.findViewById(R.id.complaintDescriptionEditText);
        bookingSessionManager = new BookingSessionManager(getContext());

        // ++++++++++++++++++++++++++++++++++Button click listener+++++++++++++++++++++++++++
        btn_submit_complaint = view.findViewById(R.id.btn_complaint);
        btn_submit_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complaintDescription = complaintDescriptionEditText.getText().toString();
                selectedComplaintType = spinner.getSelectedItem().toString();

                if (isValidInput()) {
                    Register_Complaint();
                }
            }
        });

        back_to_home = view.findViewById(R.id.complaintback);
        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getActivity(), Booking_Activity.class);
                startActivity(home);
            }
        });

        return view;
    }

    public void Register_Complaint() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_COMPLAINT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("message")) {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Complaint Registered.", Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            Toast.makeText(getContext(), "Unable to connect to the server. Please check your internet connection.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Complaint_Description", complaintDescription);
                params.put("Complaint_Type", selectedComplaintType);
                params.put("Complaint_status", "Pending");
                params.put("Booking_ID", bookingSessionManager.getKeyBookingId());
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    //++++++++++++++++++++++++++++++++++ Function to validate the input++++++++++++++++++++++++++++++++++++++++++
    private boolean isValidInput() {
        if (complaintDescription.isEmpty()) {
            complaintDescriptionLayout.setError("Description cannot be empty");
            return false;
        } else {
            complaintDescriptionLayout.setError(null); // Clear any previous error
        }
        return true;
    }
}
