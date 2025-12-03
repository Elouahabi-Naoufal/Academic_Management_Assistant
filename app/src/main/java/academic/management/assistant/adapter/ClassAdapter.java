package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.data.ClassItem;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    
    private List<ClassItem> classes;
    
    public ClassAdapter(List<ClassItem> classes) {
        this.classes = classes;
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
        ClassItem classItem = classes.get(position);
        holder.nameText.setText(classItem.name);
        holder.teacherText.setText(classItem.teacher);
        holder.roomText.setText(classItem.room);
        holder.scheduleText.setText(classItem.schedule);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, teacherText, roomText, scheduleText;
        
        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            teacherText = itemView.findViewById(R.id.teacherText);
            roomText = itemView.findViewById(R.id.roomText);
            scheduleText = itemView.findViewById(R.id.scheduleText);
        }
    }
}