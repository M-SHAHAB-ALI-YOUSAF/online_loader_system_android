package com.shahab12344.loader_system;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class fragment_driver_request extends Fragment {
    private RecyclerView recyclerView;
    private DriverInfoAdapter adapter;

    public fragment_driver_request() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_request, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.driverRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create a list of driver information
        List<DriverRequstModel> driverList = new ArrayList<>();
        driverList.add(new DriverRequstModel(R.drawable.final_driver, "John Doe", "Toyota Camry", "ABC 123", "$20.00"));
        driverList.add(new DriverRequstModel(R.drawable.final_customer, "Jane Smith", "Honda Accord", "XYZ 456", "$18.50"));

        // Create and set the adapter
        FragmentManager fragmentManager = getFragmentManager();
        adapter = new DriverInfoAdapter(getContext(), driverList, fragmentManager);
        recyclerView.setAdapter(adapter);

        return view;
    }
}