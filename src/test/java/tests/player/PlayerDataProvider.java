package tests.player;

import enums.Gender;
import enums.UserRole;
import models.player.request.CreatePlayerRequest;
import org.testng.annotations.DataProvider;

import java.util.function.Consumer;

import static specification.ResponseSpecFactory.response200;
import static specification.ResponseSpecFactory.response400;

public class PlayerDataProvider {

    @DataProvider(name = "testCreatePlayerRequiredFieldsData")
    public Object[][] testCreatePlayerRequiredFieldsData() {
        return new Object[][]{
                {"missing age", (Consumer<CreatePlayerRequest>) r -> r.setAge(null)},
                {"missing gender", (Consumer<CreatePlayerRequest>) r -> r.setGender(null)},
                {"missing login", (Consumer<CreatePlayerRequest>) r -> r.setLogin(null)},
                {"missing role", (Consumer<CreatePlayerRequest>) r -> r.setRole(null)},
                {"missing screenName", (Consumer<CreatePlayerRequest>) r -> r.setScreenName(null)}
        };
    }

    @DataProvider(name = "testCreatePlayerAgeData")
    public Object[][] testCreatePlayerAgeData() {
        return new Object[][]{
                {"age=16", 16, response400()},
                {"age=17", 17, response200()},
                {"age=59", 59, response200()},
                {"age=60", 60, response400()}
        };
    }

    @DataProvider(name = "testCreatePlayerRoleData")
    public Object[][] testCreatePlayerData() {
        return new Object[][]{
                {UserRole.USER.getValue(), response200()},
                {UserRole.ADMIN.getValue(), response200()},
                {UserRole.SUPERVISOR.getValue(), response400()},
                {"role", response400()}
        };
    }

    @DataProvider(name = "testCreatePlayerUniqueFieldData")
    public Object[][] testCreatePlayerUniqueFieldData() {
        return new Object[][]{
                {"login"},
                {"screenName"}
        };
    }

    @DataProvider(name = "testCreatePlayerPasswordData")
    public Object[][] testCreatePlayerPasswordData() {
        return new Object[][]{
                {"Valid size letters and numbers", "Abc123Def4", response200()},
                {"Valid size only letters", "abcdefghij", response400()},
                {"Valid size only numbers", "1234567890", response400()},
                {"Edge 6 chars", "Abc123", response400()},
                {"Edge 7 chars", "Abc1234", response200()},
                {"Edge 16 chars", "Abc123Def456Gh78", response400()},
                {"Edge 15 chars", "Abc123Def456Gh7", response200()}
        };
    }

    @DataProvider(name = "testCreatePlayerGenderData")
    public Object[][] testCreatePlayerGenderData() {
        return new Object[][]{
                {"Valid male", Gender.MALE.getValue(), response200()},
                {"Valid female", Gender.FEMALE.getValue(), response200()},
                {"Invalid value", "unknown", response400()},
                {"Invalid uppercase", "MALE", response400()},
                {"Invalid empty", "", response400()},
                {"Invalid null", null, response400()}
        };
    }

    @DataProvider(name = "testSupervisorDeleteRolesData")
    public Object[][] testSupervisorDeleteRolesData() {
        return new Object[][]{
                {"Supervisor deletes ADMIN", UserRole.ADMIN},
                {"Supervisor deletes USER", UserRole.USER}
        };
    }
}
