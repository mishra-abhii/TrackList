package com.abhishek.tracklist;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Thread thread = new Thread()
        {
            public void run ()
            {
                try {
                    sleep(1000);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent intent= new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } ;
        thread.start();

    }
}