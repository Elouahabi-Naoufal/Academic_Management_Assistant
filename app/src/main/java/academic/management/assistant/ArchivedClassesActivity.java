package academic.management.assistant;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ArchiveDao;
import academic.management.assistant.adapter.ClassAdapter;
import academic.management.assistant.model.ClassItem;
import java.util.List;

public class ArchivedClassesActivity extends AppCompatActivity {
    private TextView titleText;
    private RecyclerView classesRecycler;
    private ArchiveDao archiveDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_classes);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        archiveDao = new ArchiveDao(dbHelper);
        
        titleText = findViewById(R.id.titleText);
        classesRecycler = findViewById(R.id.classesRecycler);
        
        int sessionId = getIntent().getIntExtra("session_id", -1);
        String sessionTitle = getIntent().getStringExtra("session_title");
        
        titleText.setText(sessionTitle);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        loadArchivedClasses(sessionId);
    }
    
    private void loadArchivedClasses(int sessionId) {
        List<ClassItem> classes = archiveDao.getArchivedClasses(sessionId);
        ClassAdapter adapter = new ClassAdapter(classes, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(ClassItem classItem) {
                // View only - no editing for archived classes
            }
            
            @Override
            public void onClassLongClick(ClassItem classItem) {
                // No actions for archived classes
            }
        });
        classesRecycler.setLayoutManager(new LinearLayoutManager(this));
        classesRecycler.setAdapter(adapter);
    }
}