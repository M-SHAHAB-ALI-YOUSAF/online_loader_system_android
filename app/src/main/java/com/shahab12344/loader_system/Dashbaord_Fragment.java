package com.shahab12344.loader_system;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Dashbaord_Fragment extends Fragment implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private EditText editTextPickup;
    private EditText editTextDestination;
    private EditText editTextPassengers;
    private Spinner spinnerCount;
    private Button buttonFindDriver;
    NavigationView navigationView;
    ProgressDialog progressDialog;

    private SessionManager sessionManager;
    String email, type_of_vehicle;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    ImageView hamburgerIcon;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public Dashbaord_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashbaord_, container, false);

        //+++++++++++++++++++++++++++++++++session
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getActivity());

        if (!isLocationEnabled()) {
            showLocationSettingsAlert();
        }

        // +++++++++++++++++++Inside your onCreate or wherever you need to load and display the images
        ImageView image1 = view.findViewById(R.id.small);
        ImageView image2 = view.findViewById(R.id.medium);
        ImageView  image3 = view.findViewById(R.id.large);
        ImageView image4 = view.findViewById(R.id.extra_large);

        //++++++++++++++++++++++ Load and set the images++++++++++++++++++++++++++++++++++
        image1.setImageBitmap(loadAndScaleImage(R.drawable.small));
        image2.setImageBitmap(loadAndScaleImage(R.drawable.medium));
        image3.setImageBitmap(loadAndScaleImage(R.drawable.large));
        image4.setImageBitmap(loadAndScaleImage(R.drawable.extra_large));

        //+++++++++++++++++++showing trip detail++++++++++++++++++++++++++++++++
        editTextPickup = view.findViewById(R.id.editTextPickup);
        editTextDestination = view.findViewById(R.id.editTextDestination);
        editTextPassengers = view.findViewById(R.id.editTextPassengers);
        spinnerCount = view.findViewById(R.id.spinnerCount);
        buttonFindDriver = view.findViewById(R.id.buttonFindDriver);

        //++++++++++ Create an adapter with numbers 0 to 4 and the hint as the first +++++++++++++++++++++++
        Integer[] numbers = new Integer[]{1, 2, 3, 4};
        String[] displayValues = new String[]{"Select No of Helpers", "1", "2", "3", "4"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayValues);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //+++++++++++++++++++ Set the adapter to the spinner+++++++++++++++++
        spinnerCount.setAdapter(spinnerAdapter);
        spinnerCount.setSelection(0);

        //truck size card views++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        CardView smalltruck = view.findViewById(R.id.smalltruck);
        CardView mediumtruck = view.findViewById(R.id.mediumtruck);
        CardView largetruck = view.findViewById(R.id.largetruck);
        CardView elargetruck = view.findViewById(R.id.elargetruck);


        smalltruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smalltruck.setSelected(true);
                mediumtruck.setSelected(false);
                largetruck.setSelected(false);
                elargetruck.setSelected(false);
                type_of_vehicle = "Small";
                updateTotalCost();
            }
        });

        mediumtruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediumtruck.setSelected(true);
                largetruck.setSelected(false);
                elargetruck.setSelected(false);
                smalltruck.setSelected(false);
                type_of_vehicle = "Medium";
                updateTotalCost();
            }
        });

        largetruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                largetruck.setSelected(true);
                smalltruck.setSelected(false);
                elargetruck.setSelected(false);
                mediumtruck.setSelected(false);
                type_of_vehicle = "large";
                updateTotalCost();
            }
        });

        elargetruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elargetruck.setSelected(true);
                smalltruck.setSelected(false);
                mediumtruck.setSelected(false);
                largetruck.setSelected(false);
                type_of_vehicle = "extra_large";
                updateTotalCost();
            }
        });




        buttonFindDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pickup = editTextPickup.getText().toString();
                String destination = editTextDestination.getText().toString();
                String passengersStr = editTextPassengers.getText().toString();
                String selectedCountStr = spinnerCount.getSelectedItem().toString();

                if (selectedCountStr.equals("Select No of Helpers")) {
                    Toast.makeText(getContext(), "Please select number of helpers", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedCount = Integer.parseInt(selectedCountStr);
                if (pickup.isEmpty() || destination.isEmpty() || passengersStr.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("pickup", pickup);
                bundle.putString("destination", destination);
                bundle.putString("helpers", selectedCountStr);
                bundle.putString("cost", passengersStr);
                bundle.putString("vehicleType", type_of_vehicle);

                fragment_driver_request fragment = new fragment_driver_request();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.bookingfragment, fragment);
                fragmentTransaction.addToBackStack(null); // Replace with the container ID
                fragmentTransaction.commit();


            }
        });


        //side drawer code
        drawerLayout = view.findViewById(R.id.drawerLayout);

        hamburgerIcon = view.findViewById(R.id.hamburger_icon);


        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView = view.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView headerTextView = headerView.findViewById(R.id.User_name_in_header);
        ImageView profile = headerView.findViewById(R.id.userprofile);
        String profileImageUri = sessionManager.getProfileImageUri();
        if (profileImageUri != null) {
            Glide.with(this)
                    .load("http://10.0.2.2/Cargo_Go/v1/" + profileImageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile);
        }

        String firstName = sessionManager.getFirstName();

        headerTextView.setText(firstName);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);



        //++++++++++++++++++++++++++ Initialize FusedLocationProviderClient++++++++++++++++++++++++++++++++++
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction trip_detail = getFragmentManager().beginTransaction();
            trip_detail.add(R.id.map, mapFragment);
            trip_detail.commit();
        }

        mapFragment.getMapAsync(this);
        ImageView currentLocationButton = view.findViewById(R.id.current);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    requestLocationPermissions();
                } else {
                    getLastLocation();
                }
            }
        });



        return view;
    }




    //++++++++++++++++++++++++++++++++++++++++++++location+++++++++++++++++++++++++++++++++++++++
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showLocationSettingsAlert() {
        Toast.makeText(getContext(), "Please turn on location services to use this feature.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastLocation();
    }

    private void requestLocationPermissions() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.Profile:
                Show_Profile_customerFragment Profile = new Show_Profile_customerFragment();
                FragmentManager profileManager = getFragmentManager();
                FragmentTransaction Profiletransaction = profileManager.beginTransaction();
                Profiletransaction.replace(R.id.bookingfragment, Profile);
                Profiletransaction.addToBackStack(null);
                Profiletransaction.commit();
                break;

            case R.id.phoneChange:
                Fragment changenumber = new Chnage_Phone_No();
                FragmentManager ChnaagephoneManager = getFragmentManager();
                FragmentTransaction Chnaagephonetransaction = ChnaagephoneManager.beginTransaction();
                Chnaagephonetransaction.replace(R.id.bookingfragment, changenumber);
                Chnaagephonetransaction.addToBackStack(null);
                Chnaagephonetransaction.commit();
                break;

            case R.id.FAQ:
                Fragment faq = new FaqFragment();
                FragmentManager faqManager = getFragmentManager();
                FragmentTransaction faqtransaction = faqManager.beginTransaction();
                faqtransaction.replace(R.id.bookingfragment, faq);
                faqtransaction.addToBackStack(null);
                faqtransaction.commit();
                break;

            case R.id.logout:
                sessionManager.logoutUser();
                Intent intent = new Intent(requireActivity(), Login_Registration.class); // Replace with your login activity
                startActivity(intent);
                break;

            case R.id.History:
                Fragment History = new Driver_History_Fragment();
                FragmentManager HistoryManager = getFragmentManager();
                FragmentTransaction Historytransaction = HistoryManager.beginTransaction();
                Historytransaction.replace(R.id.bookingfragment, History);
                Historytransaction.addToBackStack(null);
                Historytransaction.commit();
                break;

            case R.id.Wishlist:
                Fragment wish = new WishlistFragment();
                FragmentManager wishManager = getFragmentManager();
                FragmentTransaction wishtransaction = wishManager.beginTransaction();
                wishtransaction.replace(R.id.bookingfragment, wish);
                wishtransaction.addToBackStack(null);
                wishtransaction.commit();
                break;

            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Remove Account");
                builder.setMessage("Are you sure you want to remove your account?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        removeAccount();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getActivity(), "Account NOT removed!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }


    //+++++++++++++++++++++++++++++++++++++++remove account+++++++++++++++++++++++++++++++++++++++++++++++++
    private void removeAccount() {
        progressDialog.setMessage("Removing User Account...");
        progressDialog.show();

        // Create a StringRequest with POST method
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DELETE_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("message").matches("true")) {
                                startActivity(new Intent(getActivity(), Login_Registration.class));

                                Toast.makeText(getActivity(), "User removed successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
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
                if(sessionManager.getRole()=="Driver"){
                     email = "Driver_Email";
                }
                else{
                    email = "Email";
                }
                params.put(email, sessionManager.getEmail());
                return params;
            }
        };

        // Add the request to the request queue
        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private Bitmap loadAndScaleImage (int resId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        return BitmapFactory.decodeResource(getResources(), resId, options);
    }


    // +++++++++++++++++++++Method to update the total cost and display it in the edit passenger EditText
    private void updateTotalCost() {
        String selectedCountStr = spinnerCount.getSelectedItem().toString();
        if (selectedCountStr.equals("Select No of Helpers")) {
            return;
        }

        int selectedCount = Integer.parseInt(selectedCountStr);
        int totalCost = 0;
        switch (type_of_vehicle) {
            case "Small":
                totalCost = selectedCount * 5000;
                break;
            case "Medium":
                totalCost = selectedCount * 6000;
                break;
            case "large":
                totalCost = selectedCount * 7000;
                break;
            case "extra_large":
                totalCost = selectedCount * 8000;
                break;
            default:
                break;
        }

        editTextPassengers.setText(String.valueOf(totalCost));
        int updated_cost = totalCost - 500;
                if (totalCost < updated_cost) {
                    Toast.makeText(getContext(), "Total cost cannot be less than " + updated_cost, Toast.LENGTH_SHORT).show();
                    return;
                }
    }

}