package com.shahab12344.loader_system;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTP_Fragment extends Fragment {

    //++++++++++++++++++++++++++++++++++Variables++++++++++++++++++++++++++++++++++++++++++++
    Button btn_otp_verify;
    TextView resent_otp;
    ImageView back_to_login;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    String f_name;
    String l_name;
    String email;
    String phone;
    String source;
    private ProgressDialog progressDialog;

    //++++++++++++++++++++++++++++++++++Firebase Varaiables++++++++++++++++++++++++++++++++++++++++++++
    private FirebaseAuth mAuth;
    private String verificationId, verfication_login, login_contact;

    //++++++++++++++++++++++++++++++++++Session for customer++++++++++++++++++++++++++++++++++++++++++++

    private SessionManager sessionManager;


    public OTP_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_o_t_p_, container, false);

        //++++++++++++++++++++++++++++++++++Firebase and session++++++++++++++++++++++++++++++++++++++++++++
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getContext());

        //++++++++++++++++++++++++++++++++++values coming from login and signup++++++++++++++++++++++++++++++++++++++++++++
        Bundle bundle = getArguments();
        if (bundle != null) {
            //++++++++++++++++++++++++++++++++++Login data++++++++++++++++++++++++++++++++++++++++++++
            source = bundle.getString("source");
            verfication_login = bundle.getString("verificationid_login");
            login_contact = bundle.getString("phone_login");

            //++++++++++++++++++++++++++++++++++SignUp data++++++++++++++++++++++++++++++++++++++++++++
            f_name = bundle.getString("f_name");
            l_name = bundle.getString("l_name");
            email = bundle.getString("email");
            phone = bundle.getString("phone");
            verificationId = bundle.getString("verificationid");
        }

        //++++++++++++++++++++++++++++++++++Six box for otp++++++++++++++++++++++++++++++++++++++++++++
        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);

        //++++++++++++++++++++++++++++++++Set TextChangedListener for each EditText+++++++++++++++++++++++++++++++++++
        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, otp5));
        otp5.addTextChangedListener(new GenericTextWatcher(otp5, otp6));
        otp6.addTextChangedListener(new GenericTextWatcher(otp6, null));

        //++++++++++++++++++++++++++++++++Button to verify otp++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        btn_otp_verify = view.findViewById(R.id.btn_submit);
        btn_otp_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Combine the OTP from all EditText fields
                String code = otp1.getText().toString() +
                        otp2.getText().toString() +
                        otp3.getText().toString() +
                        otp4.getText().toString() +
                        otp5.getText().toString() +
                        otp6.getText().toString();

                if (code.length() == 6) {

                    //++++++++++++++++++++++++++++++++If customer coming from login screen+++++++++++++++++++++++++++++++++++
                    if ("login_customer".equals(source)) {
                        if (!verfication_login.isEmpty()) {
                            Login_otp_verification(code);
                        } else {
                            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //++++++++++++++++++++++++++++++++Coming from signup+++++++++++++++++++++++++++++++++++++++++++++++++++++
                    else {
                        if (!verificationId.isEmpty()) {
                            Signup_otp_verfiy(code);
                        } else {
                            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //++++++++++++++++++++++++++++++++Going back to login++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        back_to_login = view.findViewById(R.id.otp_back);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Login_customers();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });

        //++++++++++++++++++++++++++++++++Resend otp TODO++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        resent_otp = view.findViewById(R.id.resent_otp);
        resent_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Resend OTP logic goes here
                Toast.makeText(getContext(), "Resending OTP...", Toast.LENGTH_SHORT).show();
                // sendVerificationCode(phone);
            }
        });

        return view;
    }

    //++++++++++++++++++++++++++++++++Signup verification of otp++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void Signup_otp_verfiy(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    signup_customer_Fragment fragment = new signup_customer_Fragment();
                    Bundle passdata = new Bundle();
                    passdata.putString("verfiy", "true");
                    passdata.putString("f_name", f_name);
                    passdata.putString("l_name", l_name);
                    passdata.putString("email", email);
                    passdata.putString("phone", phone);
                    fragment.setArguments(passdata);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.login_RegFragmentContainer, fragment);
                    transaction.addToBackStack(null); // If you want to add the transaction to the back stack
                    transaction.commit();

                } else {
                    Toast.makeText(getContext(), "Not valid", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    //++++++++++++++++++++++++++++++++Login verification of otp++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void Login_otp_verification(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verfication_login, code);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(getContext(), "welcome", Toast.LENGTH_SHORT).show();
                    fetchUserDataFromDatabase(login_contact);

                } else {
                    Toast.makeText(getContext(), "Not valid", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    //++++++++++++++++++++++++++++++++Fetching logged in user data from DB to create a session++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void fetchUserDataFromDatabase(String login_contact) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_USER_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                // User data is successfully retrieved from the database
                                JSONObject userData = jsonObject.getJSONObject("users");

                                // Create a User object and store the fetched data
                                User_Data user = new User_Data();
                                user.setUserId(userData.getString("customer_id"));
                                user.setFirstName(userData.getString("firstname"));
                                user.setLastName(userData.getString("lastname"));
                                user.setEmail(userData.getString("email"));
                                user.setPhoneNumber(userData.getString("phoneno"));
                                user.setProfileImage(userData.getString("profileimage"));
                                Toast.makeText(getContext(), user.getEmail(), Toast.LENGTH_SHORT).show();

                                // Create a user session and store the user's data
                                sessionManager.createUserSession(
                                        userData.getString("customer_id"),
                                        userData.getString("firstname"),
                                        userData.getString("lastname"),
                                        userData.getString("email"),
                                        userData.getString("phoneno")
                                );
                                Intent intent = new Intent(getContext(), Booking_Activity.class);
                                startActivity(intent);
                            } else {
                                // Handle the case where user data retrieval from the database fails
                                Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phoneno", login_contact); // Send the phone number to the PHP script
                return params;
            }
        };

        // Add the request to the Volley request queue
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    //++++++++++++++++++++++++++++++++6 BOX functionality for otp++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

}
