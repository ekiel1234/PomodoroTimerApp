package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.example.group9pomodoroapp1.CountDownTimerService;

public class StartTimerUtils {

    public static void startTimer(long duration, Context context) {
        Intent intent = new Intent(context, CountDownTimerService.class);
        intent.setAction(Constants.ACTION_START);
        intent.putExtra("duration", duration); // ✅ match this with service expectation

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopCurrentTimer(Context context) {
        Intent stopIntent = new Intent(context, CountDownTimerService.class);
        stopIntent.setAction(Constants.ACTION_STOP); // ✅ allow stopping timer safely
        context.startService(stopIntent);
    }
}
