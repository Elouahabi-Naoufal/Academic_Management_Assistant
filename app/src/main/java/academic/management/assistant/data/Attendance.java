package academic.management.assistant.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "attendance",
        foreignKeys = @ForeignKey(entity = ClassItem.class,
                                  parentColumns = "id",
                                  childColumns = "classId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@androidx.room.Index(value = "classId")})
public class Attendance {
    public enum Status { PRESENT, ABSENT, LATE, EXCUSED }
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int classId;
    public long date;
    public Status status;
    public String notes;
    public int minutesLate;
    
    public Attendance(int classId, long date, Status status) {
        this.classId = classId;
        this.date = date;
        this.status = status;
    }
}