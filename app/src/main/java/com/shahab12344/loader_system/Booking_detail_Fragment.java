package com.shahab12344.loader_system;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Booking_detail_Fragment extends Fragment implements OnMapReadyCallback {

    //+++++++++++++++++++++++++++++++++++++++++++variables++++++++++++++++++++++++++++++++++
    private static SessionManager sessionManager;
    private BookingSessionManager bookingSessionManager;
    private GoogleMap mMap;
    Button finish_ride;
    ImageView driver_image;
    String phonenumber;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    TextView vehicleNameTextView, driverNameTextView, rideCostTextView;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public Booking_detail_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_detail_, container, false);
        if (!isLocationEnabled()) {
            showLocationSettingsAlert();
        }

        //+++++++++++++++++++++++++++++++++++++++++++++getting ids+++++++++++++++++++++++++++++++++++++
        vehicleNameTextView = view.findViewById(R.id.vehiclename);
        driverNameTextView = view.findViewById(R.id.driverename);
        rideCostTextView = view.findViewById(R.id.cost);
        driver_image = view.findViewById(R.id.driver_image);
        sessionManager = new SessionManager(getContext());
        bookingSessionManager = new BookingSessionManager(getContext());
        ImageView callCustomerImageView = view.findViewById(R.id.drivercall);
        ImageView messageCustomerImageView = view.findViewById(R.id.cudtomer_message);

        //+++++++++++++++++++++++++++++++++++++Set click listeners for call and message icons
        callCustomerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +phonenumber));
                getActivity().startActivity(callIntent);
            }
        });
        messageCustomerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phonenumber));
                getActivity().startActivity(messageIntent);
            }
        });

        //++++++++++++++++++++++++++++++++++++++++++finish ride+++++++++++++++++++++++++++++++++
        finish_ride = view.findViewById(R.id.buttonridefinish);
        finish_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeStatus("Complete");
                Fragment payment = new Online_Booking();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.bookingfragment, payment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        //++++++++++++++++++++++++++++++++cancel button+++++++++++++++++++++++++++++++++++++
        Button cancel = view.findViewById(R.id.cancelride);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeStatus("Cancel");
                Fragment payment = new Dashbaord_Fragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.bookingfragment, payment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //++++++++++++++++++++++++++++Initialize FusedLocationProviderClient++++++++++++++++++++++++++
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //++++++++++++++++++++++++++++Get the SupportMapFragment and request the map asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        //++++++++++++++++++++++++++++++++++++++ Fetch booking details from server and update UI
        Bundle bundle = getArguments();
        if (bundle != null) {
            String bookingid = bundle.getString("booking_id");
            fetchBookingDetails(bookingid);
        }
        return view;
    }


    //+++++++++++++++++++++++++++++++Chnage status to complete/cancel+++++++++++++++++++++++++
    private void ChangeStatus(String newstatus) {
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
                                    Toast.makeText(getActivity(), "Booking is "+newstatus, Toast.LENGTH_SHORT).show();;
                                    sendtoDbRideHistory(newstatus);
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
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Booking_ID", bookingSessionManager.getKeyBookingId());
                    params.put("Booking_Status", newstatus);
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
                                if(newStatus.equals("Cancel")){
                                Dashbaord_Fragment fragment = new Dashbaord_Fragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.bookingfragment, fragment);
                                fragmentTransaction.addToBackStack(null); // Replace with the container ID
                                fragmentTransaction.commit();
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
                params.put("Customer_ID", bookingSessionManager.getCustomerID());
                params.put("Driver_ID", bookingSessionManager.getDriverID());
                params.put("Booking_ID", bookingSessionManager.getKeyBookingId());
                params.put("Fare_Amount", bookingSessionManager.getRideCost());
                params.put("Pick_up", bookingSessionManager.getPickupLocation());
                params.put("Drop_off", bookingSessionManager.getDropoffLocation());
                params.put("Ride_Status", newStatus);
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showLocationSettingsAlert() {
        Toast.makeText(getContext(), "Please turn on location services to use this feature.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    //++++++++++++++++++++++++++fetching booking detail from db++++++++++++++++++++++++++++++++++
    private void fetchBookingDetails(String Booking_id) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_BOOKINF_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                JSONObject bookingDetailsObject = jsonObject.getJSONObject("booking_details");

                                String vehicleName = bookingDetailsObject.getString("Vehicle_number") + " " + bookingDetailsObject.getString("Vehicle_Model");
                                bookingSessionManager.setVehicleName(vehicleName);

                                String driverName = bookingDetailsObject.getString("Driver_First_Name") + " " + bookingDetailsObject.getString("Driver_Last_Name");
                                bookingSessionManager.setDriverName(driverName);

                                String rideCost = bookingDetailsObject.getString("Total_Cost");
                                bookingSessionManager.setRideCost(rideCost);

                                String imageURL = bookingDetailsObject.getString("Driver_Profile_Image");
                                bookingSessionManager.setImageURL(imageURL);

                                String driverID = bookingDetailsObject.getString("Driver_ID");
                                bookingSessionManager.setDriverID(driverID);

                                String customerID = bookingDetailsObject.getString("Customer_ID");
                                bookingSessionManager.setCustomerID(customerID);

                                String bookingStatus = bookingDetailsObject.getString("Booking_Status");
                                bookingSessionManager.setBookingStatus(bookingStatus);

                                String pickupLocation = bookingDetailsObject.getString("Pickup_Location");
                                bookingSessionManager.setPickupLocation(pickupLocation);

                                String dropoffLocation = bookingDetailsObject.getString("Dropoff_Location");
                                bookingSessionManager.setDropoffLocation(dropoffLocation);
                                bookingSessionManager.setKeyBookingId(Booking_id);

                                phonenumber = bookingDetailsObject.getString("Driver_Phone_No");
                                vehicleNameTextView.setText("Vehicle Number: " + vehicleName);
                                driverNameTextView.setText("Driver Name: " + driverName);
                                rideCostTextView.setText("Fare Amount: " + rideCost);

                                // Load and display driver image
                                Picasso.get().load("http://10.0.2.2/Cargo_Go/v1/" + imageURL).into(driver_image); // Assuming you are using Picasso library
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
                        // Handle error cases
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Booking_ID", Booking_id);
                return params;
            }
        };

        // Add the request to the request queue
        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastLocation();
    }

    private void requestLocationPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-related tasks
                getLastLocation();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, request them
            requestLocationPermissions();
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    // Display the current location on the map
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }
            }
        });
    }


}
