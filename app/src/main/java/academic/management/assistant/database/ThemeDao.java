package academic.management.assistant.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ThemeDao {
    private DatabaseHelper dbHelper;
    
    public ThemeDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    public void saveTheme(boolean isDark, String accentColor) {
        saveTheme(isDark, accentColor, false);
    }
    
    public void saveTheme(boolean isDark, String accentColor, boolean useSystemTheme) {
        saveTheme(isDark, accentColor, useSystemTheme, getSchoolName());
    }
    
    public void saveTheme(boolean isDark, String accentColor, boolean useSystemTheme, String schoolName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM theme");
        ContentValues values = new ContentValues();
        values.put("is_dark", isDark ? 1 : 0);
        values.put("accent_color", accentColor);
        values.put("use_system_theme", useSystemTheme ? 1 : 0);
        values.put("school_name", schoolName);
        db.insert("theme", null, values);
    }
    
    public boolean isDarkTheme() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT is_dark FROM theme LIMIT 1", null);
        boolean isDark = false;
        if (cursor.moveToFirst()) {
            isDark = cursor.getInt(0) == 1;
        }
        cursor.close();
        return isDark;
    }
    
    public String getAccentColor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT accent_color FROM theme LIMIT 1", null);
        String color = "#6200EE";
        if (cursor.moveToFirst()) {
            color = cursor.getString(0);
        }
        cursor.close();
        return color;
    }
    
    public boolean useSystemTheme() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT use_system_theme FROM theme LIMIT 1", null);
        boolean useSystem = false;
        if (cursor.moveToFirst()) {
            useSystem = cursor.getInt(0) == 1;
        }
        cursor.close();
        return useSystem;
    }
    
    public String getSchoolName() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT school_name FROM theme LIMIT 1", null);
        String schoolName = "Academic Management";
        if (cursor.moveToFirst()) {
            schoolName = cursor.getString(0);
        }
        cursor.close();
        return schoolName;
    }
    
    public void saveSchoolName(String schoolName) {
        saveTheme(isDarkTheme(), getAccentColor(), useSystemTheme(), schoolName);
    }
    
    public String getAcademicYear() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT academic_year FROM theme LIMIT 1", null);
        String academicYear = "1st Year";
        if (cursor.moveToFirst()) {
            academicYear = cursor.getString(0);
        }
        cursor.close();
        return academicYear;
    }
    
    public String getCurrentAcademicYearName() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT current_academic_year FROM theme LIMIT 1", null);
        String yearName = "2024-2025";
        if (cursor.moveToFirst()) {
            yearName = cursor.getString(0);
        }
        cursor.close();
        return yearName;
    }
    
    public void saveAcademicYear(String academicYear) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("academic_year", academicYear);
        db.update("theme", values, null, null);
    }
    
    public void saveCurrentAcademicYearName(String yearName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Update theme table only
        ContentValues themeValues = new ContentValues();
        themeValues.put("current_academic_year", yearName);
        db.update("theme", themeValues, null, null);
    }
}
