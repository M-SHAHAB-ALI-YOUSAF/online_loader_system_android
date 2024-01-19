package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTP_Fragment extends Fragment {

    Button btn_otp_verify;
    TextView resent_otp, countdownText;
    ImageView back_to_login;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    String f_name, l_name, email, signup_phone_no, source, Role, loginRole, newVerificationId;
    private ProgressDialog progressDialog;
    private CountDownTimer resendOtpTimer;
    private boolean isResendOtpEnabled = true;

    private FirebaseAuth mAuth;
    private String verificationId, login_contact;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private SessionManager sessionManager;

    public OTP_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_o_t_p_, container, false);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            source = bundle.getString("source");
            verificationId = bundle.getString("verificationid_login");
            login_contact = bundle.getString("phone_login");
            loginRole = bundle.getString("Rolefromlogin");

            f_name = bundle.getString("f_name");
            l_name = bundle.getString("l_name");
            email = bundle.getString("email");
            signup_phone_no = bundle.getString("phone");
            Role = bundle.getString("Role");
            if (!"login_customer".equals(source)) {
                verificationId = bundle.getString("verificationid");
            }
        }

        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);

        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, otp5));
        otp5.addTextChangedListener(new GenericTextWatcher(otp5, otp6));
        otp6.addTextChangedListener(new GenericTextWatcher(otp6, null));

        btn_otp_verify = view.findViewById(R.id.btn_submit);
        btn_otp_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = otp1.getText().toString() +
                        otp2.getText().toString() +
                        otp3.getText().toString() +
                        otp4.getText().toString() +
                        otp5.getText().toString() +
                        otp6.getText().toString();

                if (code.length() == 6) {
                    if ("login_customer".equals(source)) {
                        if (!verificationId.isEmpty()) {
                            OTP_Verify(code, false, newVerificationId);
                        } else {
                            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!verificationId.isEmpty()) {
                            OTP_Verify(code, false, newVerificationId);
                        } else {
                            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back_to_login = view.findViewById(R.id.otp_back);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Login_customers();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });

        resent_otp = view.findViewById(R.id.resent_otp);
        countdownText = view.findViewById(R.id.countdown_text);
        updateResendOtpButtonState();
        startResendOtpTimer();
        resent_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Resending OTP...", Toast.LENGTH_SHORT).show();
                if ("login_customer".equals(source)) {
                    resend_otp(login_contact);
                } else {
                    resend_otp(signup_phone_no);
                }
            }
        });

        return view;
    }

    private void updateResendOtpButtonState() {
        if (isResendOtpEnabled) {
            resent_otp.setVisibility(View.VISIBLE);
            countdownText.setVisibility(View.GONE);

            resent_otp.setEnabled(true);
            resent_otp.setTextColor(getResources().getColor(R.color.black));
        } else {
            resent_otp.setVisibility(View.GONE);
            countdownText.setVisibility(View.VISIBLE);
            countdownText.setText("Resend OTP in 0 seconds");

            countdownText.setEnabled(false);
        }
    }

    private void startResendOtpTimer() {
        countdownText.setVisibility(View.VISIBLE);
        resent_otp.setVisibility(View.GONE);
        resendOtpTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText("Resend OTP in " + millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                isResendOtpEnabled = true;
                updateResendOtpButtonState();
            }
        }.start();

        isResendOtpEnabled = false;
        updateResendOtpButtonState();
    }

    private void resend_otp(String phone_no) {
        progressDialog.setMessage("OTP is Resending...");
        progressDialog.show();
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
                progressDialog.dismiss();
                newVerificationId = verificationId;
                startResendOtpTimer();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone_no)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(getActivity())
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void OTP_Verify(String code, boolean isReverification, String newVerificationId) {
        PhoneAuthCredential credential;

        if (isReverification) {
            credential = PhoneAuthProvider.getCredential(newVerificationId, code);
        } else {
            credential = PhoneAuthProvider.getCredential(verificationId, code);
        }

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    resendOtpTimer.cancel();

                    if ("login_customer".equals(source)) {
                        if ("Customer".equals(loginRole)) {
//                            Log.d("MyApp", "getUserDataByPhone will be called");
//                            getUserDataByPhone(login_contact);
//                            Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), Booking_Activity.class);
                            startActivity(intent);
                        } else if ("Driver".equals(loginRole)) {
                            Intent driver = new Intent(getContext(), driver_homepage.class);
                            startActivity(driver);
                        }
                    } else {
                        if(Role == "Driver"){
                            Signup_driverFragment fragment = new Signup_driverFragment();
                            Bundle passdata = new Bundle();
                            passdata.putString("verfiy", "true");
                            passdata.putString("Roles", Role);
                            passdata.putString("f_name", f_name);
                            passdata.putString("l_name", l_name);
                            passdata.putString("email", email);
                            passdata.putString("phone", signup_phone_no);
                            fragment.setArguments(passdata);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.login_RegFragmentContainer, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                        else{
                        signup_customer_Fragment fragment = new signup_customer_Fragment();
                        Bundle passdata = new Bundle();
                        passdata.putString("verfiy", "true");
                        passdata.putString("Roles", Role);
                        passdata.putString("f_name", f_name);
                        passdata.putString("l_name", l_name);
                        passdata.putString("email", email);
                        passdata.putString("phone", signup_phone_no);
                        fragment.setArguments(passdata);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.login_RegFragmentContainer, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserDataByPhone(String phoneNumber) {
        // Perform a network request to fetch user data from your backend
        // Use your preferred networking library (e.g., Retrofit, Volley) to make the request

        // Example using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://10.0.2.2/Cargo_Go/v1/alluserbyphoneno.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("Customer_ID")) {
                                // Parse the JSON response
                                String customerId = jsonObject.getString("Customer_ID");
                                String firstName = jsonObject.getString("FIrst_Name");
                                String lastName = jsonObject.getString("Last_Name");
                                String email = jsonObject.getString("Email");
                                String phone = phoneNumber;
                                String profileImage = jsonObject.getString("Profile_Image");

                                // Save user data to SharedPreferences using SessionManager
                                SessionManager sessionManager = new SessionManager(getContext());
                                sessionManager.createUserSession(customerId, firstName, lastName, email, phone);
                                sessionManager.saveProfileImageUri(profileImage);
                                Log.d("MyApp", "Session created.");
                                Toast.makeText(getContext(), "Session created.", Toast.LENGTH_SHORT).show();

                                // Continue with your navigation logic...
                                Log.d("MyApp", "Navigating to Booking_Activity.");
                                // or
                                Toast.makeText(getContext(), "Navigating to Booking_Activity.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the case where the response doesn't contain user data
                              //  textInputPhonenologin.setError("Phone number is not registered with selected" + Role);
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
                params.put("Phone_No", login_contact);
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

    }


    private class GenericTextWatcher implements TextWatcher {
        private View currentView;
        private View nextView;

        private GenericTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            if (text.length() > 1) {
                editable.clear();
                editable.append(text.charAt(0));
            }

            if (text.length() == 1 && nextView != null) {
                nextView.requestFocus();
            } else if (text.length() == 0 && currentView != null) {
                View previousView = getPreviousView(currentView);
                if (previousView != null) {
                    previousView.requestFocus();
                }
            }
        }

        private View getPreviousView(View currentView) {
            if (currentView == otp2) return otp1;
            if (currentView == otp3) return otp2;
            if (currentView == otp4) return otp3;
            if (currentView == otp5) return otp4;
            if (currentView == otp6) return otp5;
            return null;
        }
    }
}
