package models.player.request;

import java.util.Objects;

public class CreatePlayerRequest {

    private Integer  age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    public CreatePlayerRequest() {
    }

    public CreatePlayerRequest(Integer age, String gender, String login, String password, String role, String screenName) {
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.role = role;
        this.screenName = screenName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreatePlayerRequest)) return false;
        CreatePlayerRequest that = (CreatePlayerRequest) o;
        return Objects.equals(age, that.age) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(login, that.login) &&
                Objects.equals(password, that.password) &&
                Objects.equals(role, that.role) &&
                Objects.equals(screenName, that.screenName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, gender, login, password, role, screenName);
    }

    @Override
    public String toString() {
        return "CreatePlayerRequest{" +
                "age=" + age +
                ", gender='" + gender + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
