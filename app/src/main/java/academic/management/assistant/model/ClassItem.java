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

    
    public String getWeekdayName() {
        // Calendar: Sunday=1, Monday=2, Tuesday=3, Wednesday=4, Thursday=5, Friday=6, Saturday=7
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[weekday];
    }
}
