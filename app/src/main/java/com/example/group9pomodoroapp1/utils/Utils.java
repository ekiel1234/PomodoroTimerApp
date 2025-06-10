package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.example.group9pomodoroapp1.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static SoundPool soundPool;
    public static int tickID, ringID;

    public static void prepareSoundPool(Context context) {
        try {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .build();

            tickID = soundPool.load(context, R.raw.clockticking, 1);
            ringID = soundPool.load(context, R.raw.bellringing, 1);
        } catch (Exception e) {
            Log.e("Utils", "Error loading sounds: " + e.getMessage());
        }
    }

    public static long getCurrentDurationPreferenceOf(SharedPreferences preferences, Context context, int sessionType) {
        switch (sessionType) {
            case Constants.WORK_SESSION:
                int workIndex = preferences.getInt(context.getString(R.string.work_duration_key), 1);
                int[] workDurations = {20, 25, 30, 40, 55};
                return workDurations[workIndex] * 60000L;

            case Constants.SHORT_BREAK:
                int shortIndex = preferences.getInt(context.getString(R.string.short_break_duration_key), 1);
                int[] shortDurations = {3, 5, 10, 15};
                return shortDurations[shortIndex] * 60000L;

            case Constants.LONG_BREAK:
                int longIndex = preferences.getInt(context.getString(R.string.long_break_duration_key), 1);
                int[] longDurations = {10, 15, 20, 25};
                return longDurations[longIndex] * 60000L;

            default:
                return 0;
        }
    }

    public static String formatDuration(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }
}