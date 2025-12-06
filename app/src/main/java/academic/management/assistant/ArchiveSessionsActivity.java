package academic.management.assistant;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ArchiveDao;
import academic.management.assistant.adapter.ArchiveSessionAdapter;
import academic.management.assistant.model.ArchiveSession;
import java.util.List;

public class ArchiveSessionsActivity extends AppCompatActivity {
    private RecyclerView sessionsRecycler;
    private ArchiveDao archiveDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_sessions);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        archiveDao = new ArchiveDao(dbHelper);
        
        sessionsRecycler = findViewById(R.id.sessionsRecycler);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        loadArchiveSessions();
    }
    
    private void loadArchiveSessions() {
        List<ArchiveSession> sessions = archiveDao.getAllArchiveSessions();
        ArchiveSessionAdapter adapter = new ArchiveSessionAdapter(sessions, session -> {
            Intent intent = new Intent(this, ArchivedClassesActivity.class);
            intent.putExtra("session_id", session.id);
            intent.putExtra("session_title", session.title);
            startActivity(intent);
        });
        sessionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        sessionsRecycler.setAdapter(adapter);
    }
}