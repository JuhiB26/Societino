package societino.com.societinof;
public class ToDo {
    private String id, name, type, mobile;

    public ToDo() {
    }

    public ToDo(String id, String name, String type, String mobile) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
