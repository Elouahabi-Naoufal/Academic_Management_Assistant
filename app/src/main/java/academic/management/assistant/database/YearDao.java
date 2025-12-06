package academic.management.assistant.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import academic.management.assistant.model.AcademicYear;

public class YearDao {
    private DatabaseHelper dbHelper;
    
    public YearDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    public List<AcademicYear> getAllYears() {
        List<AcademicYear> years = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT y.*, " +
                      "(SELECT COUNT(*) FROM class WHERE year_id = y.id) as class_count, " +
                      "0 as grade_count, 0 as assignment_count " +
                      "FROM academic_year y ORDER BY y.is_current DESC, y.created_at DESC";
        
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            AcademicYear year = new AcademicYear();
            year.id = cursor.getInt(0);
            year.yearName = cursor.getString(1);
            year.isCurrent = cursor.getInt(4) == 1;
            year.totalClasses = cursor.getInt(6);
            year.totalGrades = cursor.getInt(7);
            year.totalAssignments = cursor.getInt(8);
            years.add(year);
        }
        cursor.close();
        return years;
    }
    
    public void createNewYear(String yearName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Set all years as non-current
        ContentValues updateValues = new ContentValues();
        updateValues.put("is_current", 0);
        db.update("academic_year", updateValues, null, null);
        
        // Create new current year
        ContentValues values = new ContentValues();
        values.put("year_name", yearName);
        values.put("is_current", 1);
        db.insert("academic_year", null, values);
    }
    
    public void archiveCurrentYear() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Archive all classes from current year
        db.execSQL("UPDATE class SET is_archived = 1 WHERE year_id = (SELECT id FROM academic_year WHERE is_current = 1)");
        
        // Set current year as non-current
        ContentValues values = new ContentValues();
        values.put("is_current", 0);
        db.update("academic_year", values, "is_current = 1", null);
    }
    
    public void restoreClassesFromYear(int fromYearId, int toYearId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String query = "INSERT INTO class (title, module_id, teacher_id, location, weekday, start_time, end_time, is_archived, year_id) " +
                      "SELECT title, module_id, teacher_id, location, weekday, start_time, end_time, 0, ? " +
                      "FROM class WHERE year_id = ?";
        
        db.execSQL(query, new Object[]{toYearId, fromYearId});
    }
    
    public void restoreSelectedClasses(List<Integer> classIds, int toYearId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        for (int classId : classIds) {
            String query = "INSERT INTO class (title, module_id, teacher_id, location, weekday, start_time, end_time, is_archived, year_id) " +
                          "SELECT title, module_id, teacher_id, location, weekday, start_time, end_time, 0, ? " +
                          "FROM class WHERE id = ?";
            
            db.execSQL(query, new Object[]{toYearId, classId});
        }
    }
    
    public int getCurrentYearId() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM academic_year WHERE is_current = 1 LIMIT 1", null);
        int yearId = 1;
        if (cursor.moveToFirst()) {
            yearId = cursor.getInt(0);
        }
        cursor.close();
        return yearId;
    }
    
    public void archiveSelectedData(boolean archiveClasses, boolean archiveModules, boolean archiveTeachers) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        if (archiveClasses) {
            db.execSQL("UPDATE class SET is_archived = 1 WHERE year_id = (SELECT id FROM academic_year WHERE is_current = 1)");
        }
        
        if (archiveModules) {
            // Add archived flag to modules if needed, or create archive table
            db.execSQL("CREATE TABLE IF NOT EXISTS archived_module AS SELECT * FROM module WHERE 0");
            db.execSQL("INSERT INTO archived_module SELECT * FROM module");
        }
        
        if (archiveTeachers) {
            // Add archived flag to teachers if needed, or create archive table
            db.execSQL("CREATE TABLE IF NOT EXISTS archived_teacher AS SELECT * FROM teacher WHERE 0");
            db.execSQL("INSERT INTO archived_teacher SELECT * FROM teacher");
        }
        
        // Set current year as non-current
        ContentValues values = new ContentValues();
        values.put("is_current", 0);
        db.update("academic_year", values, "is_current = 1", null);
    }
    
    public AcademicYear getYearById(int yearId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT y.*, " +
                      "(SELECT COUNT(*) FROM class WHERE year_id = y.id) as class_count, " +
                      "0 as grade_count, 0 as assignment_count " +
                      "FROM academic_year y WHERE y.id = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(yearId)});
        AcademicYear year = null;
        
        if (cursor.moveToFirst()) {
            year = new AcademicYear();
            year.id = cursor.getInt(0);
            year.yearName = cursor.getString(1);
            year.isCurrent = cursor.getInt(4) == 1;
            year.totalClasses = cursor.getInt(6);
            year.totalGrades = cursor.getInt(7);
            year.totalAssignments = cursor.getInt(8);
        }
        cursor.close();
        return year;
    }
    
    public void updateCurrentYearName(String yearName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("year_name", yearName);
        db.update("academic_year", values, "is_current = 1", null);
    }
}