package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
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

    //+++++++++++++++++++++variables++++++++++++++++++++++++++++++++++++++
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //+++++++++++++++++++++Full screen no top bar++++++++++++++++++++++++++++++++++++++
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


    //+++++++++++++++++++++Session for customer+++++++++++++++++++++++++++++++++++++++++
        sessionManager = new SessionManager(this);

    //+++++++++++++++++++++++++++hetting ids++++++++++++++++++++++++++++++++++++++++++++
        image = findViewById(R.id.logo_image);
        logo = findViewById(R.id.book_loader);
        slogan = findViewById(R.id.slogan);

    //+++++++++++++++++++++Animation+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

    //+++++++++++++++++++++setting animation to elements++++++++++++++++++++++++++++++++++++++
        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);


    //+++++++++++++++++++++Handle for going to login+++++++++++++++++++++++++++++++++++++++++++++
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

   //+++++++++++++++++++++if customer has logged in before and don't logout++++++++++++++++++++++++++++++++++++++
                if (sessionManager.isLoggedIn()) {
                    // User is logged in, navigate to the dashboard activity
                    Intent intent = new Intent(MainActivity.this, Booking_Activity.class);
                    startActivity(intent);
                }

     //+++++++++++++++++++++new customer or customer logout when last use++++++++++++++++++++++++++++++++++++++
                else {
                Intent intent = new Intent(MainActivity.this, Login_Registration.class);
                // Attach all the elements those you want to animate in design
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_text");
                //wrap the call in API level 21 or higher
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }
                finish();
            }
            }
        }, 3000);
    }



}
