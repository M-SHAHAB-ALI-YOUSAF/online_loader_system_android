package com.shahab12344.loader_system;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
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

public class signup_customer_Fragment extends Fragment {

    //++++++++++++++++++++++++++++++++Variables Signup++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    Button btn_register, btn_go_login;
    ImageView back_to_login;
    String firstname;
    String lastname;
    String email;
    String phoneno, status;

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

        //++++++++++++++++++++++++++++++++Bundle for data coming from otp++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getString("verfiy");
            firstname = bundle.getString("f_name");
            lastname = bundle.getString("l_name");
            email = bundle.getString("email");
            phoneno = bundle.getString("phone");
        }

        //++++++++++++++++++++++++++++++++FOR OTP IS VERIFIED OR NOT++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        if (status == "true") {
            textInputFirstname.getEditText().setText(firstname);
            textInputLastname.getEditText().setText(lastname);
            textInputEmail.getEditText().setText(email);
            textInputPhoneno.getEditText().setText(phoneno);

            senddatatodatabase();
        }

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());

        //++++++++++++++++++++++++++++++++Button for register new user++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        btn_register = view.findViewById(R.id.reg_btn);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        //++++++++++++++++++++++++++++++++Button to go to Login Screen++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        btn_go_login = view.findViewById(R.id.reg_login_btn);
        btn_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Login_customers();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
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


        return view;
    }


    //++++++++++++++++++++++++++++++++Signup OTP SENDING++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void otpsend() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                //++++++++++++++++++++++++++++++++BUNDLE AND NAVIGATION TO OTP SCREEN++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                OTP_Fragment otp = new OTP_Fragment();
                Bundle bundle = new Bundle();
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

    //++++++++++++++++++++++++++++++++Pakistani pphone no validation++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private boolean isValidPakistanPhoneNumber(String phoneNumber) {
        String pakistanPhoneNumberPattern = "^(\\+92|0)[1-9]{1}[0-9]{9}$";
        Pattern pattern = Pattern.compile(pakistanPhoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    //++++++++++++++++++++++++++++++++SFor register a new user ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void registerUser() {
        firstname = textInputFirstname.getEditText().getText().toString().trim();
        lastname = textInputLastname.getEditText().getText().toString().trim();
        email = textInputEmail.getEditText().getText().toString().trim();
        phoneno = textInputPhoneno.getEditText().getText().toString().trim();

        textInputFirstname.setError(null);
        textInputLastname.setError(null);
        textInputEmail.setError(null);
        textInputPhoneno.setError(null);

        if (firstname.isEmpty()) {
            textInputFirstname.setError("First name is required");
            return;
        } else if (lastname.isEmpty()) {
            textInputLastname.setError("Last name is required");
            return;
        } else if (email.isEmpty()) {
            textInputEmail.setError("Email is required");
            return;
        } else if (phoneno.isEmpty()) {
            textInputPhoneno.setError("Phone number is required");
            return;
        } else if (!isValidPakistanPhoneNumber(phoneno)) {
            textInputPhoneno.setError("Invalid Phone No pattern");
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.setError("Invalid email format");
            return;
        } else {
            progressDialog.setMessage("Checking Phone Number...");
            progressDialog.show();


            //++++++++++++++++++++++++++++++++Checking phone no is not register + email TODO++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_PHONENO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    if (message.equals("Phone number is already registered, please choose a different one.")) {
                                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
                            progressDialog.hide();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("phoneno", phoneno);
                    return params;
                }
            };

            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
    }

    //+++++++++++++++++++++++++++++++After OTP VERIFICATION DATA IS SEND TO DB+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void senddatatodatabase() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("email", email);
                params.put("phoneno", phoneno);
                params.put("profileimage", "null");
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

}
