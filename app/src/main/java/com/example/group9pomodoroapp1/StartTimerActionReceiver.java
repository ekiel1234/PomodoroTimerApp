package com.example.group9pomodoroapp1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.StartTimerUtils;
import com.example.group9pomodoroapp1.utils.Utils;

public class StartTimerActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        SharedPreferences preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        String action = intent.getStringExtra(Constants.INTENT_NAME_ACTION);
        if (action == null) return;

        long duration;

        switch (action) {
            case Constants.ACTION_START_TIMER:
                duration = Utils.getCurrentDurationPreferenceOf(preferences, Constants.WORK_SESSION);
                break;
            case Constants.ACTION_START_SHORT_BREAK:
                duration = Utils.getCurrentDurationPreferenceOf(preferences, Constants.SHORT_BREAK);
                break;
            case Constants.ACTION_START_LONG_BREAK:
                duration = Utils.getCurrentDurationPreferenceOf(preferences, Constants.LONG_BREAK);
                break;
            default:
                return;
        }

        StartTimerUtils.startTimer(duration, context);
    }
}
