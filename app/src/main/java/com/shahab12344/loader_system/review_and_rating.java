package com.shahab12344.loader_system;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class review_and_rating extends Fragment {

    private RatingBar ratingBar;
    private EditText reviewEditText;
    private BookingSessionManager bookingSessionManager;
    String fragment2;
    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_and_rating, container, false);

        ratingBar = view.findViewById(R.id.ratingBar);
        reviewEditText = view.findViewById(R.id.reviewEditText);
        submitButton = view.findViewById(R.id.submitButton);
        bookingSessionManager = new BookingSessionManager(getContext());
        TextView nameview = view.findViewById(R.id.driver_name_rating);
        ImageView driverimg = view.findViewById(R.id.driverimage);

        nameview.setText(bookingSessionManager.getDriverName());

        String profileImageUri = bookingSessionManager.getImageURL();
        if (profileImageUri != null) {
            Glide.with(this)
                    .load("http://10.0.2.2/Cargo_Go/v1/" + profileImageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(driverimg);
        }

        TextInputLayout textInputLayout = view.findViewById(R.id.review);
        TextInputEditText textInputEditText = view.findViewById(R.id.reviewEditText);

        textInputLayout.setHint("Enter Review");

        Bundle bundle = getArguments();
        if (bundle != null) {
             fragment2 = bundle.getString("Fragment");
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ratingBar.getRating();
                String review = reviewEditText.getText().toString().trim();

                if (rating < 2) {

                    reviewtoDb(String.valueOf(rating), review);
              //------------------------------------- Show an AlertDialog for complaining
                    new AlertDialog.Builder(getContext())
                            .setTitle("Complaint")
                            .setMessage("Do you want to lodge a complaint?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Navigate to ComplaintFragment
                                    ComplaintFragment fragment = new ComplaintFragment();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    if ("ComeFromFragment".equals(fragment2)) {
                                        transaction.replace(R.id.bookingfragment, fragment);
                                    } else {
                                        transaction.replace(R.id.feedbackarea, fragment);
                                    }

                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                } else if (rating >= 2) {
                    reviewtoDb(String.valueOf(rating), review);

                    new AlertDialog.Builder(getContext())
                            .setTitle("Add to Wishlist")
                            .setMessage("Do you want to add the driver to your wishlist?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    addToWishlist();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
                else{
                    if(review.isEmpty()){
                        Toast.makeText(getActivity(), "Please enter review", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        reviewtoDb(String.valueOf(rating), review);
                    }
                }
            }
        });



        return view;
    }


    //----------------------------------------------driver to wihslist------------------------
    private void addToWishlist(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_ADD_TO_WISHLIST,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), Booking_Activity.class);
                            startActivity(intent);

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
                params.put("Customer_ID", bookingSessionManager.getCustomerID());
                params.put("Driver_ID", bookingSessionManager.getDriverID());
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    //---------------------------------review to db-------------------------------------------

    private void reviewtoDb(String rating, String review){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_REVIEW_RATING,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                    params.put("Customer_ID", bookingSessionManager.getCustomerID());
                    params.put("Driver_ID", bookingSessionManager.getDriverID());
                    params.put("Booking_ID", bookingSessionManager.getKeyBookingId());
                    params.put("Rating", rating);
                    params.put("Review_Text", review);

                    return params;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
