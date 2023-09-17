package com.shahab12344.loader_system;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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

public class Dashbaord_Fragment extends Fragment implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private EditText editTextPickup;
    private EditText editTextDestination;
    private EditText editTextPassengers;
    private Spinner spinnerCount;
    private Button buttonFindDriver;
    NavigationView navigationView;

    //session
    private SessionManager sessionManager;

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

        //session
        sessionManager = new SessionManager(getContext());


        //location must on
        if (!isLocationEnabled()) {
            showLocationSettingsAlert();
        }

        //showing trip detail
        editTextPickup = view.findViewById(R.id.editTextPickup);
        editTextDestination = view.findViewById(R.id.editTextDestination);
        editTextPassengers = view.findViewById(R.id.editTextPassengers);
        spinnerCount = view.findViewById(R.id.spinnerCount);
        buttonFindDriver = view.findViewById(R.id.buttonFindDriver);

// Create an adapter with numbers 0 to 4 and the hint as the first item
        Integer[] numbers = new Integer[]{1, 2, 3, 4};
        String[] displayValues = new String[]{"Select No of Helpers", "1", "2", "3", "4"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayValues);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set the adapter to the spinner
        spinnerCount.setAdapter(spinnerAdapter);
        spinnerCount.setSelection(0); // Set the hint as the selected item




        buttonFindDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Retrieve values from EditText fields and Spinner
//                String pickup = editTextPickup.getText().toString();
//                String destination = editTextDestination.getText().toString();
//                String passengers = editTextPassengers.getText().toString();
//                int selectedCount = (int) spinnerCount.getSelectedItem();
                //---- PreLogin Fragement ---

                Fragment fragment = new Booking_detail_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, fragment).commit();

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
        //adding textview header
        View headerView = navigationView.getHeaderView(0);

        // Find the TextView in the header layout by its ID
        TextView headerTextView = headerView.findViewById(R.id.User_name_in_header);

        // Retrieve the user's first name from SessionManager
        String firstName = sessionManager.getFirstName();

        // Set the first name in the TextView
        headerTextView.setText(firstName);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);



        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Get the SupportMapFragment and request the map asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction trip_detail = getFragmentManager().beginTransaction();
            trip_detail.add(R.id.map, mapFragment);
            trip_detail.commit();
        }

        mapFragment.getMapAsync(this);


        return view;
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
                // Permission granted, proceed with location-related tasks
                getLastLocation();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (item.getItemId()) {
            // Handle other navigation items

            case R.id.Profile:
                Fragment fragment = new Show_Profile_customerFragment();
                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, fragment).commit();
                break;

            case R.id.FAQ:
                Fragment fragment2 = new FaqFragment();
                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, fragment2).commit();
                break;

            case R.id.logout:
                sessionManager.logoutUser();

                // Navigate back to the login or splash screen
                Intent intent = new Intent(requireActivity(), Login_Registration.class); // Replace with your login activity
                startActivity(intent);
                break;
            case R.id.History:
                Fragment review = new review_and_rating();
                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, review).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }
}