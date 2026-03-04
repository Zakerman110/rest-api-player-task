package models.player.response;

public class PlayerSummaryResponse {

    private int id;
    private int age;
    private String gender;
    private String screenName;

    public PlayerSummaryResponse() {
    }

    public PlayerSummaryResponse(int id, int age, String gender, String screenName) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.screenName = screenName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
