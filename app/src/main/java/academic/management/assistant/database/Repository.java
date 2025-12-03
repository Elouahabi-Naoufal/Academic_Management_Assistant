package academic.management.assistant.database;

import android.content.Context;
import java.util.List;
import academic.management.assistant.data.*;

public class Repository {
    private StudyHubDatabase database;
    private EventDao eventDao;
    private TaskDao taskDao;
    private ClassDao classDao;
    private GradeDao gradeDao;
    private AttendanceDao attendanceDao;
    private StudySessionDao studySessionDao;
    private NotificationDao notificationDao;
    
    public Repository(Context context) {
        database = StudyHubDatabase.getDatabase(context);
        eventDao = database.eventDao();
        taskDao = database.taskDao();
        classDao = database.classDao();
        gradeDao = database.gradeDao();
        attendanceDao = database.attendanceDao();
        studySessionDao = database.studySessionDao();
        notificationDao = database.notificationDao();
    }
    
    // Event CRUD operations
    public List<Event> getAllEvents() { return eventDao.getAllEvents(); }
    public Event getEventById(int id) { return eventDao.getEventById(id); }
    public Event getNextEvent() { return eventDao.getNextEvent(System.currentTimeMillis()); }
    public List<Event> getTodayEvents() { return eventDao.getEventsForDay(System.currentTimeMillis()); }
    public List<Event> getEventsForClass(int classId) { return eventDao.getEventsForClass(classId); }
    public long insertEvent(Event event) { return eventDao.insertEvent(event); }
    public void insertEvents(List<Event> events) { eventDao.insertEvents(events); }
    public void updateEvent(Event event) { eventDao.updateEvent(event); }
    public void updateEvents(List<Event> events) { eventDao.updateEvents(events); }
    public void deleteEvent(Event event) { eventDao.deleteEvent(event); }
    public void deleteEventById(int id) { eventDao.deleteEventById(id); }
    public void deleteEventsForClass(int classId) { eventDao.deleteEventsForClass(classId); }
    public void deleteAllEvents() { eventDao.deleteAllEvents(); }
    public int getEventCount() { return eventDao.getEventCount(); }
    
    // Task CRUD operations
    public List<Task> getAllTasks() { return taskDao.getAllTasks(); }
    public Task getTaskById(int id) { return taskDao.getTaskById(id); }
    public List<Task> getIncompleteTasks() { return taskDao.getIncompleteTasks(); }
    public List<Task> getCompletedTasks() { return taskDao.getCompletedTasks(); }
    public List<Task> getTasksForClass(int classId) { return taskDao.getTasksForClass(classId); }
    public int getCompletedTaskCount() { return taskDao.getCompletedTaskCount(); }
    public int getIncompleteTaskCount() { return taskDao.getIncompleteTaskCount(); }
    public long insertTask(Task task) { return taskDao.insertTask(task); }
    public void insertTasks(List<Task> tasks) { taskDao.insertTasks(tasks); }
    public void updateTask(Task task) { taskDao.updateTask(task); }
    public void updateTasks(List<Task> tasks) { taskDao.updateTasks(tasks); }
    public void deleteTask(Task task) { taskDao.deleteTask(task); }
    public void deleteTaskById(int id) { taskDao.deleteTaskById(id); }
    public void deleteTasksForClass(int classId) { taskDao.deleteTasksForClass(classId); }
    public void deleteAllTasks() { taskDao.deleteAllTasks(); }
    public void markTaskCompleted(int taskId, boolean completed) { 
        taskDao.markTaskCompleted(taskId, completed, System.currentTimeMillis(), 
            completed ? Task.Status.COMPLETED : Task.Status.TODO); 
    }
    
    // Class CRUD operations
    public List<ClassItem> getAllClasses() { return classDao.getAllClasses(); }
    public ClassItem getClassById(int id) { return classDao.getClassById(id); }
    public List<ClassItem> getActiveClasses() { return classDao.getActiveClasses(); }
    public int getActiveClassCount() { return classDao.getActiveClassCount(); }
    public long insertClass(ClassItem classItem) { return classDao.insertClass(classItem); }
    public void insertClasses(List<ClassItem> classes) { classDao.insertClasses(classes); }
    public void updateClass(ClassItem classItem) { classDao.updateClass(classItem); }
    public void updateClasses(List<ClassItem> classes) { classDao.updateClasses(classes); }
    public void deleteClass(ClassItem classItem) { classDao.deleteClass(classItem); }
    public void deleteClassById(int id) { classDao.deleteClassById(id); }
    public void deleteAllClasses() { classDao.deleteAllClasses(); }
    public void deactivateClass(int classId) { classDao.deactivateClass(classId); }
    public void activateClass(int classId) { classDao.activateClass(classId); }
    
