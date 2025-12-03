package academic.management.assistant.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import academic.management.assistant.data.Attendance;

@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    List<Attendance> getAllAttendance();
    
    @Query("SELECT * FROM attendance WHERE classId = :classId ORDER BY date DESC")
    List<Attendance> getAttendanceForClass(int classId);
    
    @Query("SELECT * FROM attendance WHERE status = :status ORDER BY date DESC")
    List<Attendance> getAttendanceByStatus(Attendance.Status status);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE classId = :classId AND status = 'PRESENT'")
    int getPresentCount(int classId);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE classId = :classId AND status = 'ABSENT'")
    int getAbsentCount(int classId);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE classId = :classId AND status = 'LATE'")
    int getLateCount(int classId);
    
    @Query("SELECT (COUNT(*) * 100.0 / (SELECT COUNT(*) FROM attendance WHERE classId = :classId)) " +
           "FROM attendance WHERE classId = :classId AND status IN ('PRESENT', 'LATE')")
    double getAttendancePercentage(int classId);
    
    @Query("SELECT * FROM attendance WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    List<Attendance> getAttendanceBetween(long startDate, long endDate);
    
    @Insert
    long insertAttendance(Attendance attendance);
    
    @Insert
    void insertAttendanceRecords(List<Attendance> attendanceList);
    
    @Update
    void updateAttendance(Attendance attendance);
    
    @Update
    void updateAttendanceRecords(List<Attendance> attendanceList);
    
    @Delete
    void deleteAttendance(Attendance attendance);
    
    @Delete
    void deleteAttendanceRecords(List<Attendance> attendanceList);
    
    @Query("DELETE FROM attendance WHERE id = :attendanceId")
    void deleteAttendanceById(int attendanceId);
    
    @Query("DELETE FROM attendance WHERE classId = :classId")
    void deleteAttendanceForClass(int classId);
    
    @Query("DELETE FROM attendance")
    void deleteAllAttendance();
    
    @Query("SELECT * FROM attendance WHERE id = :attendanceId")
    Attendance getAttendanceById(int attendanceId);
    
    @Query("SELECT COUNT(*) FROM attendance")
    int getTotalAttendanceCount();
    
    @Query("UPDATE attendance SET status = :status WHERE id = :attendanceId")
    void updateAttendanceStatus(int attendanceId, Attendance.Status status);
}