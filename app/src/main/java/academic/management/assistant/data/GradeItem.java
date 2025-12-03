package academic.management.assistant.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "grades",
        foreignKeys = @ForeignKey(entity = ClassItem.class,
                                  parentColumns = "id",
                                  childColumns = "classId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@androidx.room.Index(value = "classId")})
public class GradeItem {
    public enum GradeType { QUIZ, EXAM, MIDTERM, FINAL, ASSIGNMENT, PROJECT, PARTICIPATION, LAB }
    public enum LetterGrade { A_PLUS, A, A_MINUS, B_PLUS, B, B_MINUS, C_PLUS, C, C_MINUS, D_PLUS, D, D_MINUS, F }
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String subject;
    public String type;
    public double score;
    public double maxScore;
    public int classId; // Foreign key
    public GradeType gradeType;
    public LetterGrade letterGrade;
    public double weight; // Percentage weight in final grade
    public long dateReceived;
    public long dateEntered;
    public String notes;
    public String feedback;
    public boolean isExtraCredit;
    public boolean isDropped; // Lowest grade dropped
    
    public GradeItem(String subject, String type, double score, double maxScore) {
        this.subject = subject;
        this.type = type;
        this.score = score;
        this.maxScore = maxScore;
        this.dateEntered = System.currentTimeMillis();
        this.weight = 1.0;
        this.gradeType = GradeType.ASSIGNMENT;
    }
    
    public double getPercentage() {
        return maxScore > 0 ? (score / maxScore) * 100 : 0;
    }
    
    public double getGradePoints() {
        double percentage = getPercentage();
        if (percentage >= 97) return 4.0;
        if (percentage >= 93) return 4.0;
        if (percentage >= 90) return 3.7;
        if (percentage >= 87) return 3.3;
        if (percentage >= 83) return 3.0;
        if (percentage >= 80) return 2.7;
        if (percentage >= 77) return 2.3;
        if (percentage >= 73) return 2.0;
        if (percentage >= 70) return 1.7;
        if (percentage >= 67) return 1.3;
        if (percentage >= 65) return 1.0;
        return 0.0;
    }
}