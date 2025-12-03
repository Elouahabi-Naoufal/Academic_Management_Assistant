package academic.management.assistant.data;

public class Task {
    public String title;
    public String description;
    public long dueDate;
    public boolean completed;
    
    public Task(String title, String description, long dueDate, boolean completed) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
    }
}