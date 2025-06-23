package com.example.group9pomodoroapp1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group9pomodoroapp1.R;
import com.example.group9pomodoroapp1.model.Task;
import com.example.group9pomodoroapp1.utils.TaskStorageUtils;

import java.util.List;

public class TaskHistoryAdapter extends RecyclerView.Adapter<TaskHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Task> taskList;
    private final boolean isDeletedList;

    public TaskHistoryAdapter(Context context, List<Task> taskList, boolean isDeletedList) {
        this.context = context;
        this.taskList = taskList;
        this.isDeletedList = isDeletedList;
    }

    @NonNull
    @Override
    public TaskHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHistoryAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getName());
        holder.pomodorosUsed.setText("Pomodoros Used: " + task.getProgress());
        holder.distractions.setText("Distractions: " + task.getDistractionCount());

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to permanently delete this task from history?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        taskList.remove(position);
                        notifyItemRemoved(position);

                        if (isDeletedList) {
                            TaskStorageUtils.saveDeletedTaskHistory(context, taskList);
                        } else {
                            TaskStorageUtils.saveTaskHistory(context, taskList);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, pomodorosUsed, distractions;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.history_task_name);
            pomodorosUsed = itemView.findViewById(R.id.history_pomodoro_count);
            distractions = itemView.findViewById(R.id.history_distraction_count);
            deleteButton = itemView.findViewById(R.id.delete_history_task_button);
        }
    }
}
