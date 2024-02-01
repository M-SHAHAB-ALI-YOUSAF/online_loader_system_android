package com.shahab12344.loader_system;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup_driverFragment extends Fragment {
    Button btn_vehicle_info, btn_go_login;
    private ImageView imageView, selectimage;
    String firstname, lastname, email, phoneno, status, returnrole, trimmedPhoneNumber;

    private Dialog dialog;
    //++++++++++++++++++++++++++++++++Firebase variables++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextInputLayout textInputdriverFirstname, textInputdriverLastname, textInputdriverEmail, textInputdriverPhoneno;
    private ProgressDialog progressDialog;

    public Signup_driverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_driver, container, false);


        //++++++++++++++++++++++++++++++++Getting ids++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        textInputdriverFirstname = view.findViewById(R.id.reg_d_fname);
        textInputdriverLastname = view.findViewById(R.id.reg_d_lastname);
        textInputdriverEmail = view.findViewById(R.id.reg_d_email);
        textInputdriverPhoneno = view.findViewById(R.id.reg_d_phoneNo);


        //++++++++++++++++++++++++++++validate function+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        validation();


        //++++++++++++++++++++++++++++++++Bundle for data coming from otp++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getString("verfiy");
            firstname = bundle.getString("f_name");
            lastname = bundle.getString("l_name");
            email = bundle.getString("email");
            returnrole = bundle.getString("Roles");
            phoneno = bundle.getString("phone");
            trimmedPhoneNumber = phoneno.replace("+92", "");
        }


        //sessionManager = new SessionManager(getContext());

        //++++++++++++++++++++++++++++++++FOR OTP IS VERIFIED OR NOT++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        if (status == "true") {
            textInputdriverFirstname.getEditText().setText(firstname);
            textInputdriverLastname.getEditText().setText(lastname);
            textInputdriverEmail.getEditText().setText(email);
            textInputdriverPhoneno.getEditText().setText(trimmedPhoneNumber);

            senddatatodatabase();
        }


        //+++++++++++++++++++++++++++++=custome dialog+++++++++++++++++++++++++++++++++++++++++
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_layout);
        // Get the drawable resource using ContextCompat.getDrawable()
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(drawable);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Login_customers otp = new Login_customers();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.login_RegFragmentContainer, otp);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());



        btn_vehicle_info = view.findViewById(R.id.user_info_btn);
        btn_vehicle_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerdriver();
