package com.shahab12344.loader_system;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;

public class Signup_driverFragment extends Fragment {
    Button btn_vehicle_info, btn_go_login;
    private ImageView imageView, selectimage;

    public Signup_driverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_driver, container, false);

        btn_vehicle_info = view.findViewById(R.id.user_info_btn);
        btn_vehicle_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new driver_vehicle_informationFragment();
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

        //image setting
        selectimage = view.findViewById(R.id.addphoto);
        imageView = view.findViewById(R.id.imageView);

        //image picker

        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.with(Signup_driverFragment.this)
                        //	//Final image resolution will be less than 1080 x 1080(Optional)
                        .crop()
                        .start();
                //openImagePicker();
            }
        });

        return view;
    }

    //image
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == 1 && data != null) {

        Uri imageUri = data.getData();
        imageView.setImageURI(imageUri);


        // Use Glide to load and crop the image into a circular shape
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CircleCrop());

        Glide.with(this)
                .load(imageUri)
                .apply(requestOptions)
                .into(imageView);
//        }
    }
}