package academic.management.assistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.ArchiveDao;
import academic.management.assistant.adapter.SelectableClassAdapter;
import academic.management.assistant.model.ClassItem;
import java.util.List;
import java.util.ArrayList;

public class SelectClassesToArchiveActivity extends AppCompatActivity {
    private RecyclerView classesRecycler;
    private Button archiveBtn;
    private EditText archiveTitleEdit;
    private ClassDao classDao;
    private ArchiveDao archiveDao;
    private SelectableClassAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_classes_archive);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        classDao = new ClassDao(dbHelper);
        archiveDao = new ArchiveDao(dbHelper);
        
        classesRecycler = findViewById(R.id.classesRecycler);
        archiveBtn = findViewById(R.id.archiveBtn);
        archiveTitleEdit = findViewById(R.id.archiveTitleEdit);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        loadClasses();
        
        archiveBtn.setOnClickListener(v -> archiveSelectedClasses());
    }
    
    private void loadClasses() {
        List<ClassItem> classes = classDao.getAllClasses();
        adapter = new SelectableClassAdapter(classes);
        classesRecycler.setLayoutManager(new LinearLayoutManager(this));
        classesRecycler.setAdapter(adapter);
    }
    
    private void archiveSelectedClasses() {
        String title = archiveTitleEdit.getText().toString().trim();
        if (title.isEmpty()) {
            android.widget.Toast.makeText(this, "Please enter archive title", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<Integer> selectedIds = adapter.getSelectedClassIds();
        if (selectedIds.isEmpty()) {
            android.widget.Toast.makeText(this, "Please select classes to archive", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        long sessionId = archiveDao.createArchiveSession(title);
        archiveDao.archiveClasses(selectedIds, sessionId);
        
        android.widget.Toast.makeText(this, selectedIds.size() + " classes archived to '" + title + "'", android.widget.Toast.LENGTH_SHORT).show();
        finish();
    }
}