package tests.player;

import core.ApiResult;
import data.PlayerFactory;
import enums.UserRole;
import io.qameta.allure.Issue;
import io.restassured.http.Method;
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

import static org.hamcrest.Matchers.lessThan;
import static specification.ResponseSpecFactory.response200;
import static specification.ResponseSpecFactory.response204;
import static specification.ResponseSpecFactory.response400;
import static specification.ResponseSpecFactory.response403;
import static specification.ResponseSpecFactory.response404;
import static specification.ResponseSpecFactory.response405;
import static utils.Constants.SUPERVISOR_ID;

public class PlayerTest extends BaseTest {

    @Issue("CREATE-PLAYER-CONTRACT")
    @Test(description = "Create player contract", groups = {"bug"})
    public void testCreatePlayerContract() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(response200()).validateSchema(JsonSchemas.PLAYER);
    }

    @Test(description = "Get player by id contract")
    public void testGetPlayerByIdContract() {
        int playerId = createPlayerId();

        ApiResult<PlayerResponse> result = playerService.getPlayer(playerId);

        result.validate(response200()).validateSchema(JsonSchemas.PLAYER);
    }

    @Test(description = "Get all players contract")
    public void testGetAllPlayersContract() {
        List<CreatePlayerRequest> requests = List.of(PlayerFactory.validPlayer(), PlayerFactory.validPlayer());
        for (CreatePlayerRequest request : requests) {
            ApiResult<PlayerResponse> resultCreate = playerService.createPlayer(getPermanentSupervisorLogin(), request);
            resultCreate.validate(response200());
        }

        ApiResult<PlayersResponse> result = playerService.getAllPlayers();

        result.validate(response200()).validateSchema(JsonSchemas.PLAYER_SUMMARY);
    }

    @Test(description = "Update player contract")
    public void testUpdatePlayerContract() {
        int playerId = createPlayerId();

        UpdatePlayerRequest updateRequest = PlayerFactory.validUpdatePlayer();
        ApiResult<PlayerResponse> result = playerService.updatePlayer(getPermanentSupervisorLogin(), playerId, updateRequest);

        result.validate(response200()).validateSchema(JsonSchemas.PLAYER_UPDATE);
    }

    @Test(description = "Create player required fields", dataProvider = "testCreatePlayerRequiredFieldsData", dataProviderClass = PlayerDataProvider.class)
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
        PlayerResponse expected = new PlayerResponse(
                player.getId(),
                request.getAge(),
                request.getGender(),
                request.getLogin(),
                request.getPassword(),
                request.getRole(),
                request.getScreenName()
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(player.getId() > 0, "Player must have a valid id");
        softAssert.assertEquals(player, expected, "Created player response is incorrect");
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
        PlayerResponse expected = new PlayerResponse(
                player.getId(),
                request.getAge(),
                request.getGender(),
                request.getLogin(),
                request.getPassword(),
                request.getRole(),
                request.getScreenName()
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(player.getId() > 0, "Player must have a valid id");
        softAssert.assertEquals(player, expected, "Created player retrieved is incorrect");
        softAssert.assertAll();
    }

    @Issue("GET-PLAYER-STRING-ID")
    @Test(description = "Get player with string id should fail", groups = {"bug"})
    public void testGetPlayerWithStringId() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        ApiResult<PlayerResponse> createResult = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        createResult.validate(response200());

        ApiResult<PlayerResponse> result = playerService.getPlayer(String.valueOf(createResult.getBody().getId()));

        result.validate(response400());
    }

    @Issue("SPEC-AGE-TOP-BOUNDARY")
    @Test(description = "Create player age", dataProvider = "testCreatePlayerAgeData", dataProviderClass = PlayerDataProvider.class, groups = {"bug"})
    public void testCreatePlayerAge(String TUID, Integer age, ResponseSpecification spec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setAge(age);
        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(spec);
        if (spec == response200()) {
            ApiResult<PlayerResponse> getResult = playerService.getPlayer(result.getBody().getId());

            getResult.validate(response200());
            Assert.assertEquals(getResult.getBody().getLogin(), request.getLogin());
        }
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

    @Test(description = "Create player with role", dataProvider = "testCreatePlayerRoleData", dataProviderClass = PlayerDataProvider.class)
    public void testCreatePlayerRole(String role, ResponseSpecification spec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setRole(role);

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(spec);
    }

    @Issue("DUPLICATE-SCREEN-NAME-PLAYER")
    @Test(description = "Create player with duplicate unique field", dataProvider = "testCreatePlayerUniqueFieldData", dataProviderClass = PlayerDataProvider.class, groups = {"bug"})
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

    @Issue("CREATE-PLAYER-PASSWORD")
    @Test(description = "Password validation", dataProvider = "testCreatePlayerPasswordData", dataProviderClass = PlayerDataProvider.class, groups = {"bug"})
    public void testCreatePlayerPassword(String TUID, String password, ResponseSpecification expectedSpec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setPassword(password);

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(expectedSpec);
    }

    @Issue("CREATE-PLAYER-GENDER-VALUE")
    @Test(description = "Validate player gender field", dataProvider = "testCreatePlayerGenderData", dataProviderClass = PlayerDataProvider.class, groups = {"bug"})
    public void testCreatePlayerGender(String TUID, String gender, ResponseSpecification expectedSpec) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setGender(gender);

        ApiResult<PlayerResponse> result = playerService.createPlayer(getPermanentSupervisorLogin(), request);

        result.validate(expectedSpec);
    }

    @Test(description = "Supervisor can delete players with admin, user roles", dataProvider = "testSupervisorDeleteRolesData", dataProviderClass = PlayerDataProvider.class)
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

    @Issue("PERF-GET-SUPERVISOR-PLAYER")
    @Test(groups = {"bug"}, description = "Get supervisor player response time should be less than 2 seconds")
    public void testGetPlayerResponseTime() {
        playerService.getPlayer(SUPERVISOR_ID)
                .getRawResponse()
                .then()
                .time(lessThan(2000L));
    }

    @Issue("API-CREATE-METHOD")
    @Test(groups = {"bug"}, description = "Create player endpoint must reject GET")
    public void testCreatePlayerRejectsGet() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();

        playerService.createPlayer(Method.GET, getPermanentSupervisorLogin(), request)
                .validate(response405());
    }

    @Issue("API-CREATE-METHOD")
    @Test(groups = {"bug"}, description = "Create player endpoint must allow POST")
    public void testCreatePlayerAllowsPost() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();

        playerService.createPlayer(Method.POST, getPermanentSupervisorLogin(), request)
                .validate(response200());
    }

    @Issue("API-GET-METHOD")
    @Test(groups = {"bug"}, description = "Get player endpoint must reject POST")
    public void testGetPlayerRejectsPost() {
        int playerId = createPlayerId();

        playerService.getPlayer(Method.POST, playerId)
                .validate(response405());

    }

    @Issue("API-GET-METHOD")
    @Test(groups = {"bug"}, description = "Get player endpoint must allow GET")
    public void testGetPlayerAllowsGet() {
        int playerId = createPlayerId();

        playerService.getPlayer(Method.GET, playerId)
                .validate(response200());
    }
}
