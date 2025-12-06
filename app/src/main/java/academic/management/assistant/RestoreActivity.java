package academic.management.assistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.YearDao;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.adapter.RestoreClassAdapter;
import academic.management.assistant.model.ClassItem;
import java.util.List;
import java.util.ArrayList;

public class RestoreActivity extends AppCompatActivity {
    private TextView yearNameText;
    private RecyclerView classesRecycler;
    private Button restoreAllBtn, restoreSelectedBtn;
    private CheckBox selectAllCheckbox;
    private YearDao yearDao;
    private ClassDao classDao;
    private RestoreClassAdapter adapter;
    private int sourceYearId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        yearDao = new YearDao(dbHelper);
        classDao = new ClassDao(dbHelper);
        
        yearNameText = findViewById(R.id.yearNameText);
        classesRecycler = findViewById(R.id.classesRecycler);
        restoreAllBtn = findViewById(R.id.restoreAllBtn);
        restoreSelectedBtn = findViewById(R.id.restoreSelectedBtn);
        selectAllCheckbox = findViewById(R.id.selectAllCheckbox);
        
        sourceYearId = getIntent().getIntExtra("year_id", -1);
        String yearName = getIntent().getStringExtra("year_name");
        
        yearNameText.setText("Restore from: " + yearName);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        loadClasses();
        setupButtons();
    }
    
    private void loadClasses() {
        List<ClassItem> classes = classDao.getClassesByYear(sourceYearId);
        adapter = new RestoreClassAdapter(classes);
        classesRecycler.setLayoutManager(new LinearLayoutManager(this));
        classesRecycler.setAdapter(adapter);
        
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });
    }
    
    private void setupButtons() {
        restoreAllBtn.setOnClickListener(v -> {
            int currentYearId = yearDao.getCurrentYearId();
            yearDao.restoreClassesFromYear(sourceYearId, currentYearId);
            android.widget.Toast.makeText(this, "All classes restored", android.widget.Toast.LENGTH_SHORT).show();
            finish();
        });
        
        restoreSelectedBtn.setOnClickListener(v -> {
            List<Integer> selectedIds = adapter.getSelectedClassIds();
            if (!selectedIds.isEmpty()) {
                int currentYearId = yearDao.getCurrentYearId();
                yearDao.restoreSelectedClasses(selectedIds, currentYearId);
                android.widget.Toast.makeText(this, selectedIds.size() + " classes restored", android.widget.Toast.LENGTH_SHORT).show();
                finish();
            } else {
                android.widget.Toast.makeText(this, "No classes selected", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}