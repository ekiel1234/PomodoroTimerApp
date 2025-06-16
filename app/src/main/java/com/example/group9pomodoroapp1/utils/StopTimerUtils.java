package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.group9pomodoroapp1.CountDownTimerService;
import com.example.group9pomodoroapp1.model.Task;

import java.util.List;

public class StopTimerUtils {

    // Called when a session finishes naturally
    public static void sessionComplete(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        int currentSessionType = Utils.retrieveCurrentSessionType(preferences);
        if (currentSessionType == Constants.WORK_SESSION) {
            Utils.updateWorkSessionCount(preferences);

            // Update the first task's progress
            List<Task> taskList = TaskStorageUtils.loadTasks(context);
            if (taskList != null && !taskList.isEmpty()) {
                Task task = taskList.get(0);
                task.incrementCompletedPomodoros(); // Update progress
                TaskStorageUtils.saveTasks(context, taskList);
            }

            int nextSessionType = Utils.getTypeOfBreak(preferences);
            Utils.updateCurrentSessionType(preferences, nextSessionType);
        } else {
            Utils.updateCurrentSessionType(preferences, Constants.WORK_SESSION);
        }

        stopTimer(context);
        sendStopBroadcast(context);
    }

    // Called to pause the timer
    public static void pauseTimer(Context context) {
        try {
            context.stopService(new Intent(context, CountDownTimerService.class));
        } catch (Exception e) {
            e.printStackTrace(); // You can replace this with Log.e("StopTimerUtils", "Pause failed", e);
        }
    }

    // Called when the user cancels the timer manually
    public static void sessionCancel(Context context, SharedPreferences preferences) {
        stopTimer(context);
        sendStopBroadcast(context);
    }

    public static void stopTimer(Context context) {
        try {
            context.stopService(new Intent(context, CountDownTimerService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendStopBroadcast(Context context) {
        Intent intent = new Intent(Constants.STOP_ACTION_BROADCAST);
        context.sendBroadcast(intent); // Replaced deprecated LocalBroadcastManager
    }
}