    // Grade CRUD operations
    public List<GradeItem> getAllGrades() { return gradeDao.getAllGrades(); }
    public GradeItem getGradeById(int id) { return gradeDao.getGradeById(id); }
    public List<GradeItem> getGradesForClass(int classId) { return gradeDao.getGradesForClass(classId); }
    public double calculateGPA() { return gradeDao.calculateGPA(); }
    public double getClassAverage(int classId) { return gradeDao.getClassAverage(classId); }
    public long insertGrade(GradeItem grade) { return gradeDao.insertGrade(grade); }
    public void insertGrades(List<GradeItem> grades) { gradeDao.insertGrades(grades); }
    public void updateGrade(GradeItem grade) { gradeDao.updateGrade(grade); }
    public void updateGrades(List<GradeItem> grades) { gradeDao.updateGrades(grades); }
    public void deleteGrade(GradeItem grade) { gradeDao.deleteGrade(grade); }
    public void deleteGradeById(int id) { gradeDao.deleteGradeById(id); }
    public void deleteGradesForClass(int classId) { gradeDao.deleteGradesForClass(classId); }
    public void deleteAllGrades() { gradeDao.deleteAllGrades(); }
    
    // Attendance CRUD operations
    public List<Attendance> getAllAttendance() { return attendanceDao.getAllAttendance(); }
    public Attendance getAttendanceById(int id) { return attendanceDao.getAttendanceById(id); }
    public List<Attendance> getAttendanceForClass(int classId) { return attendanceDao.getAttendanceForClass(classId); }
    public double getAttendancePercentage(int classId) { return attendanceDao.getAttendancePercentage(classId); }
    public long insertAttendance(Attendance attendance) { return attendanceDao.insertAttendance(attendance); }
    public void updateAttendance(Attendance attendance) { attendanceDao.updateAttendance(attendance); }
    public void deleteAttendance(Attendance attendance) { attendanceDao.deleteAttendance(attendance); }
    public void deleteAttendanceById(int id) { attendanceDao.deleteAttendanceById(id); }
    
    // Study Session CRUD operations
    public List<StudySession> getAllSessions() { return studySessionDao.getAllSessions(); }
    public StudySession getSessionById(int id) { return studySessionDao.getSessionById(id); }
    public List<StudySession> getSessionsForClass(int classId) { return studySessionDao.getSessionsForClass(classId); }
    public long insertSession(StudySession session) { return studySessionDao.insertSession(session); }
    public void updateSession(StudySession session) { studySessionDao.updateSession(session); }
    public void deleteSession(StudySession session) { studySessionDao.deleteSession(session); }
    public void deleteSessionById(int id) { studySessionDao.deleteSessionById(id); }
    
    // Notification CRUD operations
    public List<Notification> getAllNotifications() { return notificationDao.getAllNotifications(); }
    public Notification getNotificationById(int id) { return notificationDao.getNotificationById(id); }
    public List<Notification> getUnreadNotifications() { return notificationDao.getUnreadNotifications(); }
    public int getUnreadNotificationCount() { return notificationDao.getUnreadNotificationCount(); }
    public long insertNotification(Notification notification) { return notificationDao.insertNotification(notification); }
    public void updateNotification(Notification notification) { notificationDao.updateNotification(notification); }
    public void deleteNotification(Notification notification) { notificationDao.deleteNotification(notification); }
    public void deleteNotificationById(int id) { notificationDao.deleteNotificationById(id); }
    public void markNotificationAsRead(int id) { notificationDao.markAsRead(id); }
    public void markAllNotificationsAsRead() { notificationDao.markAllAsRead(); }
    
    // Analytics & Statistics
    public int getTotalStudyTime() {
        long weekStart = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
        return studySessionDao.getStudyTimeInPeriod(weekStart, System.currentTimeMillis());
    }
    
    public int getTotalStudyTimeForClass(int classId) {
        return studySessionDao.getTotalStudyTimeForClass(classId);
    }
    
    public double getAverageProductivity() {
        return studySessionDao.getAverageProductivity();
    }
}