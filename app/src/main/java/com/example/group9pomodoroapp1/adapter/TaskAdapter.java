package com.example.group9pomodoroapp1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group9pomodoroapp1.R;
import com.example.group9pomodoroapp1.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskDeletedListener {
        void onTaskDeleted(int position);
    }

    private final Context context;
    private final List<Task> taskList;
    private final OnTaskDeletedListener listener;

    public TaskAdapter(Context context, List<Task> taskList, OnTaskDeletedListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskNameTextView.setText(task.getName());

        String progressText = task.getCompletedPomodoros() + " / " + task.getEstimatedPomodoros() + " pomodoros";
        holder.taskProgressTextView.setText(progressText);
        holder.taskProgressBar.setMax(task.getEstimatedPomodoros());
        holder.taskProgressBar.setProgress(task.getCompletedPomodoros());

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskDeleted(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // ✅ FIX: Make ViewHolder public to avoid visibility error
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        TextView taskProgressTextView;
        ProgressBar taskProgressBar;
        ImageButton deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.task_name_textview);
            taskProgressTextView = itemView.findViewById(R.id.task_progress_textview);
            taskProgressBar = itemView.findViewById(R.id.task_progress_bar);
            deleteButton = itemView.findViewById(R.id.delete_task_button);
        }
    }
}
