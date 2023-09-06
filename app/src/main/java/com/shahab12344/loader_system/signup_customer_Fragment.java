package com.shahab12344.loader_system;

import android.app.ProgressDialog;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signup_customer_Fragment extends Fragment {

    Button btn_register, btn_go_login;
    ImageView back_to_login;
    String smsCode;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private String verificationId;

    private TextInputLayout textInputFirstname, textInputLastname, textInputEmail, textInputPhoneno;
    private ProgressDialog progressDialog;

    public signup_customer_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_customer_, container, false);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        textInputFirstname = view.findViewById(R.id.reg_name);
        textInputLastname = view.findViewById(R.id.lastname);
        textInputEmail = view.findViewById(R.id.reg_email);
        textInputPhoneno = view.findViewById(R.id.reg_phoneNo);

        progressDialog = new ProgressDialog(getContext());

        btn_register = view.findViewById(R.id.reg_btn);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
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

    private boolean isValidPakistanPhoneNumber(String phoneNumber) {
        String pakistanPhoneNumberPattern = "^(\\+92|0)[1-9]{1}[0-9]{9}$";
        Pattern pattern = Pattern.compile(pakistanPhoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private void registerUser() {
        final String firstname = textInputFirstname.getEditText().getText().toString().trim();
        final String lastname = textInputLastname.getEditText().getText().toString().trim();
        final String email = textInputEmail.getEditText().getText().toString().trim();
        final String phoneno = textInputPhoneno.getEditText().getText().toString().trim();

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

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_PHONENO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            try {
                                JSONObject jsonObject = new JSONObject(response);
//                                String message = jsonObject.getString("message");
//                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                                OTP_Fragment fragmentB = new OTP_Fragment();
                                Bundle bundle = new Bundle();
//                                bundle.putString("verificationId", verificationId); // Use the SMS code as the verificationId
                                bundle.putString("firstname", firstname);
                                bundle.putString("lastname", lastname);
                                bundle.putString("email", email);
                                bundle.putString("phone_no", phoneno);

                                fragmentB.setArguments(bundle);

                                // Replace the current fragment with the OTP fragment
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.login_RegFragmentContainer, fragmentB);
                                transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
                                transaction.commit();
                                Toast.makeText(getContext(), bundle.toString(), Toast.LENGTH_SHORT).show();
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

//    private void sendOTP(String phoneNumber, String firstname, String lastname, String email) {
//        progressDialog.setMessage("Sending OTP...");
//        progressDialog.show();
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,
//                60,
//                TimeUnit.SECONDS,
//                getActivity(),
//                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    @Override
//                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                        progressDialog.dismiss();
//
//
//                        Toast.makeText(getContext(), "smsCode", Toast.LENGTH_SHORT).show();
//                        OTP_Fragment fragmentB = new OTP_Fragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("verificationId", verificationId); // Use the SMS code as the verificationId
//                        bundle.putString("firstname", firstname);
//                        bundle.putString("lastname", lastname);
//                        bundle.putString("email", email);
//                        bundle.putString("phone_no", phoneNumber);
//
//                        fragmentB.setArguments(bundle);
//
//                        // Replace the current fragment with the OTP fragment
//                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                        transaction.replace(R.id.login_RegFragmentContainer, fragmentB);
//                        transaction.addToBackStack(null); // Optional, allows the user to navigate back to the previous fragment
//                        transaction.commit();
//                        Toast.makeText(getContext(), bundle.toString(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onVerificationFailed(@NonNull FirebaseException e) {
//                        progressDialog.hide();
//                        Toast.makeText(getContext(), "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                        super.onCodeSent(s, forceResendingToken);
//                        progressDialog.dismiss();
//                        verificationId = s;
//                    }
//                });
//    }

}
