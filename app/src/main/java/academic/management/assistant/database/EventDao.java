package academic.management.assistant.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import academic.management.assistant.data.Event;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events ORDER BY timestamp ASC")
    List<Event> getAllEvents();
    
    @Query("SELECT * FROM events WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    List<Event> getEventsBetween(long startTime, long endTime);
    
    @Query("SELECT * FROM events WHERE classId = :classId ORDER BY timestamp ASC")
    List<Event> getEventsForClass(int classId);
    
    @Query("SELECT * FROM events WHERE type = :type ORDER BY timestamp ASC")
    List<Event> getEventsByType(Event.Type type);
    
    @Query("SELECT * FROM events WHERE timestamp >= :currentTime ORDER BY timestamp ASC LIMIT 1")
    Event getNextEvent(long currentTime);
    
    @Query("SELECT * FROM events WHERE DATE(timestamp/1000, 'unixepoch') = DATE(:date/1000, 'unixepoch') ORDER BY timestamp ASC")
    List<Event> getEventsForDay(long date);
    
    @Query("SELECT * FROM events WHERE isRecurring = 1")
    List<Event> getRecurringEvents();
    
    @Insert
    long insertEvent(Event event);
    
    @Insert
    void insertEvents(List<Event> events);
    
    @Update
    void updateEvent(Event event);
    
    @Update
    void updateEvents(List<Event> events);
    
    @Delete
    void deleteEvent(Event event);
    
    @Delete
    void deleteEvents(List<Event> events);
    
    @Query("DELETE FROM events WHERE id = :eventId")
    void deleteEventById(int eventId);
    
    @Query("DELETE FROM events WHERE classId = :classId")
    void deleteEventsForClass(int classId);
    
    @Query("DELETE FROM events")
    void deleteAllEvents();
    
    @Query("SELECT * FROM events WHERE id = :eventId")
    Event getEventById(int eventId);
    
    @Query("SELECT COUNT(*) FROM events")
    int getEventCount();
    
    @Query("SELECT COUNT(*) FROM events WHERE classId = :classId")
    int getEventCountForClass(int classId);
}