package com.shahab12344.loader_system;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class imagepicker extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_1 = 1;
    private static final int PICK_IMAGE_REQUEST_2 = 2;
    private static final int PICK_IMAGE_REQUEST_3 = 3;

    private Spinner vehicleTypeSpinner;
    private TextView textViewFileName1;
    private TextView textViewFileName2;
    private TextView textViewFileName3;
    private Button buttonSelectImage1;
    private Button buttonSelectImage2;
    private Button buttonSelectImage3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepicker);

        textViewFileName1 = findViewById(R.id.cnic_textview);
        textViewFileName2 = findViewById(R.id.license_textview);
        textViewFileName3 = findViewById(R.id.vehicle_copy_textview);
        buttonSelectImage1 = findViewById(R.id.buttonUpload);
        buttonSelectImage2 = findViewById(R.id.btn_licence);
        buttonSelectImage3 = findViewById(R.id.btn_vehicle_copy);

        // Set click listeners for each button
        buttonSelectImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.with(imagepicker.this)
                        //	//Final image resolution will be less than 1080 x 1080(Optional)
                        .crop()
                        .start(PICK_IMAGE_REQUEST_1);
                //openImagePicker();
            }

        });

        buttonSelectImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(imagepicker.this)
                        //	//Final image resolution will be less than 1080 x 1080(Optional)
                        .crop()
                        .start(PICK_IMAGE_REQUEST_2);
            }
        });

        buttonSelectImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(imagepicker.this)
                        //	//Final image resolution will be less than 1080 x 1080(Optional)
                        .crop()
                        .start(PICK_IMAGE_REQUEST_3);
            }
        });


        //spinner
//        vehicleTypeSpinner = findViewById(R.id.spinnerOptions);
//
//        // Populate the spinner with vehicle types including the heading
//        String[] vehicleTypes = getResources().getStringArray(R.array.vehicle_types);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleTypes);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        vehicleTypeSpinner.setAdapter(adapter);

        // Set an initial selection to the heading
//        vehicleTypeSpinner.setSelection(0);
    }
//
//    private void openImagePicker(int requestCode) {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent, requestCode);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
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
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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
            // If unable to extract file name, use a default value
            fileName = "Unknown";
        }

        return fileName;
    }
}
