package societino.com.societinof;

public class ToDos {
    private String id,schedule,event,cleaning;

    public ToDos() {
    }

    public ToDos(String id, String schedule, String event, String cleaning) {
        this.id = id;
        this.schedule = schedule;
        this.event = event;
        this.cleaning = cleaning;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCleaning() {
        return cleaning;
    }

    public void setCleaning(String cleaning) {
        this.cleaning = cleaning;
    }
}
