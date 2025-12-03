package academic.management.assistant.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import academic.management.assistant.data.Task;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    List<Task> getAllTasks();
    
    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY dueDate ASC")
    List<Task> getIncompleteTasks();
    
    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY completedDate DESC")
    List<Task> getCompletedTasks();
    
    @Query("SELECT * FROM tasks WHERE classId = :classId ORDER BY dueDate ASC")
    List<Task> getTasksForClass(int classId);
    
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    List<Task> getTasksByPriority(Task.Priority priority);
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDate ASC")
    List<Task> getTasksByStatus(Task.Status status);
    
    @Query("SELECT * FROM tasks WHERE dueDate <= :deadline AND completed = 0 ORDER BY dueDate ASC")
    List<Task> getTasksDueBefore(long deadline);
    
    @Query("SELECT * FROM tasks WHERE dueDate >= :startDate AND dueDate <= :endDate ORDER BY dueDate ASC")
    List<Task> getTasksBetween(long startDate, long endDate);
    
    @Query("SELECT COUNT(*) FROM tasks WHERE completed = 0")
    int getIncompleteTaskCount();
    
    @Query("SELECT COUNT(*) FROM tasks WHERE completed = 1")
    int getCompletedTaskCount();
    
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :searchTerm || '%' OR description LIKE '%' || :searchTerm || '%'")
    List<Task> searchTasks(String searchTerm);
    
    @Insert
    long insertTask(Task task);
    
    @Insert
    void insertTasks(List<Task> tasks);
    
    @Update
    void updateTask(Task task);
    
    @Update
    void updateTasks(List<Task> tasks);
    
    @Delete
    void deleteTask(Task task);
    
    @Delete
    void deleteTasks(List<Task> tasks);
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    void deleteTaskById(int taskId);
    
    @Query("DELETE FROM tasks WHERE classId = :classId")
    void deleteTasksForClass(int classId);
    
    @Query("DELETE FROM tasks")
    void deleteAllTasks();
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    Task getTaskById(int taskId);
    
    @Query("SELECT COUNT(*) FROM tasks")
    int getTotalTaskCount();
    
    @Query("UPDATE tasks SET completed = :completed, completedDate = :completedDate, status = :status WHERE id = :taskId")
    void markTaskCompleted(int taskId, boolean completed, long completedDate, Task.Status status);
    
    @Query("UPDATE tasks SET priority = :priority WHERE id = :taskId")
    void updateTaskPriority(int taskId, Task.Priority priority);
    
    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    void updateTaskStatus(int taskId, Task.Status status);
}