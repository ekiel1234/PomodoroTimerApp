package com.example.group9pomodoroapp1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group9pomodoroapp1.adapter.TaskHistoryAdapter;
import com.example.group9pomodoroapp1.model.Task;
import com.example.group9pomodoroapp1.utils.TaskStorageUtils;

import java.util.List;

public class TaskHistoryActivity extends AppCompatActivity {

    private RecyclerView completedRecyclerView;
    private RecyclerView deletedRecyclerView;
    private TextView emptyMessage;

    private TextView completedHeader;
    private TextView deletedHeader;

    private TaskHistoryAdapter completedAdapter;
    private TaskHistoryAdapter deletedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);

        completedRecyclerView = findViewById(R.id.task_history_recycler_view);
        deletedRecyclerView = findViewById(R.id.deleted_task_recycler_view);
        emptyMessage = findViewById(R.id.empty_history_text);

        completedHeader = findViewById(R.id.completed_tasks_label);
        deletedHeader = findViewById(R.id.deleted_tasks_label);

        List<Task> completedTasks = TaskStorageUtils.loadTaskHistory(this);
        List<Task> deletedTasks = TaskStorageUtils.loadDeletedTaskHistory(this);

        boolean hasCompleted = !completedTasks.isEmpty();
        boolean hasDeleted = !deletedTasks.isEmpty();

        if (!hasCompleted && !hasDeleted) {
            emptyMessage.setVisibility(View.VISIBLE);
            completedHeader.setVisibility(View.GONE);
            deletedHeader.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }

        if (hasCompleted) {
            completedHeader.setText("✔ Completed Tasks (" + completedTasks.size() + ")");
            completedAdapter = new TaskHistoryAdapter(this, completedTasks, true);
            completedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            completedRecyclerView.setAdapter(completedAdapter);
        } else {
            completedHeader.setVisibility(View.GONE);
            completedRecyclerView.setVisibility(View.GONE);
        }

        if (hasDeleted) {
            deletedHeader.setText("🗑 Deleted Tasks (" + deletedTasks.size() + ")");
            deletedAdapter = new TaskHistoryAdapter(this, deletedTasks, false);
            deletedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            deletedRecyclerView.setAdapter(deletedAdapter);
        } else {
            deletedHeader.setVisibility(View.GONE);
            deletedRecyclerView.setVisibility(View.GONE);
        }
    }
}
