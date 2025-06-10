package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.group9pomodoroapp1.MainActivity;
import com.example.group9pomodoroapp1.CountDownTimerService;

public class StopTimerUtils {

    public static void sessionComplete(Context context) {
        SharedPreferences preferences = context..getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int currentSessionType = Constants.WORK_SESSION;

        if (currentSessionType == Constants.WORK_SESSION) {
            int newCount = Utils.updateWorkSessionCount(preferences, context);
            currentSessionType = Utils.getTypeOfBreak(preferences, context);
            Utils.updateCurrentSessionType(preferences, context, currentSessionType);
        }

        stopTimer(context);
        sendStopBroadcast(context);
    }

    public static void sessionCancel(Context context, SharedPreferences preferences) {
        stopTimer(context);
        sendStopBroadcast(context);
    }

    private static void stopTimer(Context context) {
        context.stopService(new Intent(context, CountDownTimerService.class));
    }

    private static void sendStopBroadcast(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.STOP_ACTION_BROADCAST));
    }
}