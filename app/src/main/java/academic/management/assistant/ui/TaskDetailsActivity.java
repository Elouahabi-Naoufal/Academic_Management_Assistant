package academic.management.assistant.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import academic.management.assistant.R;
import academic.management.assistant.data.Task;
import academic.management.assistant.database.Repository;

public class TaskDetailsActivity extends AppCompatActivity {
    
    private Task task;
    private Repository repository;
    private boolean isEditMode = false;
    
    private EditText titleEdit, descriptionEdit;
    private TextView dueDateText, statusText;
    private Spinner prioritySpinner;
    private CheckBox completedCheck;
    private MaterialButton editBtn, saveBtn, deleteBtn;
    private ImageButton backBtn;
    private long selectedDueDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        
        repository = new Repository(this);
        
        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        if (taskId == -1) {
            finish();
            return;
        }
        
        task = repository.getTaskById(taskId);
        if (task == null) {
            finish();
            return;
        }
        
        initViews();
        setupToolbar();
        setupSpinner();
        loadTaskData();
        setupClickListeners();
    }
    
    private void initViews() {
        titleEdit = findViewById(R.id.titleEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        dueDateText = findViewById(R.id.dueDateText);
        statusText = findViewById(R.id.statusText);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        completedCheck = findViewById(R.id.completedCheck);
        editBtn = findViewById(R.id.editBtn);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        backBtn = findViewById(R.id.backBtn);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    
    private void setupSpinner() {
        ArrayAdapter<Task.Priority> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, Task.Priority.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
    }
    
    private void loadTaskData() {
        titleEdit.setText(task.title);
        descriptionEdit.setText(task.description);
        completedCheck.setChecked(task.completed);
        
        SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        dueDateText.setText(fmt.format(new Date(task.dueDate)));
        selectedDueDate = task.dueDate;
        
        statusText.setText(task.status != null ? task.status.name() : "TODO");
        
        if (task.priority != null) {
            prioritySpinner.setSelection(task.priority.ordinal());
        }
        
        setEditMode(false);
    }
    
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        editBtn.setOnClickListener(v -> setEditMode(true));
        saveBtn.setOnClickListener(v -> saveChanges());
        deleteBtn.setOnClickListener(v -> showDeleteConfirmation());
        
        dueDateText.setOnClickListener(v -> {
            if (isEditMode) showDatePicker();
        });
        
        completedCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isEditMode) {
                task.completed = isChecked;
                task.status = isChecked ? Task.Status.COMPLETED : Task.Status.TODO;
                repository.markTaskCompleted(task.id, isChecked);
                statusText.setText(task.status.name());
                Toast.makeText(this, isChecked ? "✅ Task completed!" : "📝 Task reopened", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        
        titleEdit.setEnabled(editMode);
        descriptionEdit.setEnabled(editMode);
        prioritySpinner.setEnabled(editMode);
        
        editBtn.setVisibility(editMode ? View.GONE : View.VISIBLE);
        saveBtn.setVisibility(editMode ? View.VISIBLE : View.GONE);
        
        int bgColor = editMode ? 0x1AFF6B9D : 0x00000000;
        titleEdit.setBackgroundColor(bgColor);
        descriptionEdit.setBackgroundColor(bgColor);
    }
    
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDueDate);
        DatePickerDialog dialog = new DatePickerDialog(this,
            (view, year, month, day) -> {
                cal.set(year, month, day);
                selectedDueDate = cal.getTimeInMillis();
                SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                dueDateText.setText(fmt.format(new Date(selectedDueDate)));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    
    private void saveChanges() {
        String title = titleEdit.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "❌ Please enter task title", Toast.LENGTH_SHORT).show();
            return;
        }
        
        task.title = title;
        task.description = descriptionEdit.getText().toString().trim();
        task.dueDate = selectedDueDate;
        task.priority = (Task.Priority) prioritySpinner.getSelectedItem();
        
        repository.updateTask(task);
        setEditMode(false);
        Toast.makeText(this, "✅ Task updated successfully!", Toast.LENGTH_SHORT).show();
    }
    
    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🗑️ Delete Task");
        builder.setMessage("Are you sure you want to delete \"" + task.title + "\"?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            repository.deleteTask(task);
            Toast.makeText(this, "🗑️ Task deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
        
        builder.setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.error));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.textSecondary));
    }
}