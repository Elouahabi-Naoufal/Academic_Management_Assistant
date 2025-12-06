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
        
        view.findViewById(R.id.selectBtn).setOnClickListener(v -> toggleSelectionMode());
        view.findViewById(R.id.selectAllBtn).setOnClickListener(v -> selectAll());
        view.findViewById(R.id.deleteSelectedBtn).setOnClickListener(v -> deleteSelected());
        
        loadTeachers();
        
        return view;
    }
    
    private void loadTeachers() {
        teachers = teacherDao.getAllTeachers();
        adapter = new TeacherAdapter(teachers, this::showEditDialog);
        recyclerView.setAdapter(adapter);
    }
    
    private void showAddDialog() {
        android.content.Intent intent = new android.content.Intent(getActivity(), AddTeacherActivity.class);
        startActivity(intent);
    }
    
    private void showEditDialog(Teacher teacher) {
        android.content.Intent intent = new android.content.Intent(getActivity(), EditTeacherActivity.class);
        intent.putExtra("TEACHER_ID", teacher.id);
        startActivity(intent);
    }
    
    private void toggleSelectionMode() {
        boolean newMode = !adapter.selectionMode;
        adapter.setSelectionMode(newMode);
        
        android.widget.Button selectBtn = getView().findViewById(R.id.selectBtn);
        android.widget.Button selectAllBtn = getView().findViewById(R.id.selectAllBtn);
        android.widget.Button deleteBtn = getView().findViewById(R.id.deleteSelectedBtn);
        
        selectBtn.setText(newMode ? "Cancel" : "Select");
        selectAllBtn.setVisibility(newMode ? View.VISIBLE : View.GONE);
        deleteBtn.setVisibility(newMode ? View.VISIBLE : View.GONE);
    }
    
    private void selectAll() {
        adapter.selectAll();
    }
    
    private void deleteSelected() {
        List<Teacher> selected = adapter.getSelectedTeachers();
        if (selected.isEmpty()) {
            Toast.makeText(getActivity(), "No teachers selected. Tap teachers to select them, or tap 'All' to select all teachers.", Toast.LENGTH_LONG).show();
            return;
        }
        
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Teachers")
                .setMessage("Delete " + selected.size() + " selected teachers? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    for (Teacher item : selected) {
                        teacherDao.deleteTeacher(item.id);
                    }
                    Toast.makeText(getActivity(), selected.size() + " teachers deleted", Toast.LENGTH_SHORT).show();
                    loadTeachers();
                    toggleSelectionMode();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (teacherDao != null) {
            loadTeachers();
        }
    }
}
