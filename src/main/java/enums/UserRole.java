package enums;

public enum UserRole {

    USER("user"),
    ADMIN("admin"),
    SUPERVISOR("supervisor");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
