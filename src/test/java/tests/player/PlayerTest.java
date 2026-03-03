package tests.player;

import core.ApiResult;
import data.PlayerFactory;
import models.player.request.CreatePlayerRequest;
import models.player.response.PlayerResponse;
import org.testng.annotations.Test;
import tests.BaseTest;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specification.ResponseSpecFactory.response200;
import static utils.Constants.SUPERVISOR_LOGIN;

public class PlayerTest extends BaseTest {

    @Test(description = "Create player contract")
    public void testCreatePlayerContract() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();

        ApiResult<PlayerResponse> result = playerService.createPlayer(SUPERVISOR_LOGIN, request);

        result.getRawResponse()
                .then()
                .spec(response200())
                .body(matchesJsonSchemaInClasspath("json-schemas/player-schema.json"));
    }

    @Test(description = "Get player by id contract")
    public void testGetPlayerByIdContract() {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        ApiResult<PlayerResponse> resultCreate = playerService.createPlayer(SUPERVISOR_LOGIN, request);
        resultCreate.getRawResponse()
                .then()
                .spec(response200());

        int playerId = resultCreate.getBody().getId();

        ApiResult<PlayerResponse> result = playerService.getPlayer(playerId);

        result.getRawResponse()
                .then()
                .spec(response200())
                .body(matchesJsonSchemaInClasspath("json-schemas/player-schema.json"));
    }
}
