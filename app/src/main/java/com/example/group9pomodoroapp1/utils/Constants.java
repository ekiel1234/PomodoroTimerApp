package com.example.group9pomodoroapp1.utils;

public class Constants {

    // === Session Types ===
    public static final int WORK_SESSION = 0;
    public static final int SHORT_BREAK = 1;
    public static final int LONG_BREAK = 2;

    // === Notification ===
    public static final String NOTIFICATION_CHANNEL_ID = "pomodoro_notification_channel";
    public static final int TASK_INFORMATION_NOTIFICATION_ID = 10;

    // === Broadcast Actions ===
    public static final String COUNTDOWN_BROADCAST = "countdown_broadcast";
    public static final String STOP_ACTION_BROADCAST = "stop_action_broadcast";
    public static final String COMPLETE_ACTION_BROADCAST = "complete_action_broadcast";
    public static final String START_ACTION_BROADCAST = "start_action_broadcast";

    // === Timer Control Actions (Service Intents) ===
    public static final String ACTION_START = "com.example.group9pomodoroapp1.ACTION_START";
    public static final String ACTION_PAUSE = "com.example.group9pomodoroapp1.ACTION_PAUSE";
    public static final String ACTION_RESUME = "com.example.group9pomodoroapp1.ACTION_RESUME";
    public static final String ACTION_STOP = "com.example.group9pomodoroapp1.ACTION_STOP";

    // (Optional) Action Identifiers — not currently used, can be removed if unused
    public static final String INTENT_NAME_ACTION = "intent_name_action";
    public static final String ACTION_START_TIMER = "action_start_timer";
    public static final String ACTION_COMPLETE_TIMER = "action_complete_timer";
    public static final String ACTION_CANCEL_TIMER = "action_cancel_timer";
    public static final String ACTION_START_SHORT_BREAK = "action_short_break";
    public static final String ACTION_START_LONG_BREAK = "action_long_break";

    // === SharedPreferences Keys ===
    public static final String WORK_DURATION_KEY = "work_duration_key";
    public static final String SHORT_BREAK_DURATION_KEY = "short_break_duration_key";
    public static final String LONG_BREAK_DURATION_KEY = "long_break_duration_key";
    public static final String LONG_BREAK_AFTER_KEY = "long_break_after_key";
    public static final String SESSION_COMPLETED_COUNT_KEY = "session_completed_count_key";
    public static final String TASK_PROGRESS_COUNT_KEY = "task_progress_count_key";
    public static final String CURRENT_SESSION_TYPE_KEY = "current_session_type_key";
    public static final String TICKING_VOLUME_LEVEL_KEY = "ticking_volume_level_key";
    public static final String RINGING_VOLUME_LEVEL_KEY = "ringing_volume_level_key";

    public static final String TASK_MESSAGE = "task_message_key";

    // === Timer Settings ===
    public static final long TIME_INTERVAL = 1000; // 1 second
}
