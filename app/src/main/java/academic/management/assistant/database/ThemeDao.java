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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM theme");
        ContentValues values = new ContentValues();
        values.put("is_dark", isDark ? 1 : 0);
        values.put("accent_color", accentColor);
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
}
