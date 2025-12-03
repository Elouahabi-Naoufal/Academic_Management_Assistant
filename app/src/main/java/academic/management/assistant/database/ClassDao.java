package academic.management.assistant.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import academic.management.assistant.data.ClassItem;

@Dao
public interface ClassDao {
    @Query("SELECT * FROM classes ORDER BY name ASC")
    List<ClassItem> getAllClasses();
    
    @Query("SELECT * FROM classes WHERE isActive = 1 ORDER BY name ASC")
    List<ClassItem> getActiveClasses();
    
    @Query("SELECT * FROM classes WHERE semester = :semester AND year = :year ORDER BY name ASC")
    List<ClassItem> getClassesBySemester(String semester, int year);
    
    @Query("SELECT * FROM classes WHERE teacher LIKE '%' || :teacherName || '%'")
    List<ClassItem> getClassesByTeacher(String teacherName);
    
    @Query("SELECT * FROM classes WHERE name LIKE '%' || :searchTerm || '%' OR code LIKE '%' || :searchTerm || '%'")
    List<ClassItem> searchClasses(String searchTerm);
    
    @Query("SELECT SUM(credits) FROM classes WHERE isActive = 1")
    int getTotalActiveCredits();
    
    @Query("SELECT * FROM classes WHERE id = :classId")
    ClassItem getClassById(int classId);
    
    @Insert
    long insertClass(ClassItem classItem);
    
    @Insert
    void insertClasses(List<ClassItem> classes);
    
    @Update
    void updateClass(ClassItem classItem);
    
    @Update
    void updateClasses(List<ClassItem> classes);
    
    @Delete
    void deleteClass(ClassItem classItem);
    
    @Delete
    void deleteClasses(List<ClassItem> classes);
    
    @Query("DELETE FROM classes WHERE id = :classId")
    void deleteClassById(int classId);
    
    @Query("DELETE FROM classes")
    void deleteAllClasses();
    
    @Query("SELECT COUNT(*) FROM classes")
    int getClassCount();
    
    @Query("SELECT COUNT(*) FROM classes WHERE isActive = 1")
    int getActiveClassCount();
    
    @Query("UPDATE classes SET isActive = 0 WHERE id = :classId")
    void deactivateClass(int classId);
    
    @Query("UPDATE classes SET isActive = 1 WHERE id = :classId")
    void activateClass(int classId);
    
    @Query("UPDATE classes SET color = :color WHERE id = :classId")
    void updateClassColor(int classId, String color);
}