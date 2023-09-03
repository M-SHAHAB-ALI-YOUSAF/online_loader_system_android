package com.shahab12344.loader_system;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OTP_Fragment extends Fragment {
    Button btn_otp_verify;
    TextView resent_otp;
    ImageView back_to_login;
    public OTP_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_o_t_p_, container, false);

        btn_otp_verify = view.findViewById(R.id.btn_submit);
        btn_otp_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboard = new Intent(getContext(), Booking_Activity.class);
                startActivity(dashboard);

              }
        });


        //back to login

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
                Toast.makeText(getContext(), "OTP IS SEND", Toast.LENGTH_SHORT).show();
            }
        });
        return  view;
    }
}