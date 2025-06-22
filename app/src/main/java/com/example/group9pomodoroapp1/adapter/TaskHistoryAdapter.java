package com.example.group9pomodoroapp1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group9pomodoroapp1.R;
import com.example.group9pomodoroapp1.model.Task;

import java.util.List;

public class TaskHistoryAdapter extends RecyclerView.Adapter<TaskHistoryAdapter.TaskHistoryViewHolder> {

    private final Context context;
    private final List<Task> historyList;

    public TaskHistoryAdapter(Context context, List<Task> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public TaskHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_history, parent, false);
        return new TaskHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHistoryViewHolder holder, int position) {
        Task task = historyList.get(position);
        holder.nameTextView.setText(task.getName());
        holder.pomodoroCountTextView.setText("Pomodoros Used: " + task.getCompletedPomodoros());
        holder.distractionCountTextView.setText("Distractions: " + task.getDistractionCount());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class TaskHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, pomodoroCountTextView, distractionCountTextView;

        public TaskHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.history_task_name);
            pomodoroCountTextView = itemView.findViewById(R.id.history_pomodoro_count);
            distractionCountTextView = itemView.findViewById(R.id.history_distraction_count);
        }
    }
}
