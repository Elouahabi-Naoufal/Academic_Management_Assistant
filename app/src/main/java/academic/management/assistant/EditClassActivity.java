package academic.management.assistant;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.CheckBox;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ModuleDao;
import academic.management.assistant.database.TeacherDao;
import academic.management.assistant.database.ThemeDao;
import academic.management.assistant.model.ClassItem;
import java.util.List;
import java.util.ArrayList;

public class EditClassActivity extends AppCompatActivity {
    
    private EditText titleEdit, locationEdit, startTimeEdit, endTimeEdit;
    private Spinner weekdaySpinner, moduleSpinner, teacherSpinner;
    private ClassDao classDao;
    private ClassItem classItem;
    private List<academic.management.assistant.model.Module> modules;
    private List<academic.management.assistant.model.Teacher> teachers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        int nightMode = themeDao.isDarkTheme() ? 
            AppCompatDelegate.MODE_NIGHT_YES : 
            AppCompatDelegate.MODE_NIGHT_NO;
        getDelegate().setLocalNightMode(nightMode);
        
        getTheme().applyStyle(getAccentStyle(themeDao.getAccentColor()), true);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        
        classDao = new ClassDao(dbHelper);
        ModuleDao moduleDao = new ModuleDao(dbHelper);
        TeacherDao teacherDao = new TeacherDao(dbHelper);
        
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
        moduleSpinner = findViewById(R.id.moduleSpinner);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        
        // Load modules
        modules = moduleDao.getAllModules();
        List<String> moduleNames = new ArrayList<>();
        int modulePos = 0;
        for (int i = 0; i < modules.size(); i++) {
            moduleNames.add(modules.get(i).name);
            if (modules.get(i).id == classItem.moduleId) modulePos = i;
        }
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moduleNames);
        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moduleSpinner.setAdapter(moduleAdapter);
        moduleSpinner.setSelection(modulePos);
        
        // Load teachers
        teachers = teacherDao.getAllTeachers();
        List<String> teacherNames = new ArrayList<>();
        int teacherPos = 0;
        for (int i = 0; i < teachers.size(); i++) {
            teacherNames.add(teachers.get(i).fullName);
            if (teachers.get(i).id == classItem.teacherId) teacherPos = i;
        }
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);
        teacherSpinner.setSelection(teacherPos);
        
        // Load weekdays
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> weekdayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekdays);
        weekdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(weekdayAdapter);
        
        loadClassData();
        
        Button saveBtn = findViewById(R.id.saveBtn);
        Button deleteBtn = findViewById(R.id.deleteBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);
        
        saveBtn.post(() -> {
            int accentColor = Color.parseColor(themeDao.getAccentColor());
            GradientDrawable saveBg = new GradientDrawable();
            saveBg.setShape(GradientDrawable.RECTANGLE);
            saveBg.setColor(accentColor);
            saveBg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
            saveBtn.setBackground(saveBg);
        });
        
        deleteBtn.post(() -> {
            GradientDrawable deleteBg = new GradientDrawable();
            deleteBg.setShape(GradientDrawable.RECTANGLE);
            deleteBg.setColor(Color.parseColor("#EF4444"));
            deleteBg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
            deleteBtn.setBackground(deleteBg);
        });
        
        saveBtn.setOnClickListener(v -> saveClass());
        deleteBtn.setOnClickListener(v -> showDeleteDialog());
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
        classItem.moduleId = modules.get(moduleSpinner.getSelectedItemPosition()).id;
        classItem.teacherId = teachers.get(teacherSpinner.getSelectedItemPosition()).id;
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
    
    private int getAccentStyle(String color) {
        switch (color) {
            case "#6200EE": return R.style.AccentPurple;
            case "#2196F3": return R.style.AccentBlue;
            case "#10B981": return R.style.AccentGreen;
            case "#F44336": return R.style.AccentRed;
            case "#FF9800": return R.style.AccentOrange;
            default: return R.style.AccentPurple;
        }
    }
}
