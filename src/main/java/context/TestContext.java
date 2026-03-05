package context;

public final class TestContext {

    private TestContext() {}

    private static String supervisorLogin;

    public static void setSupervisorLogin(String login) {
        supervisorLogin = login;
    }

    public static String getSupervisorLogin() {
        if (supervisorLogin == null) {
            throw new IllegalStateException("Supervisor login not initialized. Check @BeforeSuite setup.");
        }
        return supervisorLogin;
    }
}
