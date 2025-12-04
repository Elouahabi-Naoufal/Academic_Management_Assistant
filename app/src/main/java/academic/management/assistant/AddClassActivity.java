package academic.management.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.model.ClassItem;

public class AddClassActivity extends Activity {
    
    private EditText titleEdit, locationEdit, startTimeEdit, endTimeEdit;
    private Spinner weekdaySpinner;
    private ClassDao classDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        classDao = new ClassDao(dbHelper);
        
        titleEdit = findViewById(R.id.titleEdit);
        locationEdit = findViewById(R.id.locationEdit);
        startTimeEdit = findViewById(R.id.startTimeEdit);
        endTimeEdit = findViewById(R.id.endTimeEdit);
        weekdaySpinner = findViewById(R.id.weekdaySpinner);
        
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekdays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(adapter);
        
        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> saveClass());
        
        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> finish());
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
        
        ClassItem classItem = new ClassItem();
        classItem.title = title;
        classItem.moduleId = 1; // TODO: Select module
        classItem.teacherId = 1; // TODO: Select teacher
        classItem.location = location;
        classItem.weekday = weekdaySpinner.getSelectedItemPosition() + 1;
        classItem.startTime = startTime;
        classItem.endTime = endTime;
        classItem.isArchived = false;
        
        classDao.insertClass(classItem);
        Toast.makeText(this, "Class added!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
