package academic.management.assistant.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "classes")
public class ClassItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String code; // CS101, MATH201, etc.
    public String teacher;
    public String teacherEmail;
    public String room;
    public String building;
    public String schedule; // JSON for complex schedules
    public int credits;
    public String semester;
    public int year;
    public String color; // Hex color for UI
    public String description;
    public String syllabus; // File path or URL
    public boolean isActive;
    public Long startDate; // Nullable
    public Long endDate;   // Nullable
    
    public ClassItem(String name, String teacher, String room, String schedule) {
        this.name = name;
        this.teacher = teacher;
        this.room = room;
        this.schedule = schedule;
        this.isActive = true;
        this.year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    }
}