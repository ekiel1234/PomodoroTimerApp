package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

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

            tickID = soundPool.load(context, com.example.group9pomodoroapp1.R.raw.clockticking, 1);
            ringID = soundPool.load(context, com.example.group9pomodoroapp1.R.raw.bellringing, 1);
        } catch (Exception e) {
            Log.e("Utils", "Error loading sounds: " + e.getMessage());
        }
    }

    public static long getCurrentDurationPreferenceOf(SharedPreferences preferences, Context context, int sessionType) {
        switch (sessionType) {
            case Constants.WORK_SESSION:
                int workIndex = preferences.getInt(Constants.WORK_DURATION_KEY, 1);
                long[] workDurations = {5_000L, 20 * 60_000L, 25 * 60_000L, 30 * 60_000L, 40 * 60_000L, 55 * 60_000L};
                return workDurations[workIndex];

            case Constants.SHORT_BREAK:
                int shortIndex = preferences.getInt(Constants.SHORT_BREAK_DURATION_KEY, 1);
                int[] shortDurations = {3, 5, 10, 15};
                return shortDurations[shortIndex] * 60000L;

            case Constants.LONG_BREAK:
                int longIndex = preferences.getInt(Constants.LONG_BREAK_DURATION_KEY, 1);
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

    public static int retrieveCurrentSessionType(SharedPreferences preferences, Context context) {
        return preferences.getInt("currentSessionType", Constants.WORK_SESSION);
    }

    public static void updateCurrentSessionType(SharedPreferences preferences, Context context, int type) {
        preferences.edit().putInt("currentSessionType", type).apply();
    }

    public static int updateWorkSessionCount(SharedPreferences preferences, Context context) {
        int count = preferences.getInt(Constants.TASK_PROGRESS_COUNT_KEY, 0) + 1;
        preferences.edit().putInt(Constants.TASK_PROGRESS_COUNT_KEY, count).apply();
        return count;
    }

    public static int getTypeOfBreak(SharedPreferences preferences, Context context) {
        int count = preferences.getInt(Constants.TASK_PROGRESS_COUNT_KEY, 0);
        int threshold = preferences.getInt(Constants.LONG_BREAK_AFTER_KEY, 2);
        return count > 0 && count % threshold == 0 ? Constants.LONG_BREAK : Constants.SHORT_BREAK;
    }
}
