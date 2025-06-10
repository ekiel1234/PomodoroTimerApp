package com.example.group9pomodoroapp1;

import static com.example.group9pomodoroapp1.utils.Constants.ACTION_CANCEL_TIMER;
import static com.example.group9pomodoroapp1.utils.Constants.ACTION_COMPLETE_TIMER;
import static com.example.group9pomodoroapp1.utils.Constants.INTENT_NAME_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.group9pomodoroapp1.utils.StopTimerUtils;

public class StopTimerActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String action = intent.getStringExtra(INTENT_NAME_ACTION);

        switch (action) {
            case ACTION_COMPLETE_TIMER:
                StopTimerUtils.sessionComplete(context);
                break;
            case ACTION_CANCEL_TIMER:
                StopTimerUtils.sessionCancel(context, preferences);
                break;
        }
    }
}