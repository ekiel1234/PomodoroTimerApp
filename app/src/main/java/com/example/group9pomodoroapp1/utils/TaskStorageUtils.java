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
    private static final String TASK_HISTORY_PREFS = "task_history_prefs";
    private static final String TASK_HISTORY_KEY = "task_history";
    private static final String DELETED_TASK_HISTORY_PREFS = "deleted_history_prefs";
    private static final String DELETED_TASK_HISTORY_KEY = "deleted_task_history";

    // ⏺️ Current tasks
    public static void saveTasks(Context context, List<Task> taskList) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(taskList);
        editor.putString(TASK_LIST_KEY, json);
        editor.apply();
    }

    public static List<Task> loadTasks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(TASK_LIST_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    // ✅ Completed Tasks (Task History)
    public static void addTaskToHistory(Context context, Task task) {
        List<Task> history = loadTaskHistory(context);
        history.add(task);
        saveTaskHistory(context, history);
    }

    public static List<Task> loadTaskHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TASK_HISTORY_PREFS, Context.MODE_PRIVATE);
        String json = prefs.getString(TASK_HISTORY_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void saveTaskHistory(Context context, List<Task> tasks) {
        SharedPreferences prefs = context.getSharedPreferences(TASK_HISTORY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(tasks);
        editor.putString(TASK_HISTORY_KEY, json);
        editor.apply();
    }

    // ✅ Deleted Tasks (from active task list)
    public static void addTaskToDeletedHistory(Context context, Task task) {
        List<Task> deleted = loadDeletedTaskHistory(context);
        deleted.add(task);
        saveDeletedTaskHistory(context, deleted);
    }

    public static List<Task> loadDeletedTaskHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DELETED_TASK_HISTORY_PREFS, Context.MODE_PRIVATE);
        String json = prefs.getString(DELETED_TASK_HISTORY_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void saveDeletedTaskHistory(Context context, List<Task> tasks) {
        SharedPreferences prefs = context.getSharedPreferences(DELETED_TASK_HISTORY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(tasks);
        editor.putString(DELETED_TASK_HISTORY_KEY, json);
        editor.apply();
    }
}
