package tests.player;

import context.TestContext;
import core.ApiResult;
import data.PlayerFactory;
import enums.UserRole;
import io.qameta.allure.Issue;
import io.restassured.specification.ResponseSpecification;
import models.player.request.UpdatePlayerRequest;
import models.player.response.PlayerResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tests.BaseTest;

import java.util.function.Supplier;

import static specification.ResponseSpecFactory.*;
import static utils.Constants.SUPERVISOR_ID;

public class PlayerRoleTest extends BaseTest {

    private Supplier<String> supervisorLogin() {
        return this::getPermanentSupervisorLogin;
    }

    private Supplier<String> adminLogin() {
        return () -> createPlayerLogin(UserRole.ADMIN);
    }

    private Supplier<String> userLogin() {
        return () -> createPlayerLogin(UserRole.USER);
    }

    private Supplier<Integer> supervisorId() {
        return () -> SUPERVISOR_ID;
    }

    private Supplier<Integer> adminId() {
        return () -> createPlayerId(UserRole.ADMIN);
    }

    private Supplier<Integer> userId() {
        return () -> createPlayerId(UserRole.USER);
    }

    @DataProvider(name = "testDeletePlayerPermissionsData")
    public Object[][] testDeletePlayerPermissionsData() {
        return new Object[][]{
                {"SUPERVISOR cannot delete SUPERVISOR", supervisorLogin(), supervisorId(), response403()},
                {"SUPERVISOR can delete ADMIN", supervisorLogin(), adminId(), response204()},
                {"SUPERVISOR can delete USER", supervisorLogin(), userId(), response204()},
                {"ADMIN cannot delete ADMIN", adminLogin(), adminId(), response403()},
                {"ADMIN can delete USER", adminLogin(), userId(), response204()},
                {"ADMIN cannot delete SUPERVISOR", adminLogin(), supervisorId(), response403()},
                {"USER cannot delete USER", userLogin(), userId(), response403()},
                {"USER cannot delete ADMIN", userLogin(), adminId(), response403()},
                {"USER cannot delete SUPERVISOR", userLogin(), supervisorId(), response403()}
        };
    }

    @Issue("PLAYER-CAN-DELETE")
    @Test(dataProvider = "testDeletePlayerPermissionsData", groups = {"bug"})
    public void testDeletePlayerPermissions(String TUID, Supplier<String> editorLoginSupplier, Supplier<Integer> targetIdSupplier, ResponseSpecification expectedSpec) {
        String editorLogin = editorLoginSupplier.get();
        int targetId = targetIdSupplier.get();

        ApiResult<Void> result = playerService.deletePlayer(editorLogin, targetId);

        result.validate(expectedSpec);
    }

    private record PlayerContext(String login, int id) {}

    @DataProvider(name = "testDeleteSelfPermissionsData")
    public Object[][] testDeleteSelfPermissionsData() {
        return new Object[][]{
                {"SUPERVISOR cannot delete himself", (Supplier<PlayerContext>) () -> new PlayerContext(getPermanentSupervisorLogin(), SUPERVISOR_ID), response403()},
                {"ADMIN can delete himself", (Supplier<PlayerContext>) () -> {
                    var playerAdmin = createPlayer(UserRole.ADMIN);
                    return new PlayerContext(playerAdmin.getLogin(), playerAdmin.getId());
                }, response204()},
                {"USER cannot delete himself", (Supplier<PlayerContext>) () -> {
                    var playerUser = createPlayer(UserRole.USER);
                    return new PlayerContext(playerUser.getLogin(), playerUser.getId());
                }, response403()}
        };
    }

    @Issue("USER-CAN-DELETE_SELF")
    @Test(dataProvider = "testDeleteSelfPermissionsData")
    public void testDeleteSelfPermissions(String TUID, Supplier<PlayerContext> playerSupplier, ResponseSpecification expectedSpec) {
        PlayerContext player = playerSupplier.get();

        ApiResult<Void> result = playerService.deletePlayer(player.login(), player.id());

        result.validate(expectedSpec);
    }

    @DataProvider(name = "testUpdatePlayerPermissionsData")
    public Object[][] testUpdatePlayerPermissionsData() {
        return new Object[][]{
                {"SUPERVISOR can update SUPERVISOR", supervisorLogin(), supervisorId(), response200()},
                {"SUPERVISOR can update ADMIN", supervisorLogin(), adminId(), response200()},
                {"SUPERVISOR can update USER", supervisorLogin(), userId(), response200()},
                {"ADMIN cannot update ADMIN", adminLogin(), adminId(), response403()},
                {"ADMIN can update USER", adminLogin(), userId(), response200()},
                {"ADMIN cannot update SUPERVISOR", adminLogin(), supervisorId(), response403()},
                {"USER can update USER", userLogin(), userId(), response200()},
                {"USER cannot update ADMIN", userLogin(), adminId(), response403()},
                {"USER cannot update SUPERVISOR", userLogin(), supervisorId(), response403()}
        };
    }

    @Issue("PLAYER-CAN-UPDATE")
    @Test(dataProvider = "testUpdatePlayerPermissionsData", groups = {"bug"})
    public void testUpdatePlayerPermissions(String TUID, Supplier<String> editorLoginSupplier, Supplier<Integer> targetIdSupplier, ResponseSpecification expectedSpec) {
        String editorLogin = editorLoginSupplier.get();
        int targetId = targetIdSupplier.get();

        UpdatePlayerRequest request = PlayerFactory.validUpdatePlayer();

        if (targetId == SUPERVISOR_ID) request.setLogin(getPermanentSupervisorLogin());
        ApiResult<PlayerResponse> result = playerService.updatePlayer(editorLogin, targetId, request);

        result.validate(expectedSpec);
    }

    @DataProvider(name = "testSelfRoleEscalationData")
    public Object[][] testSelfRoleEscalationData() {
        return new Object[][]{
                {
                        "USER cannot escalate himself to ADMIN",
                        (Supplier<PlayerContext>) () -> {
                            var user = createPlayer(UserRole.USER);
                            return new PlayerContext(user.getLogin(), user.getId());
                        },
                        UserRole.ADMIN
                },
                {
                        "USER cannot escalate himself to SUPERVISOR",
                        (Supplier<PlayerContext>) () -> {
                            var user = createPlayer(UserRole.USER);
                            return new PlayerContext(user.getLogin(), user.getId());
                        },
                        UserRole.SUPERVISOR
                },
                {
                        "ADMIN cannot escalate himself to SUPERVISOR",
                        (Supplier<PlayerContext>) () -> {
                            var admin = createPlayer(UserRole.ADMIN);
                            return new PlayerContext(admin.getLogin(), admin.getId());
                        },
                        UserRole.SUPERVISOR
                }
        };
    }

    @Test(description = "User cannot upgrade role for himself", dataProvider = "testSelfRoleEscalationData")
    public void testSelfRoleEscalation(String TUID, Supplier<PlayerContext> playerSupplier, UserRole newRole) {
        PlayerContext player = playerSupplier.get();

        ApiResult<PlayerResponse> before = playerService.getPlayer(player.id());
        String originalRole = before.getBody().getRole();

        UpdatePlayerRequest request = PlayerFactory.validUpdatePlayer();
        request.setRole(newRole.getValue());

        ApiResult<PlayerResponse> updateResult =
                playerService.updatePlayer(player.login(), player.id(), request);

        updateResult.validate(response200());

        ApiResult<PlayerResponse> after = playerService.getPlayer(player.id());

        Assert.assertEquals(after.getBody().getRole(), originalRole, "Role escalation should not be allowed");
    }
}
