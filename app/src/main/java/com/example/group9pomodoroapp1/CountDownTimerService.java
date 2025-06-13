package com.example.group9pomodoroapp1;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.Utils;

public class CountDownTimerService extends Service {

    private CountDownTimer countDownTimer;
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long duration = intent.getLongExtra("time_period",
                Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.WORK_SESSION));

        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Intent tickIntent = new Intent(Constants.COUNTDOWN_BROADCAST);
                tickIntent.putExtra("countDown", Utils.formatDuration(millisUntilFinished));
                LocalBroadcastManager.getInstance(CountDownTimerService.this).sendBroadcast(tickIntent);
            }

            @Override
            public void onFinish() {
                Intent finishIntent = new Intent(Constants.COMPLETE_ACTION_BROADCAST);
                LocalBroadcastManager.getInstance(CountDownTimerService.this).sendBroadcast(finishIntent);

                if (Utils.soundPool != null) {
                    Utils.soundPool.play(Utils.ringID, 1, 1, 0, 0, 1);
                }
            }
        }.start();

        return START_STICKY;
    }
}
