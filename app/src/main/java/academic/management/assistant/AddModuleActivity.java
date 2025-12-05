package academic.management.assistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ModuleDao;
import academic.management.assistant.database.ThemeDao;
import academic.management.assistant.model.Module;

public class AddModuleActivity extends AppCompatActivity {
    
    private EditText moduleNameEdit;
    private ModuleDao moduleDao;
    
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
        setContentView(R.layout.activity_add_module);
        
        moduleDao = new ModuleDao(dbHelper);
        moduleNameEdit = findViewById(R.id.moduleNameEdit);
        
        Button saveBtn = findViewById(R.id.saveBtn);
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
        cancelBtn.setOnClickListener(v -> finish());
    }
    
    private void saveModule() {
        String name = moduleNameEdit.getText().toString().trim();
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter module name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Module module = new Module();
        module.name = name;
        moduleDao.insertModule(module);
        
        Toast.makeText(this, "Module added!", Toast.LENGTH_SHORT).show();
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
}