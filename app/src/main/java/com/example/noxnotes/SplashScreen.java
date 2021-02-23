package com.example.noxnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import app.ROUTER;
import app.spref;

public class SplashScreen extends AppCompatActivity {

    ImageView image ;
    TextView text ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        image = findViewById(R.id.image);
        text = findViewById(R.id.text);

        YoYo.with(Techniques.BounceInDown).duration(2000).playOn(image);
        YoYo.with(Techniques.BounceInDown).duration(2000).playOn(text);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent = new Intent(SplashScreen.this ,
                        spref.getOurInstance().getString(ROUTER.SESSION , "") .equals("")?
                                LoginActivity.class:
                                MainActivity.class);
                startActivity(intent);


                finish();
            }
        } , 2500);
    }
}