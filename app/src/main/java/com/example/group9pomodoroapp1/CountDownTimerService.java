package com.example.group9pomodoroapp1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.group9pomodoroapp1.utils.Utils;
import com.example.group9pomodoroapp1.utils.Constants;

public class CountDownTimerService extends Service {

    public static final int ID = 1;
    LocalBroadcastManager broadcaster;
    private CountDownTimer countDownTimer;
    private SharedPreferences preferences;
    private int sessionCompletedCount;
    private int currentSessionType;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long duration = Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.WORK_SESSION);

        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Intent intent = new Intent(Constants.COUNTDOWN_BROADCAST);
                intent.putExtra("countDown", formatDuration(millisUntilFinished));
                LocalBroadcastManager.getInstance(CountDownTimerService.this).sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(Constants.STOP_ACTION_BROADCAST);
                LocalBroadcastManager.getInstance(CountDownTimerService.this).sendBroadcast(intent);
            }
        }.start();

        return START_STICKY;
    }

    private String formatDuration(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }
}