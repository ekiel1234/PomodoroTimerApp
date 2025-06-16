package com.example.group9pomodoroapp1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;


import androidx.core.app.NotificationCompat;

import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.Utils;

public class CountDownTimerService extends Service {

    public static CountDownTimer countDownTimer;
    public static long remainingTime = 0;
    private static final String CHANNEL_ID = "PomodoroChannel";
    private boolean userStopped = false;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, getNotification("Starting..."));

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case Constants.ACTION_START:
                    userStopped = false;
                    long duration = intent.getLongExtra("duration", 0);
                    startTimer(duration);
                    break;

                case Constants.ACTION_RESUME:
                    userStopped = false;
                    resumeTimer();
                    break;

                case Constants.ACTION_PAUSE:
                    pauseTimer();
                    break;

                case Constants.ACTION_STOP:
                    stopTimer();
                    break;
            }
        }

        return START_STICKY;
    }

    private void startTimer(long millis) {
        remainingTime = millis;

        countDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                sendCountdownBroadcast(remainingTime);
                updateNotification(Utils.formatDuration(remainingTime));
            }

            public void onFinish() {
                if (userStopped) return;  // ✅ prevent any restart or alarm when user stops

                remainingTime = 0;
                playAlarmOnce();
                sendCompletionBroadcast();  // Triggers popup
                stopForeground(true);
                stopSelf();
            }
        }.start();

        sendStartBroadcast();
    }

    private void resumeTimer() {
        startTimer(remainingTime);
    }

    private void pauseTimer() {
        userStopped = true; // ✅ prevent looping
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            sendStopBroadcast();
        }
    }

    private void stopTimer() {
        userStopped = true; // ✅ prevent onFinish from restarting timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        remainingTime = 0;
        sendStopBroadcast();
        stopAlarm();
        stopForeground(true);
        stopSelf();
    }

    private void playAlarmOnce() {
        stopAlarm();
        mediaPlayer = MediaPlayer.create(this, R.raw.bellringing);
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> stopAlarm());
            mediaPlayer.start();
        }
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void sendCountdownBroadcast(long millisUntilFinished) {
        Intent broadcastIntent = new Intent(Constants.COUNTDOWN_BROADCAST);
        broadcastIntent.putExtra("countDown", Utils.formatDuration(millisUntilFinished));
        sendBroadcast(broadcastIntent);
    }

    private void sendCompletionBroadcast() {
        Intent intent = new Intent(Constants.COMPLETE_ACTION_BROADCAST);
        sendBroadcast(intent);
    }

    private void sendStopBroadcast() {
        Intent intent = new Intent(Constants.STOP_ACTION_BROADCAST);
        sendBroadcast(intent);
    }

    private void sendStartBroadcast() {
        Intent intent = new Intent(Constants.START_ACTION_BROADCAST);
        sendBroadcast(intent);
    }

    private Notification getNotification(String contentText) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pomodoro Timer")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(String contentText) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(1, getNotification(contentText));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pomodoro Timer Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Pomodoro session active");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
