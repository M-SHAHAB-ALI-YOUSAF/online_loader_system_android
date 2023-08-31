package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp_customer extends AppCompatActivity {

    //variables
    Button  go_to_login;
    private TextInputLayout textInputFirstname, textInputLastname, textInputUsername, textInputEmail, textInputPhoneno;
    private Button buttonRegister;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_customer);

        //getting ids
        buttonRegister = findViewById(R.id.reg_btn);
        go_to_login = findViewById(R.id.reg_login_btn);

        textInputFirstname = findViewById(R.id.reg_name);
        textInputLastname = findViewById(R.id.lastname);
        textInputUsername = findViewById(R.id.reg_username);
        textInputEmail = findViewById(R.id.reg_email);
        textInputPhoneno = findViewById(R.id.reg_phoneNo);


        progressDialog = new ProgressDialog(this);


        //button function register
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });


        //button function for go to login
        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent faq = new Intent(SignUp_customer.this, Login_customer.class);
                startActivity(faq);
            }
        });

    }

    //database
    private void registerUser() {
        final String firstname = textInputFirstname.getEditText().getText().toString().trim();
        final String lastname = textInputLastname.getEditText().getText().toString().trim();
        final String username = textInputUsername.getEditText().getText().toString().trim().toLowerCase();
        final String email = textInputEmail.getEditText().getText().toString().trim();
        final String phoneno = textInputPhoneno.getEditText().getText().toString().trim();

        // Reset any previous error messages
        textInputFirstname.setError(null);
        textInputLastname.setError(null);
        textInputUsername.setError(null);
        textInputEmail.setError(null);
        textInputPhoneno.setError(null);

        // Check if any field is empty and display error messages
        if (firstname.isEmpty()) {
            textInputFirstname.setError("First name is required");
            return;
        }
        if (lastname.isEmpty()) {
            textInputLastname.setError("Last name is required");
            return;
        }
        if (username.isEmpty()) {
            textInputUsername.setError("Username is required");
            return;
        }
        if (email.isEmpty()) {
            textInputEmail.setError("Email is required");
            return;
        }

        if (phoneno.isEmpty()) {
            textInputPhoneno.setError("Phone number is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.setError("Invalid email format");
            return;
        }

        // Check username length and display error message
        if (username.length() > 20) {
            textInputUsername.setError("Username must be under 20 characters");
            return;
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Intent dashboard = new Intent(SignUp_customer.this, Dashboard.class);
                            startActivity(dashboard);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("username", username);
                params.put("email", email);
                params.put("phoneno", phoneno);
                params.put("profileimage", "null");
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

}