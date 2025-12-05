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
