package com.example.group9pomodoroapp1.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.group9pomodoroapp1.R;

public class CheckMarkUtils {

    public static void updateCheckMarkCount(Activity activity) {
        LinearLayout insertPoint = activity.findViewById(R.id.insert_point);
        TextView sessionCountTextView = activity.findViewById(R.id.session_completed_value_textview_main);

        if (insertPoint == null || sessionCountTextView == null) return;

        insertPoint.removeAllViews();

        Context context = activity.getApplicationContext();
        android.content.SharedPreferences preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int n = preferences.getInt(Constants.TASK_PROGRESS_COUNT_KEY, 0);

        sessionCountTextView.setText(String.valueOf(n));

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        for (int i = 0; i < n; i++) {
            View checkMarkView = layoutInflater.inflate(R.layout.check_mark, insertPoint, false);
            insertPoint.addView(checkMarkView);
        }
    }
}