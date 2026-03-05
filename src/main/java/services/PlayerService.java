package services;

import context.TestContext;
import core.ApiResult;
import core.BaseService;
import data.TestDataManager;
import endpoints.PlayerEndpoints;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import models.player.request.CreatePlayerRequest;
import models.player.request.UpdatePlayerRequest;
import models.player.response.PlayerResponse;
import models.player.response.PlayersResponse;

import java.util.HashMap;
import java.util.Map;

public class PlayerService extends BaseService {

    public ApiResult<PlayerResponse> createPlayer(String editor, CreatePlayerRequest request) {
        return createPlayer(Method.GET, editor, request); // incorrect http method, but this is swagger spec
    }

    public ApiResult<PlayerResponse> createPlayer(Method method, String editor, CreatePlayerRequest request) {
        Map<String, Object> pathParams = Map.of("editor", editor);
        Map<String, Object> queryParams = new HashMap<>();

        if (request.getAge() != null) queryParams.put("age", request.getAge());
        if (request.getGender() != null) queryParams.put("gender", request.getGender());
        if (request.getLogin() != null) queryParams.put("login", request.getLogin());
        if (request.getPassword() != null) queryParams.put("password", request.getPassword());
        if (request.getRole() != null) queryParams.put("role", request.getRole());
        if (request.getScreenName() != null) queryParams.put("screenName", request.getScreenName());

        ApiResult<PlayerResponse> result = execute(
                method,
                PlayerEndpoints.CREATE_PLAYER,
                null,
                pathParams,
                queryParams,
                new TypeRef<>() {}
        );

        if (result.getRawResponse().getStatusCode() == 200) {
            int playerId = result.getBody().getId();
            TestDataManager.registerCleanup(() -> deletePlayer(editor, playerId));
        }

        return result;
    }

    public ApiResult<Void> deletePlayer(String editor, int playerId) {
        Map<String, Object> pathParams = Map.of("editor", editor);
        Map<String, Integer> body = Map.of("playerId", playerId);

        return execute(
                Method.DELETE,
                PlayerEndpoints.DELETE_PLAYER,
                body,
                pathParams,
                null,
                null
        );
    }

    public ApiResult<PlayerResponse> getPlayer(int playerId) {
        return getPlayer(Method.POST, playerId); // incorrect http method, but this is swagger spec
    }

    public ApiResult<PlayerResponse> getPlayer(Method method, int playerId) {
        Map<String, Integer> body = Map.of("playerId", playerId);

        return execute(
                method,
                PlayerEndpoints.GET_PLAYER_BY_ID,
                body,
                null,
                null,
                new TypeRef<>() {}
        );
    }

    public ApiResult<PlayersResponse> getAllPlayers() {
        return execute(
                Method.GET,
                PlayerEndpoints.GET_ALL_PLAYERS,
                null,
                null,
                null,
                new TypeRef<>() {}
        );
    }

    /**
     * Updates a player by id.
     *
     * <p><b>Test framework constraint:</b></p>
     * Prevents modification of the permanent supervisor (id = 1) login.
     * The supervisor login is cached in {@link TestContext} and used across tests,
     * so changing it would break the test suite.
     */
    public ApiResult<PlayerResponse> updatePlayer(String editor, int id, UpdatePlayerRequest request) {
        if (id == 1 && !TestContext.getSupervisorLogin().equals(request.getLogin())) {
            throw new IllegalStateException("Tests must not modify supervisor login (id = 1)");
        }

        Map<String, Object> pathParams = Map.of(
                "editor", editor,
                "id", id
        );

        return execute(
                Method.PATCH,
                PlayerEndpoints.UPDATE_PLAYER,
                request,
                pathParams,
                null,
                new TypeRef<>() {}
        );
    }
}
