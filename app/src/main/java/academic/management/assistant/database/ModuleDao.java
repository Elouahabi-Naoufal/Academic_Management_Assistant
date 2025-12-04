package academic.management.assistant.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.model.Module;

public class ModuleDao {
    private DatabaseHelper dbHelper;
    
    public ModuleDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    public List<Module> getAllModules() {
        List<Module> modules = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name FROM module", null);
        
        while (cursor.moveToNext()) {
            Module module = new Module();
            module.id = cursor.getInt(0);
            module.name = cursor.getString(1);
            modules.add(module);
        }
        
        cursor.close();
        return modules;
    }
    
    public long insertModule(Module module) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", module.name);
        return db.insert("module", null, values);
    }
    
    public void updateModule(Module module) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", module.name);
        db.update("module", values, "id = ?", new String[]{String.valueOf(module.id)});
    }
    
    public void deleteModule(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("module", "id = ?", new String[]{String.valueOf(id)});
    }
}
