package com.shahab12344.loader_system;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link trip_detail_fragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class trip_detail_fragment extends Fragment {

    private EditText editTextPickup;
    private EditText editTextDestination;
    private EditText editTextPassengers;
    private Spinner spinnerCount;
    private Button buttonFindDriver;

    public trip_detail_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_detail_fragment, container, false);

        editTextPickup = view.findViewById(R.id.editTextPickup);
        editTextDestination = view.findViewById(R.id.editTextDestination);
        editTextPassengers = view.findViewById(R.id.editTextPassengers);
        spinnerCount = view.findViewById(R.id.spinnerCount);
        buttonFindDriver = view.findViewById(R.id.buttonFindDriver);

// Create an adapter with numbers 0 to 4 and the hint as the first item
        Integer[] numbers = new Integer[]{1, 2, 3, 4};
        String[] displayValues = new String[]{"Select No of Helpers", "1", "2", "3", "4"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, displayValues);
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
                Intent trip_detail = new Intent(getActivity(), ride_detail_customer_end.class);
                startActivity(trip_detail);

                // Perform actions based on the user input
                // For example, start a new activity or show a message
            }
        });

        return view;
    }
}
