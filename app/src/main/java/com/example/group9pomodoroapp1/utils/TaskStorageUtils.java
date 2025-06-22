package com.example.group9pomodoroapp1.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.group9pomodoroapp1.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TaskStorageUtils {
    private static final String PREF_NAME = "prefs";
    private static final String TASK_LIST_KEY = "TASK_LIST";
    private static final String TASK_HISTORY_KEY = "TASK_HISTORY_LIST";

    // Save current tasks
    public static void saveTasks(Context context, List<Task> taskList) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(taskList);
        editor.putString(TASK_LIST_KEY, json);
        editor.apply();
    }

    // Load current tasks
    public static List<Task> loadTasks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(TASK_LIST_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    // ✅ Add task to history list
    public static void addTaskToHistory(Context context, Task task) {
        List<Task> history = loadTaskHistory(context);
        history.add(task);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(history);
        editor.putString(TASK_HISTORY_KEY, json);
        editor.apply();
    }

    // ✅ Load task history
    public static List<Task> loadTaskHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(TASK_HISTORY_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    // ✅ Optional: Clear task history
    public static void clearTaskHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(TASK_HISTORY_KEY).apply();
    }
}
