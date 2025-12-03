package academic.management.assistant.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.data.ClassItem;
import academic.management.assistant.adapter.ClassAdapter;

public class ClassesFragment extends Fragment {
    
    private RecyclerView classesRecycler;
    private FloatingActionButton fab;
    private ClassAdapter adapter;
    private List<ClassItem> classes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);
        
        classesRecycler = view.findViewById(R.id.classesRecycler);
        fab = view.findViewById(R.id.fab);
        
        setupRecyclerView();
        loadSampleData();
        
        fab.setOnClickListener(v -> {
            // Add new class
        });
        
        return view;
    }

    private void setupRecyclerView() {
        classesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter(classes);
        classesRecycler.setAdapter(adapter);
    }

    private void loadSampleData() {
        classes.add(new ClassItem("Mathematics", "Dr. Smith", "Room 101", "Mon, Wed, Fri 10:00"));
        classes.add(new ClassItem("Physics", "Dr. Johnson", "Lab 2", "Tue, Thu 14:00"));
        classes.add(new ClassItem("Chemistry", "Dr. Brown", "Lab 1", "Mon, Wed 16:00"));
        adapter.notifyDataSetChanged();
    }
}