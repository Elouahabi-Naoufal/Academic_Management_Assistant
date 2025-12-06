package academic.management.assistant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "academic_management.db";
    private static final int DATABASE_VERSION = 7;
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE academic_year (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "year_name VARCHAR(50) NOT NULL, " +
                "start_date DATE, " +
                "end_date DATE, " +
                "is_current INTEGER DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        
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
                "is_archived INTEGER DEFAULT 0, " +
                "year_id INTEGER DEFAULT 1, " +
                "FOREIGN KEY (module_id) REFERENCES module(id), " +
                "FOREIGN KEY (teacher_id) REFERENCES teacher(id), " +
                "FOREIGN KEY (year_id) REFERENCES academic_year(id))");
        
        db.execSQL("CREATE TABLE theme (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "is_dark INTEGER DEFAULT 0, " +
                "accent_color VARCHAR(7) DEFAULT '#6200EE', " +
                "use_system_theme INTEGER DEFAULT 0, " +
                "school_name VARCHAR(255) DEFAULT 'Academic Management', " +
                "academic_year VARCHAR(50) DEFAULT '1st Year', " +
                "current_academic_year VARCHAR(50) DEFAULT '2024-2025')");
        
        // Insert default academic year
        db.execSQL("INSERT INTO academic_year (year_name, is_current) VALUES ('2024-2025', 1)");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE theme ADD COLUMN use_system_theme INTEGER DEFAULT 0");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE theme ADD COLUMN school_name VARCHAR(255) DEFAULT 'Academic Management'");
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE theme ADD COLUMN academic_year VARCHAR(50) DEFAULT '1st Year'");
        }
        if (oldVersion < 6) {
            db.execSQL("CREATE TABLE academic_year (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "year_name VARCHAR(50) NOT NULL, " +
                    "start_date DATE, " +
                    "end_date DATE, " +
                    "is_current INTEGER DEFAULT 0, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            db.execSQL("INSERT INTO academic_year (year_name, is_current) VALUES ('2024-2025', 1)");
            db.execSQL("ALTER TABLE class ADD COLUMN year_id INTEGER DEFAULT 1");
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE theme ADD COLUMN current_academic_year VARCHAR(50) DEFAULT '2024-2025'");
        }
    }
}
