package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import academic.management.assistant.R;
import academic.management.assistant.data.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    
    public interface OnTaskClickListener {
        void onTaskClick(Task task, int position);
    }
    
    public interface OnTaskCheckListener {
        void onTaskCheck(Task task);
    }
    
    private List<Task> tasks;
    private OnTaskClickListener clickListener;
    private OnTaskCheckListener checkListener;
    
    public TaskAdapter(List<Task> tasks, OnTaskClickListener clickListener, OnTaskCheckListener checkListener) {
        this.tasks = tasks;
        this.clickListener = clickListener;
        this.checkListener = checkListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.titleText.setText(task.title);
        holder.descriptionText.setText(task.description);
        holder.checkBox.setChecked(task.completed);
        
        SimpleDateFormat fmt = new SimpleDateFormat("MMM dd", Locale.getDefault());
        holder.dueDateText.setText("Due: " + fmt.format(new Date(task.dueDate)));
        
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkListener != null) {
                checkListener.onTaskCheck(task);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onTaskClick(task, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText, dueDateText;
        CheckBox checkBox;
        
        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}