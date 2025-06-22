package com.example.group9pomodoroapp1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.StopTimerUtils;

public class StopTimerActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String action = intent.getStringExtra(Constants.INTENT_NAME_ACTION);
        if (action == null) return;

        switch (action) {
            case Constants.ACTION_CANCEL_TIMER:
                StopTimerUtils.sessionCancel(context);
                break;
            case Constants.ACTION_COMPLETE_TIMER:
                StopTimerUtils.sessionComplete(context);
                break;
        }
    }
}
