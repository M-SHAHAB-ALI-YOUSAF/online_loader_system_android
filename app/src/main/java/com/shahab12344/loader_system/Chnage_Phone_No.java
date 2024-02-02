package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Chnage_Phone_No extends Fragment {
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextInputLayout textInputPhoneno;
    private SessionManager sessionManager;

    private ProgressDialog progressDialog;
    String phoneno, url, coulumnName;

    public Chnage_Phone_No() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chnage__phone__no, container, false);


        textInputPhoneno = view.findViewById(R.id.reg_new_phoneNo);

        //++++++++++++++++++++++++++++validate function+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        validation();

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        //session
        sessionManager = new SessionManager(getContext());

        //+++++++++++++++++++++++=Otp Button++++++++++++++++++++++++++++
        Button generateOtp= view.findViewById(R.id.buttonChangePhoneNumber);
        generateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), sessionManager.getRole(), Toast.LENGTH_SHORT).show();
                checkDetailandSendOTP();
            }
        });

        return view;
    }

    //++++++++++++++++++++++++++++++Checking Phone number+++++++++++++++++++++++++++++++++++++++++

    private void checkDetailandSendOTP() {

            phoneno = "+92" +textInputPhoneno.getEditText().getText().toString().trim();

            validatePhoneno(phoneno);

            if (textInputPhoneno.getError() != null) {
                // There are errors in the fields, so don't proceed
                return;
            }

            if("Driver".equals(sessionManager.getRole())) {
                url = "http://10.0.2.2/Cargo_Go/v1/DriverChangePhoneNumber.php";
                coulumnName = "Driver_Phone_No";
            }
            else{
                url = "http://10.0.2.2/Cargo_Go/v1/CustomerChangePhoneNumber.php";
                coulumnName = "Phone_No";
            }

            progressDialog.setMessage("Checking User Details...");
            progressDialog.show();


            //++++++++++++++++++++++++++++++++Checking phone no is not register + email ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url, // Update the URL to include both phone number and email check
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    if (message.equals("Phone number is already registered")) {
                                        textInputPhoneno.setError("Phone number is already registered");
                                    }  else {
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
                    params.put(coulumnName, phoneno);
                    return params;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
        }

        //+++++++++++++++++++++++++++++++++++++++++++++SendOTP+++++++++++++++++++++++++++++++++++++++++++
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
                   if("Driver".equals(sessionManager.getRole())) {
                       NewPhoneOTP otp = new NewPhoneOTP();
                       Bundle bundle = new Bundle();
                       bundle.putString("phone", phoneno);
                       bundle.putString("verificationid", verificationId);
                       otp.setArguments(bundle);
                       FragmentTransaction transaction = getFragmentManager().beginTransaction();
                       transaction.replace(R.id.driver_fragment, otp);
                       transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
                       transaction.commit();
                   }
                   else if ("Customer".equals(sessionManager.getRole())){
                       NewPhoneOTP otp = new NewPhoneOTP();
                       Bundle bundle = new Bundle();
                       bundle.putString("phone", phoneno);
                       bundle.putString("verificationid", verificationId);
                       otp.setArguments(bundle);
                       FragmentTransaction transaction = getFragmentManager().beginTransaction();
                       transaction.replace(R.id.bookingfragment, otp);
                       transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
                       transaction.commit();
                   }
                   else{
                       Toast.makeText(getContext(), "Something Wrong Try Again Later", Toast.LENGTH_LONG ).show();
                   }


                }
            };

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+92"+textInputPhoneno.getEditText().getText().toString())       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(getActivity())                 // (optional) Activity for callback binding
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }

     private void validation() {

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


        textInputPhoneno.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePhoneno("+92"+textInputPhoneno.getEditText().getText().toString().trim());
            }
        });
    }


    private void validatePhoneno(String phoneno) {
        textInputPhoneno.setError(null);

        if (phoneno.isEmpty()) {
            textInputPhoneno.setError("Phone number is required");
        } else if (!isValidPakistanPhoneNumber(phoneno)) {
            textInputPhoneno.setError("Invalid Phone No pattern");
        }
    }


    //++++++++++++++++++++++++++++++++Pakistani phone no validation++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private boolean isValidPakistanPhoneNumber(String phoneNumber) {
        String pakistanPhoneNumberPattern = "^(\\+92|0)[3]{1}[0-4]{1}[0-9]{8}$";
        Pattern pattern = Pattern.compile(pakistanPhoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

}