package academic.management.assistant.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.model.ArchiveSession;
import academic.management.assistant.model.ClassItem;

public class ArchiveDao {
    private DatabaseHelper dbHelper;
    
    public ArchiveDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    public long createArchiveSession(String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        return db.insert("archive_session", null, values);
    }
    
    public void archiveClasses(List<Integer> classIds, long sessionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int classId : classIds) {
            ContentValues values = new ContentValues();
            values.put("archive_session_id", sessionId);
            db.update("class", values, "id = ?", new String[]{String.valueOf(classId)});
        }
    }
    
    public List<ArchiveSession> getAllArchiveSessions() {
        List<ArchiveSession> sessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT a.*, COUNT(c.id) as class_count " +
                      "FROM archive_session a " +
                      "LEFT JOIN class c ON a.id = c.archive_session_id " +
                      "GROUP BY a.id " +
                      "ORDER BY a.created_at DESC";
        
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            ArchiveSession session = new ArchiveSession();
            session.id = cursor.getInt(0);
            session.title = cursor.getString(1);
            session.createdAt = cursor.getString(2);
            session.classCount = cursor.getInt(3);
            sessions.add(session);
        }
        cursor.close();
        return sessions;
    }
    
    public List<ClassItem> getArchivedClasses(int sessionId) {
        List<ClassItem> classes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT c.*, m.name as module_name, t.full_name as teacher_name " +
                      "FROM class c " +
                      "JOIN module m ON c.module_id = m.id " +
                      "JOIN teacher t ON c.teacher_id = t.id " +
                      "WHERE c.archive_session_id = ? " +
                      "ORDER BY c.weekday, c.start_time";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sessionId)});
        while (cursor.moveToNext()) {
            ClassItem item = new ClassItem();
            item.id = cursor.getInt(0);
            item.title = cursor.getString(1);
            item.moduleId = cursor.getInt(2);
            item.teacherId = cursor.getInt(3);
            item.location = cursor.getString(4);
            item.weekday = cursor.getInt(5);
            item.startTime = cursor.getString(6);
            item.endTime = cursor.getString(7);
            item.moduleName = cursor.getString(8);
            item.teacherName = cursor.getString(9);
            classes.add(item);
        }
        cursor.close();
        return classes;
    }
    
    public void deleteArchiveSession(int sessionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("class", "archive_session_id = ?", new String[]{String.valueOf(sessionId)});
        db.delete("archive_session", "id = ?", new String[]{String.valueOf(sessionId)});
    }
}