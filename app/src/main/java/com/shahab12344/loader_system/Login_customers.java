package com.shahab12344.loader_system;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class Login_customers extends Fragment {

    //+++++++++++++++++++++++++Variables+++++++++++++++++++++++++++++++++++++++++++
    private TextInputLayout textInputPhonenologin;
    String phone, Role;
    private ProgressDialog progressDialog;
    Button btn_otp, btn_go_singup;

    //+++++++++++++++++++++++++Firebase variables+++++++++++++++++++++++++++++++++++++++++++
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;



    public Login_customers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_customers, container, false);

        //+++++++++++++++++++++++++getting id+++++++++++++++++++++++++++++++++++++++++++
        textInputPhonenologin = view.findViewById(R.id.login_phoneno);
        progressDialog = new ProgressDialog(getContext());

        //+++++++++++++++++++++++++Initialize Firebase Authentication+++++++++++++++++++++++++++++++++++++++++++
        mAuth = FirebaseAuth.getInstance();
        validation();

        //+++++++++++++++++++++++++button to send otp+++++++++++++++++++++++++++++++++++++++++++
        btn_otp = view.findViewById(R.id.get_otp);
        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendOtp();
            }
        });


        //+++++++++++++++++++++++++button for signup+++++++++++++++++++++++++++++++++++++++++++
        btn_go_singup = view.findViewById(R.id.reg_go_btn);
        btn_go_singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment signup_page = new signup_customer_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, signup_page).commit();
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

            }
        });


        return view;
    }

    //++++++++++++++++++++++++++++++=++++OTP SEND METHOD+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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

                //+++++++++++++++++++++++++Bundle for data send and naviation to otp screen+++++++++++++++++++++++++++++++++++++++++++
                progressDialog.dismiss();
                OTP_Fragment otp = new OTP_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("source", "login_customer");
                bundle.putString("phone_login", phone);
                bundle.putString("Rolefromlogin", Role);
                bundle.putString("verificationid_login", verificationId);
                otp.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.login_RegFragmentContainer, otp);
                transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
                transaction.commit();
            }
        };


        //+++++++++++++++++++++++++Firebase Send OTP function+++++++++++++++++++++++++++++++++++++++++++
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // (optional) Activity for callback binding
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //+++++++++++++++++++++++++Pakistani Phone no validation+++++++++++++++++++++++++++++++++++++++++++
    private boolean isValidPakistanPhoneNumber(String phoneNumber) {
        String pakistanPhoneNumberPattern = "^(\\+92|0)[1-9]{1}[0-9]{9}$";
        Pattern pattern = Pattern.compile(pakistanPhoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private void validation() {
        textInputPhonenologin.getEditText().addTextChangedListener(new TextWatcher() {
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
                validatePhoneno(s.toString().trim());
            }
        });

        textInputPhonenologin.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePhoneno(textInputPhonenologin.getEditText().getText().toString().trim());
            }
        });
    }


    private void validatePhoneno(String phoneno) {
        textInputPhonenologin.setError(null);
        if (phoneno.isEmpty()) {
            textInputPhonenologin.setError("Phone number is required");
        } else if (!isValidPakistanPhoneNumber(phoneno)) {
            textInputPhonenologin.setError("Invalid Phone No pattern");
        }
    }


    //++++++++++++++++++++++++++++++++++Validation of phone no++++++++++++++++++++++++++++++++++++++++++++
    private void SendOtp() {
        phone = textInputPhonenologin.getEditText().getText().toString().trim();
        validatePhoneno(phone);

        if (textInputPhonenologin.getError() != null ){
            // There are errors in the fields, so don't proceed
            return;
        }

        progressDialog.setMessage("Checking User Details...");
        progressDialog.show();

        //++++++++++++++++++++++++++++++++++Checking phone no is register or not++++++++++++++++++++++++++++++++++++++++++++
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://10.0.2.2/Cargo_Go/v1/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("message")) {
                                String message = jsonObject.getString("message");
                                if (message.equals("Phone number is already registered, please choose a different one.")) {
                                    otpsend();
                                } else {
                                    textInputPhonenologin.setError("Phone number is not registered with selected" + Role);
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
                            Toast.makeText(getContext(), "Unable to connect to the server. Please check your internet connection.", Toast.LENGTH_LONG).show();
                        } else {
                            // Handle other errors
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Phone_No", phone);
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

    }
}

