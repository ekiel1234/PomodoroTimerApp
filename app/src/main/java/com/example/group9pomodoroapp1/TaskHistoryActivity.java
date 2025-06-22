package com.example.group9pomodoroapp1;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group9pomodoroapp1.adapter.TaskHistoryAdapter;
import com.example.group9pomodoroapp1.model.Task;
import com.example.group9pomodoroapp1.utils.TaskStorageUtils;

import java.util.List;

public class TaskHistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private TextView emptyMessage;
    private TaskHistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);

        historyRecyclerView = findViewById(R.id.task_history_recycler_view);
        emptyMessage = findViewById(R.id.empty_history_text);

        List<Task> completedTasks = TaskStorageUtils.loadTaskHistory(this);

        if (completedTasks.isEmpty()) {
            emptyMessage.setText("No completed tasks yet.");
            emptyMessage.setVisibility(TextView.VISIBLE);
        } else {
            emptyMessage.setVisibility(TextView.GONE);
        }

        historyAdapter = new TaskHistoryAdapter(this, completedTasks);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);
    }
}
