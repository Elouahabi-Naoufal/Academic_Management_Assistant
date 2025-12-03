package academic.management.assistant.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import academic.management.assistant.data.GradeItem;

@Dao
public interface GradeDao {
    @Query("SELECT * FROM grades ORDER BY dateReceived DESC")
    List<GradeItem> getAllGrades();
    
    @Query("SELECT * FROM grades WHERE classId = :classId ORDER BY dateReceived DESC")
    List<GradeItem> getGradesForClass(int classId);
    
    @Query("SELECT * FROM grades WHERE gradeType = :gradeType ORDER BY dateReceived DESC")
    List<GradeItem> getGradesByType(GradeItem.GradeType gradeType);
    
    @Query("SELECT AVG((score / maxScore) * 100) FROM grades WHERE classId = :classId AND isDropped = 0")
    double getClassAverage(int classId);
    
    @Query("SELECT AVG((score / maxScore) * 100) FROM grades WHERE isDropped = 0")
    double getOverallAverage();
    
    @Query("SELECT SUM((score / maxScore) * weight) / SUM(weight) * 100 FROM grades WHERE classId = :classId AND isDropped = 0")
    double getWeightedClassAverage(int classId);
    
    @Query("SELECT AVG(gradePoints * credits) / AVG(credits) FROM (SELECT g.*, c.credits, " +
           "CASE WHEN (g.score / g.maxScore * 100) >= 97 THEN 4.0 " +
           "WHEN (g.score / g.maxScore * 100) >= 93 THEN 4.0 " +
           "WHEN (g.score / g.maxScore * 100) >= 90 THEN 3.7 " +
           "WHEN (g.score / g.maxScore * 100) >= 87 THEN 3.3 " +
           "WHEN (g.score / g.maxScore * 100) >= 83 THEN 3.0 " +
           "WHEN (g.score / g.maxScore * 100) >= 80 THEN 2.7 " +
           "WHEN (g.score / g.maxScore * 100) >= 77 THEN 2.3 " +
           "WHEN (g.score / g.maxScore * 100) >= 73 THEN 2.0 " +
           "WHEN (g.score / g.maxScore * 100) >= 70 THEN 1.7 " +
           "WHEN (g.score / g.maxScore * 100) >= 67 THEN 1.3 " +
           "WHEN (g.score / g.maxScore * 100) >= 65 THEN 1.0 " +
           "ELSE 0.0 END as gradePoints " +
           "FROM grades g JOIN classes c ON g.classId = c.id WHERE g.isDropped = 0)")
    double calculateGPA();
    
    @Query("SELECT * FROM grades WHERE (score / maxScore * 100) < :threshold ORDER BY dateReceived DESC")
    List<GradeItem> getLowGrades(double threshold);
    
    @Query("SELECT COUNT(*) FROM grades WHERE classId = :classId")
    int getGradeCountForClass(int classId);
    
    @Insert
    long insertGrade(GradeItem grade);
    
    @Insert
    void insertGrades(List<GradeItem> grades);
    
    @Update
    void updateGrade(GradeItem grade);
    
    @Update
    void updateGrades(List<GradeItem> grades);
    
    @Delete
    void deleteGrade(GradeItem grade);
    
    @Delete
    void deleteGrades(List<GradeItem> grades);
    
    @Query("DELETE FROM grades WHERE id = :gradeId")
    void deleteGradeById(int gradeId);
    
    @Query("DELETE FROM grades WHERE classId = :classId")
    void deleteGradesForClass(int classId);
    
    @Query("DELETE FROM grades")
    void deleteAllGrades();
    
    @Query("SELECT * FROM grades WHERE id = :gradeId")
    GradeItem getGradeById(int gradeId);
    
    @Query("SELECT COUNT(*) FROM grades")
    int getTotalGradeCount();
    
    @Query("UPDATE grades SET isDropped = 1 WHERE id = :gradeId")
    void dropGrade(int gradeId);
    
    @Query("UPDATE grades SET isDropped = 0 WHERE id = :gradeId")
    void restoreGrade(int gradeId);
    
    @Query("UPDATE grades SET weight = :weight WHERE id = :gradeId")
    void updateGradeWeight(int gradeId, double weight);
}