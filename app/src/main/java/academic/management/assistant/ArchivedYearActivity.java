package academic.management.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ModuleDao;
import academic.management.assistant.adapter.ArchivedModuleAdapter;
import academic.management.assistant.model.Module;
import java.util.List;

public class ArchivedYearActivity extends AppCompatActivity {
    private TextView yearNameText;
    private RecyclerView modulesRecycler;
    private ModuleDao moduleDao;
    private int yearId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_year);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        moduleDao = new ModuleDao(dbHelper);
        
        yearNameText = findViewById(R.id.yearNameText);
        modulesRecycler = findViewById(R.id.modulesRecycler);
        
        yearId = getIntent().getIntExtra("year_id", -1);
        String yearName = getIntent().getStringExtra("year_name");
        
        yearNameText.setText(yearName + " - Archived Modules");
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        loadArchivedModules();
    }
    
    private void loadArchivedModules() {
        List<Module> modules = moduleDao.getAllModules();
        ArchivedModuleAdapter adapter = new ArchivedModuleAdapter(modules, module -> {
            Intent intent = new Intent(this, YearDetailsActivity.class);
            intent.putExtra("module_id", module.id);
            intent.putExtra("module_name", module.name);
            intent.putExtra("year_id", yearId);
            startActivity(intent);
        });
        modulesRecycler.setLayoutManager(new LinearLayoutManager(this));
        modulesRecycler.setAdapter(adapter);
    }
}