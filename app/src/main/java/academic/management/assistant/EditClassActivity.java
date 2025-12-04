package academic.management.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.model.ClassItem;

public class EditClassActivity extends Activity {
    
    private EditText titleEdit, locationEdit, startTimeEdit, endTimeEdit;
    private Spinner weekdaySpinner;
    private ClassDao classDao;
    private ClassItem classItem;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        classDao = new ClassDao(dbHelper);
        
        int classId = getIntent().getIntExtra("CLASS_ID", -1);
        classItem = classDao.getClassById(classId);
        
        if (classItem == null) {
            finish();
            return;
        }
        
        titleEdit = findViewById(R.id.titleEdit);
        locationEdit = findViewById(R.id.locationEdit);
        startTimeEdit = findViewById(R.id.startTimeEdit);
        endTimeEdit = findViewById(R.id.endTimeEdit);
        weekdaySpinner = findViewById(R.id.weekdaySpinner);
        
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekdays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(adapter);
        
        loadClassData();
        
        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> saveClass());
        
        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(v -> showDeleteDialog());
        
        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> finish());
    }
    
    private void loadClassData() {
        titleEdit.setText(classItem.title);
        locationEdit.setText(classItem.location);
        startTimeEdit.setText(classItem.startTime);
        endTimeEdit.setText(classItem.endTime);
        // Calendar to spinner: Sunday=1->6, Monday=2->0, Tuesday=3->1, etc.
        int spinnerPos = classItem.weekday == 1 ? 6 : classItem.weekday - 2;
        weekdaySpinner.setSelection(spinnerPos);
    }
    
    private void saveClass() {
        String title = titleEdit.getText().toString().trim();
        String location = locationEdit.getText().toString().trim();
        String startTime = startTimeEdit.getText().toString().trim();
        String endTime = endTimeEdit.getText().toString().trim();
        
        if (title.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        classItem.title = title;
        classItem.location = location;
        int spinnerPos = weekdaySpinner.getSelectedItemPosition();
        if (spinnerPos == 6) {
            classItem.weekday = 1; // Sunday
        } else {
            classItem.weekday = spinnerPos + 2;
        }
        classItem.startTime = startTime;
        classItem.endTime = endTime;
        
        classDao.updateClass(classItem);
        Toast.makeText(this, "Class updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Delete " + classItem.title + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    classDao.deleteClass(classItem.id);
                    Toast.makeText(this, "Class deleted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
