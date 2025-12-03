package academic.management.assistant.data;

public class GradeItem {
    public String subject;
    public String type;
    public double score;
    public double maxScore;
    
    public GradeItem(String subject, String type, double score, double maxScore) {
        this.subject = subject;
        this.type = type;
        this.score = score;
        this.maxScore = maxScore;
    }
    
    public double getPercentage() {
        return (score / maxScore) * 100;
    }
}