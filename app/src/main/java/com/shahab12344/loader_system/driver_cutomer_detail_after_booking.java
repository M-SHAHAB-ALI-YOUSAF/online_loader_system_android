package com.shahab12344.loader_system;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class driver_cutomer_detail_after_booking extends Fragment {
    Button finish_ride;
    ImageView customer_msg;
    public driver_cutomer_detail_after_booking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_driver_cutomer_detail_after_booking, container, false);

        //message_show
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

        return view;
    }
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
            Button buttonOption3 = dialogView.findViewById(R.id.jazzcashButton);

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

            buttonOption3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("Option 3 clicked");
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