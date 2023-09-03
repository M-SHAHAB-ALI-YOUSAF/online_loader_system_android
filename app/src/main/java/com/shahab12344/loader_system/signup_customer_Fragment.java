package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class signup_customer_Fragment extends Fragment {
    Button btn_register, btn_go_login;
    ImageView back_to_login;

    public signup_customer_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_customer_, container, false);

        btn_register = view.findViewById(R.id.reg_btn);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new OTP_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
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

        //back to login using button back
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
}