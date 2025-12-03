package academic.management.assistant.database;

import androidx.room.TypeConverter;
import academic.management.assistant.data.*;

public class Converters {
    
    // Event.Type converters
    @TypeConverter
    public static Event.Type fromEventTypeString(String value) {
        return value == null ? null : Event.Type.valueOf(value);
    }
    
    @TypeConverter
    public static String eventTypeToString(Event.Type type) {
        return type == null ? null : type.name();
    }
    
    // Task.Priority converters
    @TypeConverter
    public static Task.Priority fromPriorityString(String value) {
        return value == null ? null : Task.Priority.valueOf(value);
    }
    
    @TypeConverter
    public static String priorityToString(Task.Priority priority) {
        return priority == null ? null : priority.name();
    }
    
    // Task.Status converters
    @TypeConverter
    public static Task.Status fromStatusString(String value) {
        return value == null ? null : Task.Status.valueOf(value);
    }
    
    @TypeConverter
    public static String statusToString(Task.Status status) {
        return status == null ? null : status.name();
    }
    
    // GradeItem.GradeType converters
    @TypeConverter
    public static GradeItem.GradeType fromGradeTypeString(String value) {
        return value == null ? null : GradeItem.GradeType.valueOf(value);
    }
    
    @TypeConverter
    public static String gradeTypeToString(GradeItem.GradeType type) {
        return type == null ? null : type.name();
    }
    
    // GradeItem.LetterGrade converters
    @TypeConverter
    public static GradeItem.LetterGrade fromLetterGradeString(String value) {
        return value == null ? null : GradeItem.LetterGrade.valueOf(value);
    }
    
    @TypeConverter
    public static String letterGradeToString(GradeItem.LetterGrade grade) {
        return grade == null ? null : grade.name();
    }
    
    // Attendance.Status converters
    @TypeConverter
    public static Attendance.Status fromAttendanceStatusString(String value) {
        return value == null ? null : Attendance.Status.valueOf(value);
    }
    
    @TypeConverter
    public static String attendanceStatusToString(Attendance.Status status) {
        return status == null ? null : status.name();
    }
    
    // StudySession.SessionType converters
    @TypeConverter
    public static StudySession.SessionType fromSessionTypeString(String value) {
        return value == null ? null : StudySession.SessionType.valueOf(value);
    }
    
    @TypeConverter
    public static String sessionTypeToString(StudySession.SessionType type) {
        return type == null ? null : type.name();
    }
    
    // Notification.NotificationType converters
    @TypeConverter
    public static Notification.NotificationType fromNotificationTypeString(String value) {
        return value == null ? null : Notification.NotificationType.valueOf(value);
    }
    
    @TypeConverter
    public static String notificationTypeToString(Notification.NotificationType type) {
        return type == null ? null : type.name();
    }
    
    // Notification.Priority converters
    @TypeConverter
    public static Notification.Priority fromNotificationPriorityString(String value) {
        return value == null ? null : Notification.Priority.valueOf(value);
    }
    
    @TypeConverter
    public static String notificationPriorityToString(Notification.Priority priority) {
        return priority == null ? null : priority.name();
    }
}