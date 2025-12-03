package academic.management.assistant.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = ClassItem.class,
                                  parentColumns = "id",
                                  childColumns = "classId",
                                  onDelete = ForeignKey.SET_NULL),
        indices = {@androidx.room.Index(value = "classId")})
public class Task {
    public enum Priority { LOW, MEDIUM, HIGH, URGENT }
    public enum Status { TODO, IN_PROGRESS, COMPLETED, OVERDUE }
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String title;
    public String description;
    public long dueDate;
    public long createdDate;
    public long completedDate;
    public boolean completed;
    public Priority priority;
    public Status status;
    public Integer classId;
    public String tags;
    public int estimatedHours;
    public int actualHours;
    public String attachments;
    
    public Task(String title, String description, long dueDate, boolean completed) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.createdDate = System.currentTimeMillis();
        this.priority = Priority.MEDIUM;
        this.status = completed ? Status.COMPLETED : Status.TODO;
    }
}