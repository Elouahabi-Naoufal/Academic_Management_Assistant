package academic.management.assistant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "academic_management.db";
    private static final int DATABASE_VERSION = 11;
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE module (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(255) NOT NULL)");
        
        db.execSQL("CREATE TABLE teacher (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name VARCHAR(255) NOT NULL, " +
                "image_path VARCHAR(255))");
        
        db.execSQL("CREATE TABLE class (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR(255) NOT NULL, " +
                "module_id INTEGER NOT NULL, " +
                "teacher_id INTEGER NOT NULL, " +
                "location VARCHAR(255), " +
                "weekday INTEGER NOT NULL, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "FOREIGN KEY (module_id) REFERENCES module(id), " +
                "FOREIGN KEY (teacher_id) REFERENCES teacher(id))");
        
        db.execSQL("CREATE TABLE theme (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "is_dark INTEGER DEFAULT 0, " +
                "accent_color VARCHAR(7) DEFAULT '#6200EE', " +
                "use_system_theme INTEGER DEFAULT 0, " +
                "school_name VARCHAR(255) DEFAULT 'Academic Management', " +
                "academic_year VARCHAR(50) DEFAULT '1st Year', " +
                "current_academic_year VARCHAR(50) DEFAULT '2024-2025')");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 11) {
            db.execSQL("DROP TABLE IF EXISTS archive_session");
            db.execSQL("CREATE TEMPORARY TABLE class_backup(id, title, module_id, teacher_id, location, weekday, start_time, end_time)");
            db.execSQL("INSERT INTO class_backup SELECT id, title, module_id, teacher_id, location, weekday, start_time, end_time FROM class");
            db.execSQL("DROP TABLE class");
            db.execSQL("CREATE TABLE class (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "module_id INTEGER NOT NULL, " +
                    "teacher_id INTEGER NOT NULL, " +
                    "location VARCHAR(255), " +
                    "weekday INTEGER NOT NULL, " +
                    "start_time TEXT NOT NULL, " +
                    "end_time TEXT NOT NULL, " +
                    "FOREIGN KEY (module_id) REFERENCES module(id), " +
                    "FOREIGN KEY (teacher_id) REFERENCES teacher(id))");
            db.execSQL("INSERT INTO class SELECT * FROM class_backup");
            db.execSQL("DROP TABLE class_backup");
        }
    }
}