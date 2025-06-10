package com.example.group9pomodoroapp1;

import static com.example.group9pomodoroapp1.utils.Constants.NOTIFICATION_CHANNEL_ID;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class PomodoroApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createPomodoroNotificationChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPomodoroNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = Objects.requireNonNull(getSystemService(NotificationManager.class));
        manager.createNotificationChannel(channel);
    }
}
