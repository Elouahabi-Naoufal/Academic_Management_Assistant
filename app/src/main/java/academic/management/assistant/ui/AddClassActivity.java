package academic.management.assistant.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
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
import academic.management.assistant.data.ClassItem;
import academic.management.assistant.database.Repository;

public class AddClassActivity extends AppCompatActivity {
    
    private Repository repository;
    private EditText nameEdit, teacherEdit, roomEdit, scheduleEdit, creditsEdit;
    private TextView startDateText, endDateText;
    private MaterialButton saveBtn;
    private ImageButton backBtn;
    private Long selectedStartDate, selectedEndDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        
        repository = new Repository(this);
        
        initViews();
        setupToolbar();
        setupClickListeners();
    }
    
    private void initViews() {
        nameEdit = findViewById(R.id.nameEdit);
        teacherEdit = findViewById(R.id.teacherEdit);
        roomEdit = findViewById(R.id.roomEdit);
        scheduleEdit = findViewById(R.id.scheduleEdit);
        creditsEdit = findViewById(R.id.creditsEdit);
        startDateText = findViewById(R.id.startDateText);
        endDateText = findViewById(R.id.endDateText);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);
        
        // Set default values
        creditsEdit.setText("3");
        startDateText.setText("Tap to select");
        endDateText.setText("Tap to select");
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        
        saveBtn.setOnClickListener(v -> saveClass());
        
        startDateText.setOnClickListener(v -> showDatePicker(true));
        endDateText.setOnClickListener(v -> showDatePicker(false));
    }
    
    private void showDatePicker(boolean isStartDate) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
            (view, year, month, day) -> {
                cal.set(year, month, day);
                long selectedDate = cal.getTimeInMillis();
                SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                
                if (isStartDate) {
                    selectedStartDate = selectedDate;
                    startDateText.setText(fmt.format(new Date(selectedDate)));
                } else {
                    selectedEndDate = selectedDate;
                    endDateText.setText(fmt.format(new Date(selectedDate)));
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    
    private void saveClass() {
        String name = nameEdit.getText().toString().trim();
        String teacher = teacherEdit.getText().toString().trim();
        String room = roomEdit.getText().toString().trim();
        String schedule = scheduleEdit.getText().toString().trim();
        
        if (name.isEmpty() || teacher.isEmpty()) {
            Toast.makeText(this, "❌ Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            ClassItem newClass = new ClassItem(name, teacher, room, schedule);
            
            String creditsStr = creditsEdit.getText().toString().trim();
            newClass.credits = !creditsStr.isEmpty() ? Integer.parseInt(creditsStr) : 3;
            
            newClass.startDate = selectedStartDate;
            newClass.endDate = selectedEndDate;
            newClass.isActive = true;
            newClass.year = Calendar.getInstance().get(Calendar.YEAR);
            
            repository.insertClass(newClass);
            Toast.makeText(this, "✅ Class added successfully!", Toast.LENGTH_SHORT).show();
            finish();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "❌ Please enter valid credits", Toast.LENGTH_SHORT).show();
        }
    }
}