package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.group9pomodoroapp1.CountDownTimerService;
import com.example.group9pomodoroapp1.model.Task;

import java.util.List;

public class StopTimerUtils {

    private static final String TAG = "StopTimerUtils";

    // Called when the timer finishes normally or user skips a session
    public static void sessionComplete(Context context) {
        int currentSessionType = Utils.retrieveCurrentSessionType(
                context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        );

        if (currentSessionType == Constants.WORK_SESSION) {
            Utils.updateWorkSessionCount(context.getSharedPreferences("prefs", Context.MODE_PRIVATE));

            List<Task> taskList = TaskStorageUtils.loadTasks(context);
            if (taskList != null && !taskList.isEmpty()) {
                Task task = taskList.get(0);
                task.incrementCompletedPomodoros();
                TaskStorageUtils.saveTasks(context, taskList);
            }

            int nextSession = Utils.getTypeOfBreak(context.getSharedPreferences("prefs", Context.MODE_PRIVATE));
            Utils.updateCurrentSessionType(context.getSharedPreferences("prefs", Context.MODE_PRIVATE), nextSession);
        } else {
            // Break ends → switch to Pomodoro
            Utils.updateCurrentSessionType(context.getSharedPreferences("prefs", Context.MODE_PRIVATE), Constants.WORK_SESSION);
        }

        stopTimer(context);
        sendStopBroadcast(context);
    }

    // Called when user presses "Pause" — no session switching, just retains time
    public static void pauseTimer(Context context) {
        try {
            Intent pauseIntent = new Intent(context, CountDownTimerService.class);
            pauseIntent.setAction(Constants.ACTION_PAUSE);
            context.startService(pauseIntent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send pause intent", e);
        }
    }

    // Called when user presses "Stop" — no session switching
    public static void sessionCancel(Context context) {
        stopTimer(context);
        sendStopBroadcast(context);
    }

    // Fully stops countdown timer
    public static void stopTimer(Context context) {
        try {
            Intent stopIntent = new Intent(context, CountDownTimerService.class);
            stopIntent.setAction(Constants.ACTION_STOP);
            context.startService(stopIntent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to stop timer", e);
        }
    }

    // Broadcast to reset UI (pause button, toggle, timer text, etc.)
    private static void sendStopBroadcast(Context context) {
        Intent intent = new Intent(Constants.STOP_ACTION_BROADCAST);
        context.sendBroadcast(intent);
    }
}
