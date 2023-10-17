package com.shahab12344.loader_system;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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

public class signup_customer_Fragment extends Fragment {

    //++++++++++++++++++++++++++++++++Variables Signup++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    Button btn_register, btn_go_login;
    ImageView back_to_login;
    String firstname;
    private SessionManager sessionManager;
    String lastname;
    String email;
    String phoneno, status;
    String Role, returnrole;

    private Dialog dialog;
    //++++++++++++++++++++++++++++++++Firebase variables++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextInputLayout textInputFirstname, textInputLastname, textInputEmail, textInputPhoneno;
    private ProgressDialog progressDialog;

    public signup_customer_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_customer_, container, false);

        //++++++++++++++++++++++++++++++++Getting ids++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        textInputFirstname = view.findViewById(R.id.reg_name);
        textInputLastname = view.findViewById(R.id.lastname);
        textInputEmail = view.findViewById(R.id.reg_email);
        textInputPhoneno = view.findViewById(R.id.reg_phoneNo);


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
        }


        sessionManager = new SessionManager(getContext());

        //++++++++++++++++++++++++++++++++FOR OTP IS VERIFIED OR NOT++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        if (status == "true") {
            textInputFirstname.getEditText().setText(firstname);
            textInputLastname.getEditText().setText(lastname);
            textInputEmail.getEditText().setText(email);
            textInputPhoneno.getEditText().setText(phoneno);

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

        //++++++++++++++++++++++++++++++++Button for register new user++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        btn_register = view.findViewById(R.id.reg_btn);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Role == null) {
                    Toast.makeText(getContext(), "Select Role Customer/Driver", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser();
                }
            }
        });

        //++++++++++++++++++++++++++++++++Button to go to Login Screen++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        btn_go_login = view.findViewById(R.id.reg_login_btn);
        btn_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment login_page = new Login_customers();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, login_page).commit();
            }
        });


        //++++++++++++++++++++++++++++++++Using back button to login++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        back_to_login = view.findViewById(R.id.signup_back);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Login_customers();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });


        //customer or driver
        CardView customerCardView = view.findViewById(R.id.customerCardView);
        CardView driverCardView = view.findViewById(R.id.driverCardView);

        customerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerCardView.setSelected(true);
                driverCardView.setSelected(false);
                Role = "Customer";
            }
        });

        driverCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerCardView.setSelected(false);
                driverCardView.setSelected(true);
                Role = "Driver";
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Signup_driverFragment driver_signup = new Signup_driverFragment();
                transaction.replace(R.id.login_RegFragmentContainer, driver_signup);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }


    //+++++++++++++++++++++++++++++++++validation of input fields+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void validation() {

        textInputFirstname.getEditText().addTextChangedListener(new TextWatcher() {
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

        textInputLastname.getEditText().addTextChangedListener(new TextWatcher() {
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

        textInputEmail.getEditText().addTextChangedListener(new TextWatcher() {
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

        textInputPhoneno.getEditText().addTextChangedListener(new TextWatcher() {
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

        textInputFirstname.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateFirstname(textInputFirstname.getEditText().getText().toString().trim());
            }
        });

        textInputLastname.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateLastname(textInputLastname.getEditText().getText().toString().trim());
            }
        });

        textInputEmail.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmail(textInputEmail.getEditText().getText().toString().trim());
            }
        });

        textInputPhoneno.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePhoneno("+92"+textInputPhoneno.getEditText().getText().toString().trim());
            }
        });
    }

    //++++++++++++++++++++++++++++++++++ Helper method to check if a string contains only alphabetic characters
    private boolean isAlphaString(String input) {
        return input.matches("[a-zA-Z]+");
    }

    private void validateFirstname(String firstname) {
        textInputFirstname.setError(null);

        if (firstname.isEmpty()) {
            textInputFirstname.setError("First name is required");
        } else if (!isAlphaString(firstname)) {
            textInputFirstname.setError("First name should only contain alphabetic characters");
        }
    }

    private void validateLastname(String lastname) {
        textInputLastname.setError(null);

        if (lastname.isEmpty()) {
            textInputLastname.setError("Last name is required");
        } else if (!isAlphaString(lastname)) {
            textInputLastname.setError("Last name should only contain alphabetic characters");
        }
    }

    private void validateEmail(String email) {
        textInputEmail.setError(null);

        if (email.isEmpty()) {
            textInputEmail.setError("Email is required");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.setError("Invalid email format");
        }
    }

    private void validatePhoneno(String phoneno) {
        textInputPhoneno.setError(null);

        if (phoneno.isEmpty()) {
            textInputPhoneno.setError("Phone number is required");
        } else if (!isValidPakistanPhoneNumber(phoneno)) {
            textInputPhoneno.setError("Invalid Phone No pattern");
        }
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
                bundle.putString("Role", Role);
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
                        .setPhoneNumber(textInputPhoneno.getEditText().getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // (optional) Activity for callback binding
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //++++++++++++++++++++++++++++++++Pakistani phone no validation++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private boolean isValidPakistanPhoneNumber(String phoneNumber) {
        String pakistanPhoneNumberPattern = "^(\\+92|0)[3]{1}[0-4]{1}[0-9]{8}$";
        Pattern pattern = Pattern.compile(pakistanPhoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    //++++++++++++++++++++++++++++++++For register a new user ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void registerUser() {
        firstname = textInputFirstname.getEditText().getText().toString().trim();
        lastname = textInputLastname.getEditText().getText().toString().trim();
        email = textInputEmail.getEditText().getText().toString().trim();
        phoneno = "+92" +textInputPhoneno.getEditText().getText().toString().trim();

        validateFirstname(firstname);
        validateLastname(lastname);
        validateEmail(email);
        validatePhoneno(phoneno);

        if (textInputFirstname.getError() != null
                || textInputLastname.getError() != null
                || textInputEmail.getError() != null
                || textInputPhoneno.getError() != null) {
            // There are errors in the fields, so don't proceed
            return;
        }

        progressDialog.setMessage("Checking User Details...");
        progressDialog.show();


        //++++++++++++++++++++++++++++++++Checking phone no is not register + email ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://10.0.2.2/Cargo_Go/v1/checkingphoneno.php", // Update the URL to include both phone number and email check
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("message")) {
                                String message = jsonObject.getString("message");
                                if (message.equals("Phone number is already registered, please choose a different one.")) {
                                    textInputPhoneno.setError("Phone number is already registered");
                                } else if (message.equals("Email is already registered, please choose a different one.")) {
                                    textInputEmail.setError("Email is already registered");
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
                params.put("Phone_No", phoneno);
                params.put("Email", email);
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
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
                    "http://10.0.2.2/Cargo_Go/v1/registerUser.php",
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
                    params.put("First_Name", firstname);
                    params.put("Last_Name", lastname);
                    params.put("Email", email);
                    params.put("Phone_No", phoneno);
                    params.put("Profile_Image", base64Image);
                    return params;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
    }
}
