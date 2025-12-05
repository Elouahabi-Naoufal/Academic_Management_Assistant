package academic.management.assistant;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.adapter.TeacherAdapter;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.TeacherDao;
import academic.management.assistant.model.Teacher;

public class TeachersFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private TeacherAdapter adapter;
    private TeacherDao teacherDao;
    private List<Teacher> teachers;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teachers, container, false);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        teacherDao = new TeacherDao(dbHelper);
        
        recyclerView = view.findViewById(R.id.teachersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        FloatingActionButton fab = view.findViewById(R.id.fabAddTeacher);
        fab.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor(
                new academic.management.assistant.database.ThemeDao(dbHelper).getAccentColor()
            )
        ));
        fab.setOnClickListener(v -> showAddDialog());
        
        loadTeachers();
        
        return view;
    }
    
    private void loadTeachers() {
        teachers = teacherDao.getAllTeachers();
        adapter = new TeacherAdapter(teachers, this::showEditDialog);
        recyclerView.setAdapter(adapter);
    }
    
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Teacher");
        
        EditText input = new EditText(getActivity());
        input.setHint("Teacher name");
        builder.setView(input);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                Teacher teacher = new Teacher();
                teacher.fullName = name;
                teacherDao.insertTeacher(teacher);
                loadTeachers();
                Toast.makeText(getActivity(), "Teacher added!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showEditDialog(Teacher teacher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Teacher");
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getActivity());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        EditText nameInput = new EditText(getActivity());
        nameInput.setText(teacher.fullName);
        nameInput.setHint("Teacher name");
        layout.addView(nameInput);
        
        EditText imageInput = new EditText(getActivity());
        imageInput.setText(teacher.imagePath != null ? teacher.imagePath : "");
        imageInput.setHint("Image path (optional)");
        layout.addView(imageInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String imagePath = imageInput.getText().toString().trim();
            if (!name.isEmpty()) {
                teacher.fullName = name;
                teacher.imagePath = imagePath.isEmpty() ? null : imagePath;
                teacherDao.updateTeacher(teacher);
                loadTeachers();
                Toast.makeText(getActivity(), "Teacher updated!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Delete", (dialog, which) -> {
            teacherDao.deleteTeacher(teacher.id);
            loadTeachers();
            Toast.makeText(getActivity(), "Teacher deleted!", Toast.LENGTH_SHORT).show();
        });
        builder.setNeutralButton("Cancel", null);
        builder.show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (teacherDao != null) {
            loadTeachers();
        }
    }
}
