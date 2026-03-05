package tests.player;

import core.ApiResult;
import data.PlayerFactory;
import enums.Gender;
import enums.UserRole;
import io.qameta.allure.Issue;
import io.restassured.specification.ResponseSpecification;
import models.player.request.CreatePlayerRequest;
import models.player.request.UpdatePlayerRequest;
import models.player.response.PlayerResponse;
import models.player.response.PlayersResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import tests.BaseTest;
import utils.JsonSchemas;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specification.ResponseSpecFactory.*;

public class PlayerTest extends BaseTest {

    @Issue("CREATE-PLAYER-CONTRACT")
    @Test(description = "Create player contract", groups = {"bug"})
    public void testCreatePlayerContract() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.getRawResponse()
                .then()
                .spec(response200())
                .body(matchesJsonSchemaInClasspath(JsonSchemas.PLAYER));
    }

    @Test(description = "Get player by id contract")
    public void testGetPlayerByIdContract() {
        int playerId = createPlayerId();

        ApiResult<PlayerResponse> result = playerService.getPlayer(playerId);

        result.getRawResponse()
                .then()
                .spec(response200())
                .body(matchesJsonSchemaInClasspath(JsonSchemas.PLAYER));
    }

    @Test(description = "Get all players contract")
    public void testGetAllPlayersContract() {
        List<CreatePlayerRequest> requests = List.of(PlayerFactory.validPlayer(), PlayerFactory.validPlayer());
        for (CreatePlayerRequest request : requests) {
            ApiResult<PlayerResponse> resultCreate = playerService.createPlayer(getPermanentSupervisorLogin(), request);
            resultCreate.validate(response200());
        }

        ApiResult<PlayersResponse> result = playerService.getAllPlayers();

        result.getRawResponse()
                .then()
                .spec(response200())
                .body(matchesJsonSchemaInClasspath(JsonSchemas.PLAYER_SUMMARY));
    }

    @Test(description = "Update player contract")
    public void testUpdatePlayerContract() {
        int playerId = createPlayerId();

        UpdatePlayerRequest updateRequest = PlayerFactory.validUpdatePlayer();
        ApiResult<PlayerResponse> result = playerService.updatePlayer(getPermanentSupervisorLogin(), playerId, updateRequest);

        result.getRawResponse()
                .then()
                .spec(response200())
                .body(matchesJsonSchemaInClasspath(JsonSchemas.PLAYER_UPDATE));
    }

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

    @Test(description = "Create player required fields", dataProvider = "testCreatePlayerRequiredFieldsData")
    public void testCreatePlayerRequiredFields(String TUID, Consumer<CreatePlayerRequest> modifier) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        modifier.accept(request);
        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(response400());
    }

    @Issue("CREATE-PLAYER-RESPONSE")
    @Test(description = "Create player returns valid response", groups = {"bug"})
    public void testCreatePlayerResponse() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(response200());

        PlayerResponse player = result.getBody();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(player.getId() > 0, "Id should be greater than 0");
        softAssert.assertEquals(player.getAge(), request.getAge(), "Age is incorrect");
        softAssert.assertEquals(player.getGender(), request.getGender(), "Gender is incorrect");
        softAssert.assertEquals(player.getLogin(), request.getLogin(), "Login is incorrect");
        softAssert.assertEquals(player.getRole(), request.getRole(), "Role is incorrect");
        softAssert.assertEquals(player.getPassword(), request.getPassword(), "Password is incorrect");
        softAssert.assertEquals(player.getScreenName(), request.getScreenName(), "ScreenName is incorrect");
        softAssert.assertAll();
    }

    @Test(description = "Created player can be retrieved with correct data")
    public void testCreatePlayerPersistence() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        ApiResult<PlayerResponse> createResult = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        createResult.validate(response200());

        int playerId = createResult.getBody().getId();

        ApiResult<PlayerResponse> result = playerService.getPlayer(playerId);

        PlayerResponse player = result.getBody();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(player.getId() > 0, "Id should be greater than 0");
        softAssert.assertEquals(player.getAge(), request.getAge(), "Age is incorrect");
        softAssert.assertEquals(player.getGender(), request.getGender(), "Gender is incorrect");
        softAssert.assertEquals(player.getLogin(), request.getLogin(), "Login is incorrect");
        softAssert.assertEquals(player.getRole(), request.getRole(), "Role is incorrect");
        softAssert.assertEquals(player.getPassword(), request.getPassword(), "Password is incorrect");
        softAssert.assertEquals(player.getScreenName(), request.getScreenName(), "ScreenName is incorrect");
        softAssert.assertAll();
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

    @Issue("SPEC-AGE-TOP-BOUNDARY")
    @Test(description = "Create player age", dataProvider = "testCreatePlayerAgeData", groups = {"bug"})
    public void testCreatePlayerAge(String TUID, Integer age, ResponseSpecification spec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setAge(age);
        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(spec);
    }

    @DataProvider(name = "testCreatePlayerByEditorRoleData")
    public Object[][] testCreatePlayerByEditorRoleData() {
        return new Object[][]{
                {"supervisor", (Supplier<String>) this::getPermanentSupervisorLogin, response200()},
                {"admin", (Supplier<String>) () -> createPlayerLogin(UserRole.ADMIN), response200()},
                {"user", (Supplier<String>) () -> createPlayerLogin(UserRole.USER), response403()}
        };
    }

    @Test(description = "Create player by role", dataProvider = "testCreatePlayerByEditorRoleData")
    public void testCreatePlayerByEditorRole(String TUID, Supplier<String> loginSupplier, ResponseSpecification spec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();

        ApiResult<PlayerResponse> result = playerService.createPlayer(loginSupplier.get(), request);

        result.validate(spec);
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

    @Test(description = "Create player with role", dataProvider = "testCreatePlayerRoleData")
    public void testCreatePlayerRole(String role, ResponseSpecification spec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setRole(role);

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(spec);
    }

    @DataProvider(name = "testCreatePlayerUniqueFieldData")
    public Object[][] testCreatePlayerUniqueFieldData() {
        return new Object[][]{
                {"login"},
                {"screenName"}
        };
    }

    @Issue("DUPLICATE-SCREEN-NAME-PLAYER")
    @Test(description = "Create player with duplicate unique field", dataProvider = "testCreatePlayerUniqueFieldData", groups = {"bug"})
    public void testCreatePlayerUniqueField(String uniqueField) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();

        ApiResult<PlayerResponse> result =playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(response200());
        Integer firstPlayerId = result.getBody().getId();

        CreatePlayerRequest duplicateRequest = PlayerFactory.validPlayer();

        if (uniqueField.equals("login")) {
            duplicateRequest.setLogin(request.getLogin());
        } else if (uniqueField.equals("screenName")) {
            duplicateRequest.setScreenName(request.getScreenName());
        }

        ApiResult<PlayerResponse> duplicateResult = playerService.createPlayer(getPermanentSupervisorLogin(), duplicateRequest);

        duplicateResult.validate(response200());

        Assert.assertEquals(duplicateResult.getBody().getId(), firstPlayerId, "Duplicate player should not be created");
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

    @Issue("CREATE-PLAYER-PASSWORD")
    @Test(description = "Password validation", dataProvider = "testCreatePlayerPasswordData", groups = {"bug"})
    public void testCreatePlayerPassword(String TUID, String password, ResponseSpecification expectedSpec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setPassword(password);

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(expectedSpec);
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

    @Issue("CREATE-PLAYER-GENDER-VALUE")
    @Test(description = "Validate player gender field", dataProvider = "testCreatePlayerGenderData", groups = {"bug"})
    public void testCreatePlayerGender(String TUID, String gender, ResponseSpecification expectedSpec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setGender(gender);

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(expectedSpec);
    }

    @DataProvider(name = "testSupervisorDeleteRolesData")
    public Object[][] testSupervisorDeleteRolesData() {
        return new Object[][]{
                {"Supervisor deletes ADMIN", UserRole.ADMIN},
                {"Supervisor deletes USER", UserRole.USER}
        };
    }

    @Test(description = "Supervisor can delete players with admin, user roles", dataProvider = "testSupervisorDeleteRolesData")
    public void testSupervisorCanDeleteRoles(String TUID, UserRole roleToDelete) {
        int targetId = createPlayerId(roleToDelete);

        ApiResult<Void> result = playerService.deletePlayer(getPermanentSupervisorLogin(), targetId);

        result.validate(response204());

        ApiResult<PlayersResponse> allPlayers =
                playerService.getAllPlayers();

        boolean exists = allPlayers.getBody()
                .getPlayers()
                .stream()
                .anyMatch(p -> p.getId() == targetId);

        Assert.assertFalse(exists, "Deleted player still present in players list");
    }

    @Issue("GET-NON-EXISTING-404")
    @Test(description = "Get non-existing player", groups = {"bug"})
    public void testGetNonExistingPlayer() {
        int nonExistingPlayerId = Integer.MAX_VALUE;

        ApiResult<PlayerResponse> result = playerService.getPlayer(nonExistingPlayerId);

        result.validate(response404());
    }

    @Issue("DELETE-NON-EXISTING-404")
    @Test(description = "Delete non-existing player", groups = {"bug"})
    public void testDeleteNonExistingPlayer() {
        int nonExistingPlayerId = Integer.MAX_VALUE;

        ApiResult<Void> result = playerService.deletePlayer(getPermanentSupervisorLogin(), nonExistingPlayerId);

        result.validate(response404());
    }
}
