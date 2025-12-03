package academic.management.assistant.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import academic.management.assistant.data.*;

@Database(
    entities = {
        Event.class,
        Task.class,
        ClassItem.class,
        GradeItem.class,
        Attendance.class,
        StudySession.class,
        Notification.class
    },
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class StudyHubDatabase extends RoomDatabase {
    
    private static volatile StudyHubDatabase INSTANCE;
    
    public abstract EventDao eventDao();
    public abstract TaskDao taskDao();
    public abstract ClassDao classDao();
    public abstract GradeDao gradeDao();
    public abstract AttendanceDao attendanceDao();
    public abstract StudySessionDao studySessionDao();
    public abstract NotificationDao notificationDao();
    
    public static StudyHubDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StudyHubDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StudyHubDatabase.class, "studyhub_database")
                            .allowMainThreadQueries() // For simplicity - use background threads in production
                            .fallbackToDestructiveMigration() // For development - recreates DB on schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}