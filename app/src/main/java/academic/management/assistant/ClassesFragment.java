package academic.management.assistant;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import academic.management.assistant.adapter.ClassAdapter;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.model.ClassItem;

public class ClassesFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private ClassDao classDao;
    private List<ClassItem> classes;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        classDao = new ClassDao(dbHelper);
        
        recyclerView = view.findViewById(R.id.classesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        FloatingActionButton fab = view.findViewById(R.id.fabAddClass);
        fab.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor(
                new academic.management.assistant.database.ThemeDao(dbHelper).getAccentColor()
            )
        ));
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddClassActivity.class);
            startActivity(intent);
        });
        
        view.findViewById(R.id.selectBtn).setOnClickListener(v -> toggleSelectionMode());
        view.findViewById(R.id.selectAllBtn).setOnClickListener(v -> selectAll());
        view.findViewById(R.id.deleteSelectedBtn).setOnClickListener(v -> deleteSelected());
        
        loadClasses();
        
        return view;
    }
    
    private void loadClasses() {
        classes = classDao.getAllClasses();
        adapter = new ClassAdapter(classes, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(ClassItem classItem) {
                Intent intent = new Intent(getActivity(), EditClassActivity.class);
                intent.putExtra("CLASS_ID", classItem.id);
                startActivity(intent);
            }
            
            @Override
            public void onClassLongClick(ClassItem classItem) {
                // Long click does nothing now, use edit screen to delete
            }
        });
        recyclerView.setAdapter(adapter);
    }
    
    private void toggleSelectionMode() {
        boolean newMode = !adapter.isSelectionMode();
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
        List<ClassItem> selected = adapter.getSelectedClasses();
        if (selected.isEmpty()) {
            android.widget.Toast.makeText(getActivity(), "No classes selected. Tap classes to select them, or tap 'All' to select all classes.", android.widget.Toast.LENGTH_LONG).show();
            return;
        }
        
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Classes")
                .setMessage("Delete " + selected.size() + " selected classes? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    for (ClassItem item : selected) {
                        classDao.deleteClass(item.id);
                    }
                    android.widget.Toast.makeText(getActivity(), selected.size() + " classes deleted", android.widget.Toast.LENGTH_SHORT).show();
                    loadClasses();
                    toggleSelectionMode();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showDeleteDialog(ClassItem classItem) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Class")
                .setMessage("Delete " + classItem.title + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    classDao.deleteClass(classItem.id);
                    loadClasses();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (classDao != null) {
            loadClasses();
        }
    }
}
