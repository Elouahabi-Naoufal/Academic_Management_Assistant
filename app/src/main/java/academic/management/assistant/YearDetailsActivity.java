package academic.management.assistant;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.YearDao;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.model.AcademicYear;
import academic.management.assistant.model.ClassItem;
import academic.management.assistant.adapter.ClassAdapter;
import java.util.List;

public class YearDetailsActivity extends AppCompatActivity implements ClassAdapter.OnClassClickListener {
    private TextView yearNameText, statsText;
    private RecyclerView classesRecycler;
    private YearDao yearDao;
    private ClassDao classDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_details);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        yearDao = new YearDao(dbHelper);
        classDao = new ClassDao(dbHelper);
        
        yearNameText = findViewById(R.id.yearNameText);
        statsText = findViewById(R.id.statsText);
        classesRecycler = findViewById(R.id.classesRecycler);
        
        int yearId = getIntent().getIntExtra("year_id", -1);
        if (yearId != -1) {
            loadYearDetails(yearId);
        }
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
    
    private void loadYearDetails(int yearId) {
        AcademicYear year = yearDao.getYearById(yearId);
        if (year != null) {
            yearNameText.setText(year.yearName);
            statsText.setText(String.format("Classes: %d • Modules: %d", 
                year.totalClasses, year.totalModules));
            
            // Load classes for this year
            List<ClassItem> classes = getClassesByYear(yearId);
            ClassAdapter adapter = new ClassAdapter(classes, this);
            classesRecycler.setLayoutManager(new LinearLayoutManager(this));
            classesRecycler.setAdapter(adapter);
        }
    }
    
    private List<ClassItem> getClassesByYear(int yearId) {
        return new java.util.ArrayList<>();
    }
    
    @Override
    public void onClassClick(ClassItem classItem) {
        // Handle class click - could open class details
    }
    
    @Override
    public void onClassLongClick(ClassItem classItem) {
        // Handle class long click - could show options
    }
}