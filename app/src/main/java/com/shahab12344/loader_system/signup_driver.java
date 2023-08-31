package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;

public class signup_driver extends AppCompatActivity {
    Button register;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView, selectimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_driver);

        //getting ids
        register = findViewById(R.id.user_info_btn);
        selectimage = findViewById(R.id.addphoto);
        imageView = findViewById(R.id.imageView);

        //image picker

        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.with(signup_driver.this)
                        //	//Final image resolution will be less than 1080 x 1080(Optional)
                        .crop()
                        .start();
                //openImagePicker();
            }
        });


        //button function register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboard = new Intent(signup_driver.this, imagepicker.class);
                startActivity(dashboard);

            }
        });
    }


//    //showing image
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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