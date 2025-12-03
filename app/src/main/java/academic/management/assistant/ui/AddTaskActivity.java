package academic.management.assistant.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
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

public class AddTaskActivity extends AppCompatActivity {
    
    private Repository repository;
    private EditText titleEdit, descriptionEdit;
    private TextView dueDateText;
    private Spinner prioritySpinner;
    private MaterialButton saveBtn;
    private ImageButton backBtn;
    private long selectedDueDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        
        repository = new Repository(this);
        
        initViews();
        setupToolbar();
        setupSpinner();
        setupClickListeners();
        setDefaultValues();
    }
    
    private void initViews() {
        titleEdit = findViewById(R.id.titleEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        dueDateText = findViewById(R.id.dueDateText);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        saveBtn = findViewById(R.id.saveBtn);
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
        prioritySpinner.setSelection(1); // MEDIUM as default
    }
    
    private void setDefaultValues() {
        selectedDueDate = System.currentTimeMillis() + 86400000; // Tomorrow
        SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        dueDateText.setText(fmt.format(new Date(selectedDueDate)));
    }
    
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(v -> saveTask());
        dueDateText.setOnClickListener(v -> showDatePicker());
    }
    
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
            (view, year, month, day) -> {
                cal.set(year, month, day);
                selectedDueDate = cal.getTimeInMillis();
                SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                dueDateText.setText(fmt.format(new Date(selectedDueDate)));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    
    private void saveTask() {
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "❌ Please enter task title", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Task newTask = new Task(title, description, selectedDueDate, false);
        newTask.priority = (Task.Priority) prioritySpinner.getSelectedItem();
        newTask.status = Task.Status.TODO;
        newTask.createdDate = System.currentTimeMillis();
        
        repository.insertTask(newTask);
        Toast.makeText(this, "✅ Task added successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}