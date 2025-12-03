package academic.management.assistant.data;

public class Event {
    public enum Type { CLASS, EXAM, ASSIGNMENT, MEETING }
    
    public String title;
    public String location;
    public long timestamp;
    public Type type;
    
    public Event(String title, String location, long timestamp, Type type) {
        this.title = title;
        this.location = location;
        this.timestamp = timestamp;
        this.type = type;
    }
}