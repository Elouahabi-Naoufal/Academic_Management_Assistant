package academic.management.assistant.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {
    public enum NotificationType { ASSIGNMENT_DUE, CLASS_STARTING, EXAM_REMINDER, GRADE_POSTED, CUSTOM }
    public enum Priority { LOW, NORMAL, HIGH, URGENT }
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String title;
    public String message;
    public NotificationType type;
    public Priority priority;
    public long scheduledTime;
    public long createdTime;
    public boolean isRead;
    public boolean isDelivered;
    public String actionData; // JSON for action buttons
    public int relatedItemId; // ID of related task, event, etc.
    public String relatedItemType; // "task", "event", "grade"
    
    public Notification(String title, String message, NotificationType type, long scheduledTime) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.scheduledTime = scheduledTime;
        this.createdTime = System.currentTimeMillis();
        this.priority = Priority.NORMAL;
    }
}