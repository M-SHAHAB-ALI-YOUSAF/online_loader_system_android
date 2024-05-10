package com.shahab12344.loader_system;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.util.Base64;

public class Edit_customer_profileFragment extends Fragment {

    ImageView back_to_show_profile, btn_edit_done;
    private final int PICK_IMAGE_REQUEST = 71;

    private SessionManager sessionManager;

    private ImageView imageView;
    private TextInputLayout fNameInput, lastNameInput, emailInput, phoneInput;

    private String userId, url, fname_db, lname_db, email_db, phone_db, image_db, role_db;
    private String base64Image = null;

    public Edit_customer_profileFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_customer_profile, container, false);

        sessionManager = new SessionManager(getContext());



        //update
        btn_edit_done = view.findViewById(R.id.btn_profile_edit_done);
        btn_edit_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        fNameInput = view.findViewById(R.id.et_f_name);
        lastNameInput = view.findViewById(R.id.et_l_name);
        emailInput = view.findViewById(R.id.et_email);
        phoneInput = view.findViewById(R.id.et_phone);
        userId = sessionManager.getUserId();

        String firstName = sessionManager.getFirstName();
        String lastName = sessionManager.getLastName();
        String email = sessionManager.getEmail();
        String phoneNumber = sessionManager.getPhoneNumber();

        // Set the retrieved user data to TextInputLayouts
        fNameInput.getEditText().setText(firstName);
        lastNameInput.getEditText().setText(lastName);
        emailInput.getEditText().setText(email);
        phoneInput.getEditText().setText(phoneNumber);

        imageView = view.findViewById(R.id.profile_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(Edit_customer_profileFragment.this)
                        .crop()
                        .start();
            }
        });
//        Picasso.get().load(R.drawable.person_).into(imageView);
        String profileImageUri = sessionManager.getProfileImageUri();

        if (profileImageUri != null) {
            // Load the profile image using Glide and transform it into a circle
            Glide.with(this)
                    .load("http://10.0.2.2/Cargo_Go/v1/" + profileImageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
        else {
            // Load a default image if the URI is null
            Picasso.get().load(R.drawable.person_).into(imageView);
        }



        //++++++++++++++++=back button++++++++++++++++++++++++++++++++++++++++++++
        ImageView backbutton = view.findViewById(R.id.back_edit_profile);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getRole().equals("Driver")){
                    Fragment editprofile = new Driver_profile_Fragment();
                    FragmentTransaction editprofiletransaction = getFragmentManager().beginTransaction();
                    editprofiletransaction.replace(R.id.driver_fragment, editprofile);
                    editprofiletransaction.addToBackStack(null); // This line adds the transaction to the back stack
                    editprofiletransaction.commit();
                }
                else{
                    Fragment editprofile = new Dashbaord_Fragment();
                    FragmentManager editprofileManager = getFragmentManager();
                    FragmentTransaction faqtransaction = editprofileManager.beginTransaction();
                    faqtransaction.replace(R.id.bookingfragment, editprofile);
                    faqtransaction.addToBackStack(null);
                    faqtransaction.commit();
                }
            }
        });



        return view;
    }



    // Update user profile
    private void updateProfile() {
        String firstName = fNameInput.getEditText().getText().toString().trim();
        String lastName = lastNameInput.getEditText().getText().toString().trim();
        String email = emailInput.getEditText().getText().toString().trim();
        String phone = phoneInput.getEditText().getText().toString().trim();

        // Validate input fields, and you can add more validation as needed
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(sessionManager.getRole() == "Driver"){
            url = "http://10.0.2.2/Cargo_Go/v1/updateDriver.php";
            fname_db = "Driver_First_Name";
            lname_db = "Driver_Last_Name";
            email_db = "Driver_Email";
            phone_db = "Driver_Phone_No";
            image_db = "Driver_Profile_Image";
            role_db = "Driver";

        }
        else{
            url = "http://10.0.2.2/Cargo_Go/v1/updateUser.php";
            fname_db = "First_Name";
            lname_db = "Last_Name";
            email_db = "Email";
            phone_db = "Phone_No";
            image_db = "Profile_Image";
            role_db = "Customer";

        }

        // Create a HashMap to hold the updated user data
        Map<String, String> params = new HashMap<>();
        params.put(fname_db, firstName);
        params.put(lname_db, lastName);
        params.put(email_db, email);
        params.put(phone_db, phone);

        // If a new image is selected, send the base64 representation to the server
        if (base64Image != null) {
            params.put(image_db, base64Image);
        }
//        else {
//            // If no new image is selected, send the existing profile image URL to the server
//            params.put(image_db, sessionManager.getProfileImageUri());
//        }

        // Send a POST request to your PHP server for updating the user profile
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            if (!error) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                String imagePath = jsonResponse.optString("image_path"); // Use optString to handle null values
                                if (imagePath != null && !imagePath.isEmpty()) {
                                    updateSessionData(firstName, lastName, email, phone, imagePath);
                                } else {
                                    // If image path is null or empty, keep the existing image path in the session
                                    String existingImagePath = sessionManager.getProfileImageUri();
                                    updateSessionData(firstName, lastName, email, phone, existingImagePath);
                                }
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        queue.add(stringRequest);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);

                ByteArrayOutputStream byteArrayOutputStream;
                byteArrayOutputStream = new ByteArrayOutputStream();
                if(bitmap!=null){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    // Update session
    private void updateSessionData(String firstName, String lastName, String email, String phone, String profileImage) {
        sessionManager.createUserSession(userId, firstName, lastName, email, phone, role_db);
        sessionManager.saveProfileImageUri(profileImage); // Save the image path in the session
    }


}
