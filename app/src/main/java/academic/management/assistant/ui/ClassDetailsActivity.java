package academic.management.assistant.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import academic.management.assistant.R;
import academic.management.assistant.data.ClassItem;
import academic.management.assistant.database.Repository;

public class ClassDetailsActivity extends AppCompatActivity {
    
    private ClassItem classItem;
    private Repository repository;
    private boolean isEditMode = false;
    
    private EditText nameEdit, teacherEdit, roomEdit, scheduleEdit, creditsEdit;
    private TextView startDateText, endDateText;
    private MaterialButton editBtn, saveBtn, deleteBtn;
    private ImageButton backBtn;
    private Long selectedStartDate, selectedEndDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);
        
        repository = new Repository(this);
        
        // Get class ID from intent
        int classId = getIntent().getIntExtra("CLASS_ID", -1);
        if (classId == -1) {
            finish();
            return;
        }
        
        classItem = repository.getClassById(classId);
        if (classItem == null) {
            finish();
            return;
        }
        
        initViews();
        setupToolbar();
        loadClassData();
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
    
    private void loadClassData() {
        nameEdit.setText(classItem.name);
        teacherEdit.setText(classItem.teacher);
        roomEdit.setText(classItem.room);
        scheduleEdit.setText(classItem.schedule);
        creditsEdit.setText(String.valueOf(classItem.credits));
        
        SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        if (classItem.startDate != null) {
            startDateText.setText(fmt.format(new Date(classItem.startDate)));
            selectedStartDate = classItem.startDate;
        } else {
            startDateText.setText("Not set");
        }
        
        if (classItem.endDate != null) {
            endDateText.setText(fmt.format(new Date(classItem.endDate)));
            selectedEndDate = classItem.endDate;
        } else {
            endDateText.setText("Not set");
        }
        
        setEditMode(false);
    }
    
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        
        editBtn.setOnClickListener(v -> setEditMode(true));
        
        saveBtn.setOnClickListener(v -> saveChanges());
        
        deleteBtn.setOnClickListener(v -> showDeleteConfirmation());
        
        startDateText.setOnClickListener(v -> {
            if (isEditMode) showDatePicker(true);
        });
        
        endDateText.setOnClickListener(v -> {
            if (isEditMode) showDatePicker(false);
        });
    }
    
    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        
        nameEdit.setEnabled(editMode);
        teacherEdit.setEnabled(editMode);
        roomEdit.setEnabled(editMode);
        scheduleEdit.setEnabled(editMode);
        creditsEdit.setEnabled(editMode);
        
        editBtn.setVisibility(editMode ? View.GONE : View.VISIBLE);
        saveBtn.setVisibility(editMode ? View.VISIBLE : View.GONE);
        
        // Visual feedback for edit mode
        int bgColor = editMode ? 0x1A6C63FF : 0x00000000;
        nameEdit.setBackgroundColor(bgColor);
        teacherEdit.setBackgroundColor(bgColor);
        roomEdit.setBackgroundColor(bgColor);
        scheduleEdit.setBackgroundColor(bgColor);
        creditsEdit.setBackgroundColor(bgColor);
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
    
    private void saveChanges() {
        try {
            classItem.name = nameEdit.getText().toString().trim();
            classItem.teacher = teacherEdit.getText().toString().trim();
            classItem.room = roomEdit.getText().toString().trim();
            classItem.schedule = scheduleEdit.getText().toString().trim();
            classItem.credits = Integer.parseInt(creditsEdit.getText().toString().trim());
            classItem.startDate = selectedStartDate;
            classItem.endDate = selectedEndDate;
            
            repository.updateClass(classItem);
            setEditMode(false);
            Toast.makeText(this, "✅ Class updated successfully!", Toast.LENGTH_SHORT).show();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "❌ Please enter valid credits", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🗑️ Delete Class");
        builder.setMessage("Are you sure you want to delete \"" + classItem.name + "\"?\\n\\nThis will also delete all related grades and events.");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            repository.deleteClass(classItem);
            Toast.makeText(this, "🗑️ Class deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
        
        builder.setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Style the buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.error));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.textSecondary));
    }
}