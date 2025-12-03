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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.Calendar;

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
        
        fab.setOnClickListener(v -> showAddTaskDialog());
        
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
    
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Task");
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        EditText titleInput = new EditText(getContext());
        titleInput.setHint("Task Title");
        layout.addView(titleInput);
        
        EditText descInput = new EditText(getContext());
        descInput.setHint("Description");
        layout.addView(descInput);
        
        EditText dueDateInput = new EditText(getContext());
        dueDateInput.setHint("Due Date (tap to select)");
        dueDateInput.setFocusable(false);
        
        final long[] selectedDate = {System.currentTimeMillis() + 86400000}; // Tomorrow
        dueDateInput.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(),
                (view, year, month, day) -> {
                    cal.set(year, month, day);
                    selectedDate[0] = cal.getTimeInMillis();
                    dueDateInput.setText(String.format("%02d/%02d/%d", month+1, day, year));
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        layout.addView(dueDateInput);
        
        Spinner prioritySpinner = new Spinner(getContext());
        ArrayAdapter<Task.Priority> priorityAdapter = new ArrayAdapter<>(getContext(),
            android.R.layout.simple_spinner_item, Task.Priority.values());
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);
        layout.addView(prioritySpinner);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            
            if (!title.isEmpty()) {
                Task newTask = new Task(title, desc, selectedDate[0], false);
                newTask.priority = (Task.Priority) prioritySpinner.getSelectedItem();
                newTask.status = Task.Status.TODO;
                newTask.createdDate = System.currentTimeMillis();
                
                repository.insertTask(newTask);
                loadData();
                Toast.makeText(getContext(), "✅ Task added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "❌ Please enter task title", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showTaskOptions(Task task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(task.title);
        
        String[] options = {"Edit", "Delete", "Mark as " + (task.completed ? "Incomplete" : "Complete")};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: showEditTaskDialog(task); break;
                case 1: deleteTask(task); break;
                case 2: toggleTaskCompletion(task); break;
            }
        });
        
        builder.show();
    }
    
    private void showEditTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Task");
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        EditText titleInput = new EditText(getContext());
        titleInput.setText(task.title);
        layout.addView(titleInput);
        
        EditText descInput = new EditText(getContext());
        descInput.setText(task.description);
        layout.addView(descInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Update", (dialog, which) -> {
            task.title = titleInput.getText().toString().trim();
            task.description = descInput.getText().toString().trim();
            
            repository.updateTask(task);
            loadData();
            Toast.makeText(getContext(), "Task updated!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void deleteTask(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            repository.deleteTask(task);
            loadData();
            Toast.makeText(getContext(), "Task deleted!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void toggleTaskCompletion(Task task) {
        repository.markTaskCompleted(task.id, !task.completed);
        loadData();
        Toast.makeText(getContext(), task.completed ? "Task completed!" : "Task marked incomplete", Toast.LENGTH_SHORT).show();
    }
}