package academic.management.assistant.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_sessions",
        foreignKeys = @ForeignKey(entity = ClassItem.class,
                                  parentColumns = "id",
                                  childColumns = "classId",
                                  onDelete = ForeignKey.SET_NULL),
        indices = {@androidx.room.Index(value = "classId")})
public class StudySession {
    public enum SessionType { INDIVIDUAL, GROUP, TUTORING, REVIEW }
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public Integer classId;
    public long startTime;
    public long endTime;
    public int durationMinutes;
    public SessionType type;
    public String location;
    public String topic;
    public String notes;
    public int productivityRating; // 1-5 scale
    public String participants; // JSON array for group sessions
    public String materials; // Books, notes, etc.
    
    public StudySession(long startTime, long endTime, SessionType type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.durationMinutes = (int) ((endTime - startTime) / 60000);
    }
}