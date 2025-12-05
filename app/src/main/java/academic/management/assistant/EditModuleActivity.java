package academic.management.assistant;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ModuleDao;
import academic.management.assistant.database.ThemeDao;
import academic.management.assistant.model.Module;
import academic.management.assistant.model.ClassItem;
import java.util.List;

public class EditModuleActivity extends AppCompatActivity {
    
    private EditText moduleNameEdit;
    private LinearLayout classesContainer;
    private ModuleDao moduleDao;
    private ClassDao classDao;
    private Module module;
    
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
        setContentView(R.layout.activity_edit_module);
        
        moduleDao = new ModuleDao(dbHelper);
        classDao = new ClassDao(dbHelper);
        
        int moduleId = getIntent().getIntExtra("MODULE_ID", -1);
        module = moduleDao.getModuleById(moduleId);
        
        if (module == null) {
            finish();
            return;
        }
        
        moduleNameEdit = findViewById(R.id.moduleNameEdit);
        classesContainer = findViewById(R.id.classesContainer);
        
        moduleNameEdit.setText(module.name);
        loadClasses();
        
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
        
        saveBtn.setOnClickListener(v -> saveModule());
        deleteBtn.setOnClickListener(v -> showDeleteDialog());
        cancelBtn.setOnClickListener(v -> finish());
    }
    
    private void loadClasses() {
        List<ClassItem> classes = classDao.getClassesByModule(module.id);
        classesContainer.removeAllViews();
        
        if (classes.isEmpty()) {
            TextView noClasses = new TextView(this);
            noClasses.setText("No classes assigned to this module");
            noClasses.setTextSize(16);
            noClasses.setTextColor(Color.parseColor("#64748B"));
            noClasses.setPadding(0, 8, 0, 8);
            classesContainer.addView(noClasses);
        } else {
            for (ClassItem classItem : classes) {
                TextView classView = new TextView(this);
                classView.setText("• " + classItem.title + " (" + classItem.getWeekdayName() + " " + classItem.startTime + ")");
                classView.setTextSize(16);
                classView.setTextColor(Color.parseColor("#64748B"));
                classView.setPadding(0, 8, 0, 8);
                classesContainer.addView(classView);
            }
        }
    }
    
    private void saveModule() {
        String name = moduleNameEdit.getText().toString().trim();
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter module name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        module.name = name;
        moduleDao.updateModule(module);
        Toast.makeText(this, "Module updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void showDeleteDialog() {
        List<ClassItem> classes = classDao.getClassesByModule(module.id);
        if (!classes.isEmpty()) {
            StringBuilder message = new StringBuilder("Cannot delete module with assigned classes:\n\n");
            for (ClassItem classItem : classes) {
                message.append("• ").append(classItem.title).append("\n");
            }
            message.append("\nPlease delete or reassign these classes first.");
            
            new android.app.AlertDialog.Builder(this)
                .setTitle("Cannot Delete Module")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Delete Module")
                .setMessage("Delete " + module.name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    moduleDao.deleteModule(module.id);
                    Toast.makeText(this, "Module deleted!", Toast.LENGTH_SHORT).show();
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