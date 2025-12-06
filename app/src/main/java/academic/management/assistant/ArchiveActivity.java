package academic.management.assistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.YearDao;

public class ArchiveActivity extends AppCompatActivity {
    private CheckBox classesCheckbox, modulesCheckbox, teachersCheckbox;
    private Button archiveBtn;
    private YearDao yearDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        yearDao = new YearDao(dbHelper);
        
        classesCheckbox = findViewById(R.id.classesCheckbox);
        modulesCheckbox = findViewById(R.id.modulesCheckbox);
        teachersCheckbox = findViewById(R.id.teachersCheckbox);
        archiveBtn = findViewById(R.id.archiveBtn);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.selectAllBtn).setOnClickListener(v -> selectAll());
        
        archiveBtn.setOnClickListener(v -> performArchive());
    }
    
    private void selectAll() {
        boolean selectAll = !classesCheckbox.isChecked() || !modulesCheckbox.isChecked() || !teachersCheckbox.isChecked();
        classesCheckbox.setChecked(selectAll);
        modulesCheckbox.setChecked(selectAll);
        teachersCheckbox.setChecked(selectAll);
    }
    
    private void performArchive() {
        boolean archiveClasses = classesCheckbox.isChecked();
        boolean archiveModules = modulesCheckbox.isChecked();
        boolean archiveTeachers = teachersCheckbox.isChecked();
        
        if (!archiveClasses && !archiveModules && !archiveTeachers) {
            android.widget.Toast.makeText(this, "Please select at least one option", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        yearDao.archiveSelectedData(archiveClasses, archiveModules, archiveTeachers);
        
        String message = "Archived: ";
        if (archiveClasses) message += "Classes ";
        if (archiveModules) message += "Modules ";
        if (archiveTeachers) message += "Teachers";
        
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
        finish();
    }
}