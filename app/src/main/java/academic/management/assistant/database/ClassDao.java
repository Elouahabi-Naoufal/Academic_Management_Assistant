package academic.management.assistant.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.model.ClassItem;

public class ClassDao {
    private DatabaseHelper dbHelper;
    
    public ClassDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    public List<ClassItem> getAllClasses() {
        List<ClassItem> classes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT c.id, c.title, c.module_id, m.name as module_name, " +
                      "c.teacher_id, t.full_name as teacher_name, c.location, " +
                      "c.weekday, c.start_time, c.end_time, c.is_archived " +
                      "FROM class c " +
                      "LEFT JOIN module m ON c.module_id = m.id " +
                      "LEFT JOIN teacher t ON c.teacher_id = t.id " +
                      "WHERE c.is_archived = 0 " +
                      "ORDER BY c.weekday, c.start_time";
        
        Cursor cursor = db.rawQuery(query, null);
        
        while (cursor.moveToNext()) {
            ClassItem item = new ClassItem();
            item.id = cursor.getInt(0);
            item.title = cursor.getString(1);
            item.moduleId = cursor.getInt(2);
            item.moduleName = cursor.getString(3);
            item.teacherId = cursor.getInt(4);
            item.teacherName = cursor.getString(5);
            item.location = cursor.getString(6);
            item.weekday = cursor.getInt(7);
            item.startTime = cursor.getString(8);
            item.endTime = cursor.getString(9);
            item.isArchived = cursor.getInt(10) == 1;
            classes.add(item);
        }
        
        cursor.close();
        return classes;
    }
    
    public long insertClass(ClassItem classItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", classItem.title);
        values.put("module_id", classItem.moduleId);
        values.put("teacher_id", classItem.teacherId);
        values.put("location", classItem.location);
        values.put("weekday", classItem.weekday);
        values.put("start_time", classItem.startTime);
        values.put("end_time", classItem.endTime);
        values.put("is_archived", classItem.isArchived ? 1 : 0);
        return db.insert("class", null, values);
    }
    
    public ClassItem getClassById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT c.id, c.title, c.module_id, m.name as module_name, " +
                      "c.teacher_id, t.full_name as teacher_name, c.location, " +
                      "c.weekday, c.start_time, c.end_time, c.is_archived " +
                      "FROM class c " +
                      "LEFT JOIN module m ON c.module_id = m.id " +
                      "LEFT JOIN teacher t ON c.teacher_id = t.id " +
                      "WHERE c.id = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        
        ClassItem item = null;
        if (cursor.moveToFirst()) {
            item = new ClassItem();
            item.id = cursor.getInt(0);
            item.title = cursor.getString(1);
            item.moduleId = cursor.getInt(2);
            item.moduleName = cursor.getString(3);
            item.teacherId = cursor.getInt(4);
            item.teacherName = cursor.getString(5);
            item.location = cursor.getString(6);
            item.weekday = cursor.getInt(7);
            item.startTime = cursor.getString(8);
            item.endTime = cursor.getString(9);
            item.isArchived = cursor.getInt(10) == 1;
        }
        
        cursor.close();
        return item;
    }
    
    public void updateClass(ClassItem classItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", classItem.title);
        values.put("module_id", classItem.moduleId);
        values.put("teacher_id", classItem.teacherId);
        values.put("location", classItem.location);
        values.put("weekday", classItem.weekday);
        values.put("start_time", classItem.startTime);
        values.put("end_time", classItem.endTime);
        values.put("is_archived", classItem.isArchived ? 1 : 0);
        db.update("class", values, "id = ?", new String[]{String.valueOf(classItem.id)});
    }
    
    public void deleteClass(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("class", "id = ?", new String[]{String.valueOf(id)});
    }
}
