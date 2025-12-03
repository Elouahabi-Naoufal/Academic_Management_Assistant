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
import academic.management.assistant.database.Repository;
import academic.management.assistant.ui.AddTaskActivity;
import academic.management.assistant.ui.TaskDetailsActivity;
import android.widget.Toast;

public class TasksFragment extends Fragment {
    
    private RecyclerView tasksRecycler;
    private FloatingActionButton fab;
    private TaskAdapter adapter;
    private List<Task> tasks = new ArrayList<>();
    private Repository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        tasksRecycler = view.findViewById(R.id.tasksRecycler);
        fab = view.findViewById(R.id.fab);
        
        repository = new Repository(getContext());
        setupRecyclerView();
        loadData();
        
        fab.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(), AddTaskActivity.class);
            startActivity(intent);
        });
        
        return view;
    }

    private void setupRecyclerView() {
        tasksRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(tasks, this::showTaskOptions, this::toggleTaskCompletion);
        tasksRecycler.setAdapter(adapter);
    }

    private void loadData() {
        tasks.clear();
        tasks.addAll(repository.getAllTasks());
        adapter.notifyDataSetChanged();
    }
    

    
    private void showTaskOptions(Task task, int position) {
        android.content.Intent intent = new android.content.Intent(getContext(), TaskDetailsActivity.class);
        intent.putExtra("TASK_ID", task.id);
        startActivity(intent);
    }
    

    

    
    private void toggleTaskCompletion(Task task) {
        repository.markTaskCompleted(task.id, !task.completed);
        loadData();
        Toast.makeText(getContext(), task.completed ? "✅ Task completed!" : "📝 Task reopened", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (repository != null) {
            loadData();
        }
    }
}