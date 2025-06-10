package com.example.group9pomodoroapp1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.StartTimerUtils;
import com.example.group9pomodoroapp1.utils.Utils;

public class StartTimerActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra(Constants.INTENT_NAME_ACTION);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        switch (action) {
            case Constants.ACTION_START_TIMER:
                long workDuration = Utils.getCurrentDurationPreferenceOf(preferences, context, Constants.WORK_SESSION);
                StartTimerUtils.startTimer(workDuration, context);
                Log.d("TIMER was started with", String.valueOf(workDuration));
                break;

            case Constants.ACTION_START_SHORT_BREAK:
                long shortBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, context, Constants.SHORT_BREAK);
                StartTimerUtils.startTimer(shortBreakDuration, context);
                Log.d("SHRT_BRK started with", String.valueOf(shortBreakDuration));
                break;

            case Constants.ACTION_START_LONG_BREAK:
                long longBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, context, Constants.LONG_BREAK);
                StartTimerUtils.startTimer(longBreakDuration, context);
                Log.d("LONG_BRK started with", String.valueOf(longBreakDuration));
                break;
        }
    }
}