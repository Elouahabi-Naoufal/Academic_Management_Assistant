package academic.management.assistant.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.model.Teacher;

public class TeacherDao {
    private DatabaseHelper dbHelper;
    
    public TeacherDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name, image_path FROM teacher", null);
        
        while (cursor.moveToNext()) {
            Teacher teacher = new Teacher();
            teacher.id = cursor.getInt(0);
            teacher.fullName = cursor.getString(1);
            teacher.imagePath = cursor.getString(2);
            teachers.add(teacher);
        }
        
        cursor.close();
        return teachers;
    }
    
    public long insertTeacher(Teacher teacher) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", teacher.fullName);
        values.put("image_path", teacher.imagePath);
        return db.insert("teacher", null, values);
    }
    
    public void updateTeacher(Teacher teacher) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", teacher.fullName);
        values.put("image_path", teacher.imagePath);
        db.update("teacher", values, "id = ?", new String[]{String.valueOf(teacher.id)});
    }
    
    public void deleteTeacher(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("teacher", "id = ?", new String[]{String.valueOf(id)});
    }
}
