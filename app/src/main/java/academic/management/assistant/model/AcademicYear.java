package academic.management.assistant.model;

import java.util.Date;

public class AcademicYear {
    public int id;
    public String yearName;
    public Date startDate;
    public Date endDate;
    public boolean isCurrent;
    public Date createdAt;
    public int totalClasses;
    public int totalGrades;
    public int totalAssignments;
}