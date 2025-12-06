package academic.management.assistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
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

public class AddClassActivity extends AppCompatActivity {
    
    private EditText titleEdit, locationEdit, startTimeEdit, endTimeEdit;
    private Spinner weekdaySpinner, moduleSpinner, teacherSpinner;
    private ClassDao classDao;
    private List<academic.management.assistant.model.Module> modules;
    private List<academic.management.assistant.model.Teacher> teachers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        int nightMode;
        if (themeDao.useSystemTheme()) {
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else {
            nightMode = themeDao.isDarkTheme() ? 
                AppCompatDelegate.MODE_NIGHT_YES : 
                AppCompatDelegate.MODE_NIGHT_NO;
        }
        getDelegate().setLocalNightMode(nightMode);
        
        getTheme().applyStyle(getAccentStyle(themeDao.getAccentColor()), true);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        
        setupTopBar(themeDao);
        
        classDao = new ClassDao(dbHelper);
        ModuleDao moduleDao = new ModuleDao(dbHelper);
        TeacherDao teacherDao = new TeacherDao(dbHelper);
        
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
        for (academic.management.assistant.model.Module m : modules) {
            moduleNames.add(m.name);
        }
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moduleNames);
        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moduleSpinner.setAdapter(moduleAdapter);
        
        // Load teachers
        teachers = teacherDao.getAllTeachers();
        List<String> teacherNames = new ArrayList<>();
        for (academic.management.assistant.model.Teacher t : teachers) {
            teacherNames.add(t.fullName);
        }
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);
        
        // Load weekdays
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> weekdayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekdays);
        weekdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(weekdayAdapter);
        
        Button saveBtn = findViewById(R.id.saveBtn);
        int accentColor = Color.parseColor(themeDao.getAccentColor());
        GradientDrawable saveBg = new GradientDrawable();
        saveBg.setShape(GradientDrawable.RECTANGLE);
        saveBg.setColor(accentColor);
        saveBg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        saveBtn.setBackground(saveBg);
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
        
        if (modules.isEmpty() || teachers.isEmpty()) {
            Toast.makeText(this, "Please add modules and teachers first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ClassItem classItem = new ClassItem();
        classItem.title = title;
        classItem.moduleId = modules.get(moduleSpinner.getSelectedItemPosition()).id;
        classItem.teacherId = teachers.get(teacherSpinner.getSelectedItemPosition()).id;
        classItem.location = location;
        // Spinner: Monday=0, Tuesday=1, Wednesday=2, Thursday=3, Friday=4, Saturday=5, Sunday=6
        // Calendar: Sunday=1, Monday=2, Tuesday=3, Wednesday=4, Thursday=5, Friday=6, Saturday=7
        int spinnerPos = weekdaySpinner.getSelectedItemPosition();
        if (spinnerPos == 6) {
            classItem.weekday = 1; // Sunday
        } else {
            classItem.weekday = spinnerPos + 2; // Monday=2, Tuesday=3, etc.
        }
        classItem.startTime = startTime;
        classItem.endTime = endTime;
        classItem.isArchived = false;
        
        classDao.insertClass(classItem);
        Toast.makeText(this, "Class added!", Toast.LENGTH_SHORT).show();
        finish();
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
    
    private void setupTopBar(ThemeDao themeDao) {
        android.widget.LinearLayout topBar = findViewById(R.id.topBar);
        android.widget.TextView schoolNameText = findViewById(R.id.schoolNameText);
        
        int accentColor = Color.parseColor(themeDao.getAccentColor());
        GradientDrawable topBarBg = new GradientDrawable();
        topBarBg.setShape(GradientDrawable.RECTANGLE);
        topBarBg.setColor(accentColor);
        topBar.setBackground(topBarBg);
        
        schoolNameText.setText(themeDao.getSchoolName());
    }
}
