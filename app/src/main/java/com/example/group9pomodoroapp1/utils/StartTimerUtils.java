package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.group9pomodoroapp1.CountDownTimerService;

public class StartTimerUtils {

    public static void startTimer(long duration, Context context) {
        Intent serviceIntent = new Intent(context, CountDownTimerService.class);
        serviceIntent.putExtra("time_period", duration);
        serviceIntent.putExtra("time_interval", Constants.TIME_INTERVAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

        sendStartBroadcast(context);
    }

    private static void sendStartBroadcast(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.START_ACTION_BROADCAST));
    }
}