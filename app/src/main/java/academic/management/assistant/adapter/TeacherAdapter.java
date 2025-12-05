package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.model.Teacher;
import academic.management.assistant.model.ClassItem;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;

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
        
        // Load teacher image
        if (teacher.imagePath != null && !teacher.imagePath.isEmpty()) {
            File imageFile = new File(teacher.imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(teacher.imagePath);
                holder.teacherPhoto.setImageBitmap(bitmap);
            } else {
                holder.teacherPhoto.setImageResource(android.R.drawable.ic_menu_myplaces);
            }
        } else {
            holder.teacherPhoto.setImageResource(android.R.drawable.ic_menu_myplaces);
        }
        
        // Load and display classes for this teacher
        DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
        ClassDao classDao = new ClassDao(dbHelper);
        List<ClassItem> teacherClasses = classDao.getClassesByTeacher(teacher.id);
        
        holder.classesContainer.removeAllViews();
        if (!teacherClasses.isEmpty()) {
            holder.classesContainer.setVisibility(View.VISIBLE);
            for (ClassItem classItem : teacherClasses) {
                TextView classView = new TextView(holder.itemView.getContext());
                classView.setText("• " + classItem.title);
                classView.setTextSize(16);
                classView.setTextColor(Color.parseColor("#64748B"));
                classView.setPadding(0, 4, 0, 4);
                holder.classesContainer.addView(classView);
            }
        } else {
            holder.classesContainer.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> listener.onTeacherClick(teacher));
    }
    
    @Override
    public int getItemCount() {
        return teachers.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teacherName;
        ImageView teacherPhoto;
        LinearLayout classesContainer;
        
        ViewHolder(View view) {
            super(view);
            teacherName = view.findViewById(R.id.teacherName);
            teacherPhoto = view.findViewById(R.id.teacherPhoto);
            classesContainer = view.findViewById(R.id.classesContainer);
        }
    }
}