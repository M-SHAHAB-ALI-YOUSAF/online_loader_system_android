package com.shahab12344.loader_system;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class driver_vehicle_informationFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST_1 = 1;
    private static final int PICK_IMAGE_REQUEST_2 = 2;
    private static final int PICK_IMAGE_REQUEST_3 = 3;

    private TextView textViewFileName1;
    private TextView textViewFileName2;
    private TextView textViewFileName3;
    private Button buttonSelectImage1;
    private Button buttonSelectImage2;
    private Button buttonSelectImage3 ;
    ImageView back_to_driver_detail, signup_done;

    public driver_vehicle_informationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_vehicle_information, container, false);

        textViewFileName1 = view.findViewById(R.id.cnic_textview);
        textViewFileName2 = view.findViewById(R.id.license_textview);
        textViewFileName3 = view.findViewById(R.id.vehicle_copy_textview);
        buttonSelectImage1 = view.findViewById(R.id.buttonUpload);
        buttonSelectImage2 = view.findViewById(R.id.btn_licence);
        buttonSelectImage3 = view.findViewById(R.id.btn_vehicle_copy);


        back_to_driver_detail = view.findViewById(R.id.signup_done);
        back_to_driver_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent booking = new Intent(getActivity(), Booking_Activity.class);
               startActivity(booking);
                  }
        });

        signup_done = view.findViewById(R.id.back_to_signupdetail);
        signup_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Signup_driverFragment();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });

        // Set click listeners for each button
        buttonSelectImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImagePicker(PICK_IMAGE_REQUEST_1);
            }
        });

        buttonSelectImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImagePicker(PICK_IMAGE_REQUEST_2);
            }
        });

        buttonSelectImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImagePicker(PICK_IMAGE_REQUEST_3);
            }
        });

        return view;
    }

    private void launchImagePicker(int requestCode) {
        ImagePicker.with(this)
                .crop()
                .start(requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            String fileName = getFileNameFromUri(imageUri);

            if (requestCode == PICK_IMAGE_REQUEST_1) {
                textViewFileName1.setText(fileName);
            } else if (requestCode == PICK_IMAGE_REQUEST_2) {
                textViewFileName2.setText(fileName);
            } else if (requestCode == PICK_IMAGE_REQUEST_3) {
                textViewFileName3.setText(fileName);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme != null && scheme.equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex);
                }
                cursor.close();
            }
        } else if (scheme != null && scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        }

        if (fileName == null) {
            fileName = "Unknown";
        }

        return fileName;
    }

}