//                Fragment fragment = new driver_vehicle_informationFragment();
//                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });

        btn_go_login = view.findViewById(R.id.reg_login_btn);
        btn_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new Login_customers();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });

        //image setting
        selectimage = view.findViewById(R.id.addphoto);
        imageView = view.findViewById(R.id.imageView);

        //image picker

        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.with(Signup_driverFragment.this)
                        //	//Final image resolution will be less than 1080 x 1080(Optional)
                        .crop()
                        .start();
                //openImagePicker();
            }
        });

        return view;
    }


    //+++++++++++++++++++++++++++++++++validation of input fields+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void validation() {

        textInputdriverFirstname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFirstname(s.toString().trim());
            }
        });

        textInputdriverLastname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateLastname(s.toString().trim());
            }
        });

        textInputdriverEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail(s.toString().trim());
            }
        });

        textInputdriverPhoneno.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePhoneno("+92"+s.toString().trim());
            }
        });

        textInputdriverFirstname.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateFirstname(textInputdriverFirstname.getEditText().getText().toString().trim());
            }
        });

        textInputdriverLastname.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateLastname(textInputdriverLastname.getEditText().getText().toString().trim());
            }
        });

        textInputdriverEmail.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmail(textInputdriverEmail.getEditText().getText().toString().trim());
            }
        });

        textInputdriverPhoneno.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePhoneno("+92"+textInputdriverPhoneno.getEditText().getText().toString().trim());
            }
        });
    }


    private boolean isAlphaString(String input) {
        return input.matches("[a-zA-Z]+");
    }

    private void validateFirstname(String firstname) {
        textInputdriverFirstname.setError(null);

        if (firstname.isEmpty()) {
            textInputdriverFirstname.setError("First name is required");
        } else if (!isAlphaString(firstname)) {
            textInputdriverFirstname.setError("First name should only contain alphabetic characters");
        }
    }

    private void validateLastname(String lastname) {
        textInputdriverLastname.setError(null);

        if (lastname.isEmpty()) {
            textInputdriverLastname.setError("Last name is required");
        } else if (!isAlphaString(lastname)) {
            textInputdriverLastname.setError("Last name should only contain alphabetic characters");
        }
    }

    private void validateEmail(String email) {
        textInputdriverEmail.setError(null);

        if (email.isEmpty()) {
            textInputdriverEmail.setError("Email is required");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputdriverEmail.setError("Invalid email format");
        }
    }

    private void validatePhoneno(String phoneno) {
        textInputdriverPhoneno.setError(null);

        if (phoneno.isEmpty()) {
            textInputdriverPhoneno.setError("Phone number is required");
        } else if (!isValidPakistanPhoneNumber(phoneno)) {
            textInputdriverPhoneno.setError("Invalid Phone No pattern");
        }
    }


    //++++++++++++++++++++++++++++++++Pakistani phone no validation++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private boolean isValidPakistanPhoneNumber(String phoneNumber) {
        String pakistanPhoneNumberPattern = "^(\\+92|0)[3]{1}[0-4]{1}[0-9]{8}$";
        Pattern pattern = Pattern.compile(pakistanPhoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }



    //++++++++++++++++++++++++++++++++For register a new driver ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void registerdriver() {
        firstname = textInputdriverFirstname.getEditText().getText().toString().trim();
        lastname = textInputdriverLastname.getEditText().getText().toString().trim();
        email = textInputdriverEmail.getEditText().getText().toString().trim();
        phoneno = "+92" +textInputdriverPhoneno.getEditText().getText().toString().trim();

        validateFirstname(firstname);
        validateLastname(lastname);
        validateEmail(email);
        validatePhoneno(phoneno);

        if (textInputdriverFirstname.getError() != null
                || textInputdriverLastname.getError() != null
                || textInputdriverEmail.getError() != null
                || textInputdriverPhoneno.getError() != null) {
            // There are errors in the fields, so don't proceed
            return;
        }

        progressDialog.setMessage("Checking User Details...");
        progressDialog.show();


        //++++++++++++++++++++++++++++++++Checking phone no is not register + email ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://10.0.2.2/Cargo_Go/v1/alreadyexistsdriver.php", // Update the URL to include both phone number and email check
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("message")) {
                                String message = jsonObject.getString("message");
                                if (message.equals("Phone number is already registered, please choose a different one.")) {
                                    textInputdriverPhoneno.setError("Phone number is already registered");
                                } else if (message.equals("Email is already registered, please choose a different one.")) {
                                    textInputdriverEmail.setError("Email is already registered");
                                } else {
                                    otpsend();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            // Handle connection error here
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Unable to connect to the server. Please check your internet connection.", Toast.LENGTH_LONG).show();
                        } else {
                            // Handle other errors
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Driver_Phone_No", phoneno);
                params.put("Driver_Email", email);
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    //++++++++++++++++++++++++++++++++Signup OTP SENDING++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void otpsend() {
        progressDialog.setMessage("Sending OTP..."); // Set the message for the progress dialog
        progressDialog.show(); // Show the progress dialog
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                //++++++++++++++++++++++++++++++++BUNDLE AND NAVIGATION TO OTP SCREEN++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                progressDialog.dismiss();
                OTP_Fragment otp = new OTP_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("Role", "Driver");
                bundle.putString("f_name", firstname);
                bundle.putString("l_name", lastname);
                bundle.putString("email", email);
                bundle.putString("phone", phoneno);

                bundle.putString("verificationid", verificationId);
                otp.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.login_RegFragmentContainer, otp);
                transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
                transaction.commit();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+92"+textInputdriverPhoneno.getEditText().getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // (optional) Activity for callback binding
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    //+++++++++++++++++++++++++++++++After OTP VERIFICATION DATA IS SEND TO DB+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void senddatatodatabase() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.person_2);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream;
        byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            final String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    "http://10.0.2.2/Cargo_Go/v1/registerDriver.php",
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Handle the response here
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                dialog.show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                                // Handle connection error here
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Unable to connect to the server. Please check your internet connection.", Toast.LENGTH_LONG).show();
                            } else {
                                // Handle other errors
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Driver_First_Name", firstname);
                    params.put("Driver_Last_Name", lastname);
                    params.put("Driver_Email", email);
                    params.put("Driver_Phone_No", phoneno);
                    params.put("Driver_Profile_Image", base64Image);
                    params.put("is_Active", "True");
                    return params;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
    }




    //image
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == 1 && data != null) {

        Uri imageUri = data.getData();
        imageView.setImageURI(imageUri);


        // Use Glide to load and crop the image into a circular shape
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CircleCrop());

        Glide.with(this)
                .load(imageUri)
                .apply(requestOptions)
                .into(imageView);
//        }
    }
}