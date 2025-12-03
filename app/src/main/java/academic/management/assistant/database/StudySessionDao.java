package academic.management.assistant.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import academic.management.assistant.data.StudySession;

@Dao
public interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    List<StudySession> getAllSessions();
    
    @Query("SELECT * FROM study_sessions WHERE classId = :classId ORDER BY startTime DESC")
    List<StudySession> getSessionsForClass(int classId);
    
    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE classId = :classId")
    int getTotalStudyTimeForClass(int classId);
    
    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE startTime >= :startDate AND startTime <= :endDate")
    int getStudyTimeInPeriod(long startDate, long endDate);
    
    @Query("SELECT AVG(productivityRating) FROM study_sessions WHERE productivityRating > 0")
    double getAverageProductivity();
    
    @Insert
    long insertSession(StudySession session);
    
    @Insert
    void insertSessions(List<StudySession> sessions);
    
    @Update
    void updateSession(StudySession session);
    
    @Update
    void updateSessions(List<StudySession> sessions);
    
    @Delete
    void deleteSession(StudySession session);
    
    @Delete
    void deleteSessions(List<StudySession> sessions);
    
    @Query("DELETE FROM study_sessions WHERE id = :sessionId")
    void deleteSessionById(int sessionId);
    
    @Query("DELETE FROM study_sessions WHERE classId = :classId")
    void deleteSessionsForClass(int classId);
    
    @Query("DELETE FROM study_sessions")
    void deleteAllSessions();
    
    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    StudySession getSessionById(int sessionId);
    
    @Query("SELECT COUNT(*) FROM study_sessions")
    int getTotalSessionCount();
    
    @Query("UPDATE study_sessions SET productivityRating = :rating WHERE id = :sessionId")
    void updateProductivityRating(int sessionId, int rating);
}