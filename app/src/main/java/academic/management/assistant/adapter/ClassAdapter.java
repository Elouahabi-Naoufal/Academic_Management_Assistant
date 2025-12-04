package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.model.ClassItem;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    
    private List<ClassItem> classes;
    private OnClassClickListener listener;
    
    public interface OnClassClickListener {
        void onClassClick(ClassItem classItem);
        void onClassLongClick(ClassItem classItem);
    }
    
    public ClassAdapter(List<ClassItem> classes, OnClassClickListener listener) {
        this.classes = classes;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassItem item = classes.get(position);
        holder.classTitle.setText(item.title);
        holder.moduleName.setText("📚 " + item.moduleName);
        holder.teacherName.setText("👨‍🏫 " + item.teacherName);
        holder.classTime.setText("🕐 " + item.getWeekdayName() + " " + item.startTime + "-" + item.endTime);
        holder.classLocation.setText("📍 " + (item.location != null ? item.location : "No location"));
        
        holder.itemView.setOnClickListener(v -> listener.onClassClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onClassLongClick(item);
            return true;
        });
    }
    
    @Override
    public int getItemCount() {
        return classes.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView classTitle, moduleName, teacherName, classTime, classLocation;
        
        ViewHolder(View view) {
            super(view);
            classTitle = view.findViewById(R.id.classTitle);
            moduleName = view.findViewById(R.id.moduleName);
            teacherName = view.findViewById(R.id.teacherName);
            classTime = view.findViewById(R.id.classTime);
            classLocation = view.findViewById(R.id.classLocation);
        }
    }
}
