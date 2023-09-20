
package com.shahab12344.loader_system;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


public class Booking_detail_Fragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    Button finish_ride;
    ImageView customer_msg;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;


    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public Booking_detail_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking_detail_, container, false);
        //location must on
        if (!isLocationEnabled()) {
            showLocationSettingsAlert();
        }


        ///next to go
        customer_msg = view.findViewById(R.id.cudtomer_message);
        customer_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent msg = new Intent(getActivity(), message_customer_end.class);
                startActivity(msg);
            }
        });

        //finish ride
        finish_ride = view.findViewById(R.id.buttonridefinish);
        finish_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });

//        //showing trip detail
//        driver_cutomer_detail_after_booking fragment = new driver_cutomer_detail_after_booking();
//
//        // Get the FragmentManager and start a transaction
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        // Replace the fragment container with your fragment
//        fragmentTransaction.replace(R.id.fragmant_area_for_booking_detail, fragment);
//
//        // Commit the transaction
//        fragmentTransaction.commit();

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


    //dialog method work
    private void showCustomDialog() {
        DialogFragment dialog = new MyDialogFragment();
        dialog.show(getFragmentManager(), "Payment Method");
    }

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.payment_method, null);

            Button buttonOption1 = dialogView.findViewById(R.id.cashButton);
            Button buttonOption2 = dialogView.findViewById(R.id.easypaisaButton);


            buttonOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("Option 1 clicked");
                    dismiss();
                }
            });

            buttonOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("Option 2 clicked");
                    dismiss();

                }
            });


            builder.setView(dialogView);

            return builder.create();
        }

        private void showToast(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}