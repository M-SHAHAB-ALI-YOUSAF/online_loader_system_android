package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class NewPhoneOTP extends Fragment {
    Button btn_otp_verify;
    TextView resent_otp, countdownText;
    ImageView back_to_login;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private ProgressDialog progressDialog;
    private CountDownTimer resendOtpTimer;
    private boolean isResendOtpEnabled = true;
    String  url, coulumnemail, coulumnphonenumber;

    private FirebaseAuth mAuth;
    private String verificationId, change_contact, newVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private SessionManager sessionManager;


    public NewPhoneOTP() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_new_phone_o_t_p, container, false);

        //+++++++++++++++++++++++++++++firebase session++++++++++++++++++++++++++
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getContext());

        //+++++++++++++++++++++++++++++++++++++Bundle+++++++++++++++++++++++++++++++++
        Bundle bundle = getArguments();
        if (bundle != null) {

            verificationId = bundle.getString("verificationid");
            change_contact = bundle.getString("phone");

        }


        //+++++++++++++++++++++++++++++OTP BOXES+++++++++++++++++++++++++++++++++++++++++++++++
        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);

        otp1.addTextChangedListener(new NewPhoneOTP.GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new NewPhoneOTP.GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new NewPhoneOTP.GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new NewPhoneOTP.GenericTextWatcher(otp4, otp5));
        otp5.addTextChangedListener(new NewPhoneOTP.GenericTextWatcher(otp5, otp6));
        otp6.addTextChangedListener(new NewPhoneOTP.GenericTextWatcher(otp6, null));

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

                        if (!verificationId.isEmpty()) {
                            OTP_Verify(code, false, newVerificationId);
                        } else {
                            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
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
                Fragment fragment = new Chnage_Phone_No();
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
                    resend_otp(change_contact);
            }
        });
        return view;
    }


    //++++++++++++++++++++++++++uodate resend otp button+++++++++++++++++++++++++++++++++++++++
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


    //+++++verify++++++++++++++++++++++++++++++++++++++++++++++++++++
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
                    UpdateNumber();
                } else {
                    Toast.makeText(getContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //------------------------------------update number method------------------------------------------
    private void UpdateNumber() {
        if ("Customer".equals(sessionManager.getRole())){
            url = "http://10.0.2.2/Cargo_Go/v1/updateCustomerPhoneNo.php";
            coulumnemail = "Email";
            coulumnphonenumber = "Phone_No";
        }
        else{
            url = "http://10.0.2.2/Cargo_Go/v1/updateDriverPhoneNo.php";
            coulumnemail = "Driver_Email";
            coulumnphonenumber = "Driver_Phone_No";
        }

        Map<String, String> params = new HashMap<>();
        params.put(coulumnphonenumber, change_contact);
        params.put(coulumnemail, sessionManager.getEmail());

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
                                sessionManager.updatePhoneNumber(change_contact);
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                if("Driver".equals(sessionManager.getRole())){
                                    Driver_Homepage_Fragment otp = new Driver_Homepage_Fragment();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.driver_fragment, otp);
                                    transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
                                    transaction.commit();

                                }
                                else if("Customer".equals(sessionManager.getRole())) {
                                    Dashbaord_Fragment otp = new Dashbaord_Fragment();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.bookingfragment, otp);
                                    transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
                                    transaction.commit();
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
}