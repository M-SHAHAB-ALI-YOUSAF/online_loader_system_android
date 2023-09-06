package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class OTP_Fragment extends Fragment {
    Button btn_otp_verify;
    TextView resent_otp;
    ImageView back_to_login;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;

    String f_name;
    String l_name;
    String email;
    String phone;
    private ProgressDialog progressDialog;

    // Firebase
    private FirebaseAuth mAuth;
    private String verificationId;

    public OTP_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_o_t_p_, container, false);

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getArguments();
        if (bundle != null) {
            f_name = bundle.getString("firstname");
            l_name = bundle.getString("lastname");
            email = bundle.getString("email");
            phone = bundle.getString("phone_no");
        }

        if(!phone.isEmpty()){
            sendVerificationCode(phone);
        }

        // Initialize the EditText fields
        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);

        // Set TextChangedListener for each EditText
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
                // Combine the OTP from all EditText fields
                String otp = otp1.getText().toString() +
                        otp2.getText().toString() +
                        otp3.getText().toString() +
                        otp4.getText().toString() +
                        otp5.getText().toString() +
                        otp6.getText().toString();

                if (otp.length() == 6) {
                    verifyCode(otp);
                } else {
                    Toast.makeText(getContext(), "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back to login
        back_to_login = view.findViewById(R.id.otp_back);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Login_customers();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });

        resent_otp = view.findViewById(R.id.resent_otp);
        resent_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Resend OTP logic goes here
                Toast.makeText(getContext(), "Resending OTP...", Toast.LENGTH_SHORT).show();
                sendVerificationCode(phone);
            }
        });

        return view;
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
                // If more than one character is entered, keep only the first character
                editable.clear();
                editable.append(text.charAt(0));
            }

            // Move focus to the next EditText field if available
            if (text.length() == 1 && nextView != null) {
                nextView.requestFocus();
            } else if (text.length() == 0 && currentView != null) {
                // If the current EditText is empty, move focus to the previous EditText
                View previousView = getPreviousView(currentView);
                if (previousView != null) {
                    previousView.requestFocus();
                }
            }
        }

        // Helper method to get the previous EditText field
        private View getPreviousView(View currentView) {
            if (currentView == otp2) return otp1;
            if (currentView == otp3) return otp2;
            if (currentView == otp4) return otp3;
            if (currentView == otp5) return otp4;
            if (currentView == otp6) return otp5;
            return null; // If currentView is otp1 or an unknown view
        }
    }



    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(getActivity())
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otp1.setText(code.substring(0, 1));
                otp2.setText(code.substring(1, 2));
                otp3.setText(code.substring(2, 3));
                otp4.setText(code.substring(3, 4));
                otp5.setText(code.substring(4, 5));
                otp6.setText(code.substring(5, 6));
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Welcome", Toast.LENGTH_SHORT).show();
//                            progressDialog.setMessage("Registering user...");
//                            progressDialog.show();

                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    Constants.URL_REGISTER,
                                    new com.android.volley.Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
//                                            progressDialog.dismiss();

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
                                    params.put("firstname", f_name);
                                    params.put("lastname", l_name);
                                    params.put("email", email);
                                    params.put("phoneno", phone);
                                    params.put("profileimage", "null");
                                    return params;
                                }
                            };

                            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
