package com.example.group9pomodoroapp1.utils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.group9pomodoroapp1.R;
import com.example.group9pomodoroapp1.adapter.TaskAdapter;
import com.example.group9pomodoroapp1.model.Task;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static SoundPool soundPool;
    public static int tickID, ringID;

    public static void prepareSoundPool(Context context) {
        try {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .build();

            tickID = soundPool.load(context, R.raw.clockticking, 1);
            ringID = soundPool.load(context, R.raw.bellringing, 1);
        } catch (Exception e) {
            Log.e("Utils", "Error loading sounds: " + e.getMessage());
        }
    }



    public static long getCurrentDurationPreferenceOf(SharedPreferences preferences, int sessionType) {
        switch (sessionType) {
            case Constants.WORK_SESSION:
                int workIndex = preferences.getInt(Constants.WORK_DURATION_KEY, 0);
                long[] workDurations = {5000L, 20 * 60_000L, 25 * 60_000L, 30 * 60_000L, 40 * 60_000L, 55 * 60_000L};
                return workDurations[workIndex];

            case Constants.SHORT_BREAK:
                int shortIndex = preferences.getInt(Constants.SHORT_BREAK_DURATION_KEY, 0);
                long[] shortDurations = {5000L, 3 * 60_000L, 5 * 60_000L, 10 * 60_000L, 15 * 60_000L};
                return shortDurations[shortIndex];

            case Constants.LONG_BREAK:
                int longIndex = preferences.getInt(Constants.LONG_BREAK_DURATION_KEY, 0);
                long[] longDurations = {5000L, 10 * 60_000L, 15 * 60_000L, 20 * 60_000L, 25 * 60_000L};
                return longDurations[longIndex];

            default:
                return 0;
        }
    }



    public static String formatDuration(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }

    public static long convertTimeToMillis(String formattedTime) {
        try {
            String[] parts = formattedTime.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (long) (minutes * 60 + seconds) * 1000L;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int retrieveCurrentSessionType(SharedPreferences preferences) {
        return preferences.getInt("currentSessionType", Constants.WORK_SESSION);
    }

    public static void updateCurrentSessionType(SharedPreferences preferences, int type) {
        preferences.edit().putInt("currentSessionType", type).apply();
    }

    public static int updateWorkSessionCount(SharedPreferences preferences) {
        int count = preferences.getInt(Constants.TASK_PROGRESS_COUNT_KEY, 0) + 1;
        preferences.edit().putInt(Constants.TASK_PROGRESS_COUNT_KEY, count).apply();
        return count;
    }

    public static int getTypeOfBreak(SharedPreferences preferences) {
        int completedSessions = preferences.getInt("completed_sessions", 0);
        int threshold = preferences.getInt(Constants.LONG_BREAK_AFTER_KEY, 2);
        return (completedSessions > 0 && completedSessions % threshold == 0)
                ? Constants.LONG_BREAK
                : Constants.SHORT_BREAK;
    }


    public static int getLongBreakAfterPreference(SharedPreferences prefs) {
        return prefs.getInt(Constants.LONG_BREAK_AFTER_KEY, 2); // default to 2
    }


    public static void handleDistraction(SharedPreferences preferences, Context context, List<Task> taskList, TaskAdapter taskAdapter) {
        int count = preferences.getInt("distractionCount", 0) + 1;
        preferences.edit().putInt("distractionCount", count).apply();
        Toast.makeText(context, "Stay focused! You're doing great!", Toast.LENGTH_SHORT).show();

        if (!taskList.isEmpty()) {
            Task currentTask = taskList.get(0);
            currentTask.setDistractionCount(currentTask.getDistractionCount() + 1);
            taskAdapter.notifyItemChanged(0);
        }
    }

    public static void showAddTaskDialog(Context context, List<Task> taskList, TaskAdapter taskAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Task");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText nameInput = new EditText(context);
        nameInput.setHint("Task Name");
        layout.addView(nameInput);

        EditText estimateInput = new EditText(context);
        estimateInput.setHint("Estimated Pomodoros");
        estimateInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(estimateInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskName = nameInput.getText().toString().trim();
            String estimateStr = estimateInput.getText().toString().trim();

            if (!taskName.isEmpty() && !estimateStr.isEmpty()) {
                try {
                    int estimatedPomodoros = Integer.parseInt(estimateStr);
                    Task newTask = new Task(taskName, estimatedPomodoros);
                    taskList.add(newTask);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);

                    TaskStorageUtils.saveTasks(context, taskList);
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ✅ Foreground notification for CountDownTimerService
    public static Notification createNotification(Context context, String title, String content) {
        String channelId = "pomodoro_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Pomodoro Timer",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows Pomodoro timer countdown");
            notificationManager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build();
    }
}
