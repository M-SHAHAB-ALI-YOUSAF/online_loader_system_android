package com.shahab12344.loader_system;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Online_Booking extends Fragment {

    private RadioGroup radioGroup;
    private SessionManager sessionManager;
    private BookingSessionManager bookingDetailSaveFile;
    private Button confirmPaymentButton;

    public Online_Booking() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online__booking, container, false);

        // Initialize views
        radioGroup = view.findViewById(R.id.radioGroup);
        confirmPaymentButton = view.findViewById(R.id.buttonConfirmPayment);
        sessionManager = new SessionManager(getContext());
        bookingDetailSaveFile = new BookingSessionManager(getContext());

        // Set OnClickListener for the Confirm Payment Button
        confirmPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the ID of the selected RadioButton
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // Check if a RadioButton is selected
                if (selectedId != -1) {
                    // Find the selected RadioButton
                    RadioButton selectedRadioButton = view.findViewById(selectedId);

                    // Perform the desired action
                    String paymentMethod = selectedRadioButton.getText().toString();
                    if(paymentMethod.equals("Cash on Delivery")){
                        SendTransactionToDb();
                    }
                    else{
                        Intent online_payment = new Intent(getActivity(), PaymentMethod.class);
                        startActivity(online_payment);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select a payment method", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void SendTransactionToDb(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_PAYMENT_CASH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response here
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("message")) {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                Fragment review = new review_and_rating();
                                FragmentManager faqManager = getFragmentManager();
                                FragmentTransaction faqtransaction = faqManager.beginTransaction();

                                Bundle bundle = new Bundle();
                                bundle.putString("Fragment", "ComeFromFragment");
                                review.setArguments(bundle);
                                faqtransaction.replace(R.id.bookingfragment, review);
                                faqtransaction.addToBackStack(null);
                                faqtransaction.commit();

                            } else {
                                // If the response does not contain a "message" key, you can display a generic success message
                                Toast.makeText(getContext(), "Payment is Complete.", Toast.LENGTH_LONG).show();
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
                            // Handle connection error here
                            Toast.makeText(getContext(), "Unable to connect to the server. Please check your internet connection.", Toast.LENGTH_LONG).show();
                        } else {
                            // Handle other errors
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Booking_ID", bookingDetailSaveFile.getKeyBookingId());
                params.put("Customer_ID", sessionManager.getUserId());
                params.put("Amount", bookingDetailSaveFile.getRideCost());
                params.put("Payment_Method", "Cash");
                return params;
            }
        };

        // Add the request to the Volley request queue
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
