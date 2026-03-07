package tests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigKeys;
import config.ConfigManager;
import context.TestContext;
import data.PlayerFactory;
import data.TestDataManager;
import enums.UserRole;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import models.player.request.CreatePlayerRequest;
import models.player.response.PlayerResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import services.PlayerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static specification.ResponseSpecFactory.response200;
import static utils.Constants.SUPERVISOR_ID;

public abstract class BaseTest {

    protected final PlayerService playerService = new PlayerService();

    @BeforeSuite(alwaysRun = true)
    public void setup() {
        setupRestAssured();
        writeAllureEnvironment();
        initPermanentSupervisor();
    }

    public void setupRestAssured() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(
                        ObjectMapperConfig.objectMapperConfig()
                                .jackson2ObjectMapperFactory((cls, charset) -> mapper)
                );
    }

    private void writeAllureEnvironment() {
        Properties props = new Properties();
        props.setProperty("Base URL", ConfigManager.get(ConfigKeys.BASE_URL));
        props.setProperty("Retry Count", String.valueOf(ConfigManager.getInt(ConfigKeys.RETRY_COUNT)));
        props.setProperty("Parallel", ConfigManager.get(ConfigKeys.PARALLEL));
        props.setProperty("Thread Count", String.valueOf(ConfigManager.getInt(ConfigKeys.THREAD_COUNT)));

        Path resultsDir = Path.of("build", "allure-results");
        Path envFile = resultsDir.resolve("environment.properties");

        try {
            Files.createDirectories(resultsDir);
            try (var out = Files.newOutputStream(envFile)) {
                props.store(out, "Allure Environment");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Allure environment.properties", e);
        }
    }

    public void initPermanentSupervisor() {
        String login = playerService
                .getPlayer(SUPERVISOR_ID)
                .validate(response200())
                .getBody()
                .getLogin();

        TestContext.setSupervisorLogin(login);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        TestDataManager.cleanup();
    }

    @Step("Get permanent supervisor login")
    public String getPermanentSupervisorLogin() {
        return TestContext.getSupervisorLogin();
    }

    @Step("Create player with role {0}")
    public PlayerResponse createPlayer(UserRole role) {
        CreatePlayerRequest request = PlayerFactory.validPlayer();
        request.setRole(role.getValue());
        return playerService
                .createPlayer(getPermanentSupervisorLogin(), request)
                .validate(response200())
                .getBody();
    }

    public int createPlayerId() {
        return createPlayer(UserRole.USER).getId();
    }

    public int createPlayerId(UserRole role) {
        return createPlayer(role).getId();
    }

    public String createPlayerLogin(UserRole role) {
        return createPlayer(role).getLogin();
    }
}
