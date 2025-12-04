package academic.management.assistant.model;

public class ClassItem {
    public int id;
    public String title;
    public int moduleId;
    public String moduleName;
    public int teacherId;
    public String teacherName;
    public String location;
    public int weekday;
    public String startTime;
    public String endTime;
    public boolean isArchived;
    
    public String getWeekdayName() {
        String[] days = {"", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        return days[weekday];
    }
}
