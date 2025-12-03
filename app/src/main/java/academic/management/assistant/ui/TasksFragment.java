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
import academic.management.assistant.data.Task;
import academic.management.assistant.adapter.TaskAdapter;

public class TasksFragment extends Fragment {
    
    private RecyclerView tasksRecycler;
    private FloatingActionButton fab;
    private TaskAdapter adapter;
    private List<Task> tasks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        tasksRecycler = view.findViewById(R.id.tasksRecycler);
        fab = view.findViewById(R.id.fab);
        
        setupRecyclerView();
        loadSampleData();
        
        fab.setOnClickListener(v -> {
            // Add new task
        });
        
        return view;
    }

    private void setupRecyclerView() {
        tasksRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(tasks);
        tasksRecycler.setAdapter(adapter);
    }

    private void loadSampleData() {
        // No mock data - clean slate
        adapter.notifyDataSetChanged();
    }
}