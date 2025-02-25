package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;
    private ImageView image;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen, no top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //----------------------------------------------- Session for customer
        sessionManager = new SessionManager(this);

        //------------------------------------------------ Getting ids
        image = findViewById(R.id.logo_image);

        // Bitmap handling (consider using Glide or Picasso for more efficient image loading)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logofina, options);
        image.setImageBitmap(bitmap);


        // Handle for going to login
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isLoggedIn()) {
                    String role = sessionManager.getRole();
                    if ("Customer".equals(role)) {
                        Intent intent = new Intent(MainActivity.this, Booking_Activity.class);
                        startActivity(intent);
                    } else if ("Driver".equals(role)) {
                        Intent intent = new Intent(MainActivity.this, driver_homepage.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, Login_Registration.class);
                    startActivity(intent);
                }
            }
        }, SPLASH_DURATION);
    }
}
