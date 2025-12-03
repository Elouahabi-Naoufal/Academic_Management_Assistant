package academic.management.assistant.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import academic.management.assistant.data.Notification;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY scheduledTime DESC")
    List<Notification> getAllNotifications();
    
    @Query("SELECT * FROM notifications WHERE isRead = 0 ORDER BY scheduledTime DESC")
    List<Notification> getUnreadNotifications();
    
    @Query("SELECT * FROM notifications WHERE scheduledTime <= :currentTime AND isDelivered = 0")
    List<Notification> getPendingNotifications(long currentTime);
    
    @Query("SELECT * FROM notifications WHERE type = :type ORDER BY scheduledTime DESC")
    List<Notification> getNotificationsByType(Notification.NotificationType type);
    
    @Insert
    long insertNotification(Notification notification);
    
    @Insert
    void insertNotifications(List<Notification> notifications);
    
    @Update
    void updateNotification(Notification notification);
    
    @Update
    void updateNotifications(List<Notification> notifications);
    
    @Delete
    void deleteNotification(Notification notification);
    
    @Delete
    void deleteNotifications(List<Notification> notifications);
    
    @Query("DELETE FROM notifications WHERE id = :notificationId")
    void deleteNotificationById(int notificationId);
    
    @Query("DELETE FROM notifications WHERE isRead = 1")
    void deleteReadNotifications();
    
    @Query("DELETE FROM notifications")
    void deleteAllNotifications();
    
    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    Notification getNotificationById(int notificationId);
    
    @Query("SELECT COUNT(*) FROM notifications")
    int getTotalNotificationCount();
    
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    int getUnreadNotificationCount();
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);
    
    @Query("UPDATE notifications SET isDelivered = 1 WHERE id = :notificationId")
    void markAsDelivered(int notificationId);
    
    @Query("UPDATE notifications SET isRead = 1")
    void markAllAsRead();
}