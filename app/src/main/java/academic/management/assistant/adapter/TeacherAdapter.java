package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.model.Teacher;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {
    
    private List<Teacher> teachers;
    private OnTeacherClickListener listener;
    
    public interface OnTeacherClickListener {
        void onTeacherClick(Teacher teacher);
    }
    
    public TeacherAdapter(List<Teacher> teachers, OnTeacherClickListener listener) {
        this.teachers = teachers;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);
        holder.teacherName.setText(teacher.fullName);
        holder.itemView.setOnClickListener(v -> listener.onTeacherClick(teacher));
    }
    
    @Override
    public int getItemCount() {
        return teachers.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teacherName;
        
        ViewHolder(View view) {
            super(view);
            teacherName = view.findViewById(R.id.teacherName);
        }
    }
}
