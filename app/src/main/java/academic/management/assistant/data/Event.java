package academic.management.assistant.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "events",
        foreignKeys = @ForeignKey(entity = ClassItem.class,
                                  parentColumns = "id",
                                  childColumns = "classId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@androidx.room.Index(value = "classId")})
public class Event {
    public enum Type { CLASS, EXAM, ASSIGNMENT, MEETING }
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String title;
    public String location;
    public long timestamp;
    public long endTimestamp;
    public Type type;
    public int classId; // Foreign key to ClassItem
    public String description;
    public boolean isRecurring;
    public String recurrencePattern; // "WEEKLY", "DAILY", etc.
    
    public Event(String title, String location, long timestamp, Type type) {
        this.title = title;
        this.location = location;
        this.timestamp = timestamp;
        this.type = type;
    }
}