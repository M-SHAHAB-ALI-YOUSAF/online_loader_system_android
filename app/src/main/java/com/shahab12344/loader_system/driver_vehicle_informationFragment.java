package com.shahab12344.loader_system;

import static android.opengl.ETC1.encodeImage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class driver_vehicle_informationFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST_1 = 1;
    private static final int PICK_IMAGE_REQUEST_2 = 2;
    private static final int PICK_IMAGE_REQUEST_3 = 3;

    private CardView selectedCard;
    private TextView selectedText;

    private TextView textViewFileName1;
    private TextView textViewFileName2;
    private TextView textViewFileName3;
    private Button buttonSelectImage1;
    private Button buttonSelectImage2;
    private Button buttonSelectImage3;
    int driverID;
    private String base64Image1, base64Image2, base64Image3, phone ;
    ImageView back_to_driver_detail, signup_done;
    private SessionManager sessionManager;


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

        // Retrieve references to the TextInputEditText elements
        TextInputLayout vehicleNumberLayout = view.findViewById(R.id.vehicle_number);
        TextInputLayout vehicleModelLayout = view.findViewById(R.id.vehicle_model);


         //++++++++++++++++++++++++++++++++++++++++++session+++++++++++++++++++++++++++++
        sessionManager = new SessionManager(getContext());
        driverifbyphoneno();


        //+++++++++++++++++++++bundle++++++++++++++++++++++++++++++++++++++++++++++++++
        Bundle bundle = getArguments();
        if (bundle != null) {
            phone = bundle.getString("phonekey");

        }

        //vehicle type+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        CardView smallCard = view.findViewById(R.id.small_card);
        CardView mediumCard = view.findViewById(R.id.medium_card);
        CardView largeCard = view.findViewById(R.id.l_large);
        CardView extraLargeCard = view.findViewById(R.id.e_large);

        //text++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        TextView smallText = view.findViewById(R.id.small_text);
        TextView mediumText = view.findViewById(R.id.medium_text);
        TextView largeText = view.findViewById(R.id.large_text);
        TextView extraLargeText = view.findViewById(R.id.extra_large_text);

        setCardClickListener(smallCard, smallText);
        setCardClickListener(mediumCard, mediumText);
        setCardClickListener(largeCard, largeText);
        setCardClickListener(extraLargeCard, extraLargeText);

        //+++++++++++++++++++++++++++++nside your onCreate or wherever you need to load and display the images
        ImageView image1 = view.findViewById(R.id.small);
        ImageView image2 = view.findViewById(R.id.medium);
        ImageView  image3 = view.findViewById(R.id.large);
        ImageView image4 = view.findViewById(R.id.extra_large);

        //++++++++++++++++++++++++++++++++++++++++++++++++++++ Load and set the images
        image1.setImageBitmap(loadAndScaleImage(R.drawable.small));
        image2.setImageBitmap(loadAndScaleImage(R.drawable.medium));
        image3.setImageBitmap(loadAndScaleImage(R.drawable.large));
        image4.setImageBitmap(loadAndScaleImage(R.drawable.extra_large));


        back_to_driver_detail = view.findViewById(R.id.signup_done);
        back_to_driver_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vehicleNumber = vehicleNumberLayout.getEditText().getText().toString().trim();
                String vehicleModel = vehicleModelLayout.getEditText().getText().toString().trim();

                vehicleNumberLayout.setError(null);
                vehicleModelLayout.setError(null);


                if (!vehicleNumber.matches("[A-Za-z]{1,3}[0-9]{2,4}")) {

                    vehicleNumberLayout.setError("Invalid vehicle number. It should contain 1 to 3 letters followed by 2 to 4 numbers.");
                    return;
                }


                if (vehicleModel.split("\\s+").length > 10) {

                    vehicleModelLayout.setError("Invalid vehicle model. It should contain at most 10 words.");
                    return;
                }

                if (!vehicleModel.matches("[A-Za-z ]+")) {
                    vehicleModelLayout.setError("Invalid vehicle model. It should contain only alphabets and spaces.");
                    return;
                }
                sendDataToServer(vehicleNumber, vehicleModel);

            }
        });

        signup_done = view.findViewById(R.id.back_to_signupdetail);
        signup_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String vehicleNumber = vehicleNumberLayout.getEditText().getText().toString();
                String vehicleModel = vehicleModelLayout.getEditText().getText().toString();

                sendDataToServer(vehicleNumber, vehicleModel);
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

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            String fileName = getFileNameFromUri(imageUri);

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                String base64Image = encodeImage(bitmap);

                if (requestCode == PICK_IMAGE_REQUEST_1) {
                    textViewFileName1.setText(fileName);
                    base64Image1 = base64Image;
                } else if (requestCode == PICK_IMAGE_REQUEST_2) {
                    textViewFileName2.setText(fileName);
                    base64Image2 = base64Image;
                } else if (requestCode == PICK_IMAGE_REQUEST_3) {
                    textViewFileName3.setText(fileName);
                    base64Image3 = base64Image;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "File not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //--------------------------------------fetching image name---------------------------------------
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


    private Bitmap loadAndScaleImage ( int resId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        return BitmapFactory.decodeResource(getResources(), resId, options);
    }


    //.......................................encode images....................
    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    //++++++++++++++++++++++++++++++++++++++++Vehicle table insertion+++++++++++++++++++++++++++++++++++++++++++++++
    private void sendDataToServer(String vehicleNumber, String vehicleModel) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.URL_VEHICLE_INFO;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            if (!error) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                Login_customers otp = new Login_customers();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.login_RegFragmentContainer, otp);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
                Toast.makeText(getContext(), "Error sending data to server!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Vehicle_Model", vehicleModel);
                params.put("Vehicle_type", selectedText.getText().toString());
                params.put("CNIC", base64Image1);
                params.put("Licence", base64Image2);
                params.put("Registration_Number", base64Image3);
                params.put("Driver_ID", String.valueOf(driverID));
                params.put("Vehicle_number", vehicleNumber);
                return params;
            }
        };

        queue.add(stringRequest);
    }


    //+++++++++++++++++++++++++++++TYpe++++++++++++++++++++++++++++++++++++++++++=
    private void setCardClickListener(CardView cardView, TextView textView) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCard != null) {
                    selectedCard.setSelected(false);
                }

                cardView.setSelected(true);
                selectedCard = cardView;
                selectedText = textView;
            }
        });
    }




      ///+++++++++++++++++++++++++++++++++++++getting driver id bby phone no+++++++++++++++++++++++++
    public void driverifbyphoneno(){

        String url = "http://10.0.2.2/Cargo_Go/v1/gettingDriverIDbyemail.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                    if (!jsonObject.getBoolean("error")) {
                                       driverID = jsonObject.getInt("Driver_ID");
                                    } else {
                                        String errorMessage = jsonObject.getString("message");
                                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                    }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                // Handle JSON parsing error
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Driver_Phone_No", phone);
                    return params;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);


    }

}