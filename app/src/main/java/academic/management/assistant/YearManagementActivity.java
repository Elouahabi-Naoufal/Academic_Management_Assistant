package academic.management.assistant;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.YearDao;
import academic.management.assistant.adapter.YearAdapter;
import academic.management.assistant.model.AcademicYear;
import java.util.List;

public class YearManagementActivity extends AppCompatActivity {
    private RecyclerView yearsRecycler;
    private YearDao yearDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_management);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        yearDao = new YearDao(dbHelper);
        
        yearsRecycler = findViewById(R.id.yearsRecycler);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        loadYears();
    }
    
    private void showRestoreDialog(AcademicYear year) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Restore from " + year.yearName)
                .setMessage("Choose restore option:")
                .setPositiveButton("Restore All Classes", (dialog, which) -> {
                    yearDao.restoreClassesFromYear(year.id, yearDao.getCurrentYearId());
                    android.widget.Toast.makeText(this, "All classes restored", android.widget.Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Select Classes", (dialog, which) -> {
                    Intent intent = new Intent(this, RestoreActivity.class);
                    intent.putExtra("year_id", year.id);
                    intent.putExtra("year_name", year.yearName);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void loadYears() {
        List<AcademicYear> years = yearDao.getAllYears();
        YearAdapter adapter = new YearAdapter(years, year -> {
            Intent intent = new Intent(this, YearDetailsActivity.class);
            intent.putExtra("year_id", year.id);
            startActivity(intent);
        }, year -> {
            // Long click for restore options
            if (!year.isCurrent) {
                showRestoreDialog(year);
            }
        });
        yearsRecycler.setLayoutManager(new LinearLayoutManager(this));
        yearsRecycler.setAdapter(adapter);
    }
}