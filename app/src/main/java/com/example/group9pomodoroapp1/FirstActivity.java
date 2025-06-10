package com.example.group9pomodoroapp1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {

    // Splash screen display duration in milliseconds
    private static final int SPLASH_DISPLAY_LENGTH = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Delay starting MainActivity to show splash screen
        new Handler().postDelayed(() -> {
            // Start MainActivity
            Intent mainIntent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(mainIntent);

            // Close this activity so it doesn't stay in the back stack
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}