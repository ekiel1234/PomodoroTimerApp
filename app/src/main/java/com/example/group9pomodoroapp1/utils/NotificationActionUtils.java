package com.example.group9pomodoroapp1.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.example.group9pomodoroapp1.StartTimerActionReceiver;
import com.example.group9pomodoroapp1.R;

import static com.example.group9pomodoroapp1.utils.Constants.*;

public class NotificationActionUtils {

    public static NotificationCompat.Action getIntervalAction(int currentType, Context context) {
        switch (currentType) {
            case WORK_SESSION:
                return new NotificationCompat.Action(
                        R.drawable.play,
                        "Start Work",
                        getPendingIntent(WORK_SESSION, ACTION_START_TIMER, context));
            case SHORT_BREAK:
                return new NotificationCompat.Action(
                        R.drawable.short_break,
                        "Start Short Break",
                        getPendingIntent(SHORT_BREAK, ACTION_START_SHORT_BREAK, context));
            case LONG_BREAK:
                return new NotificationCompat.Action(
                        R.drawable.long_break,
                        "Start Long Break",
                        getPendingIntent(LONG_BREAK, ACTION_START_LONG_BREAK, context));
            default:
                return null;
        }
    }

    private static PendingIntent getPendingIntent(int requestCode, String action, Context context) {
        Intent intent = new Intent(context, StartTimerActionReceiver.class)
                .putExtra(INTENT_NAME_ACTION, action);
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}