package tests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.TestDataManager;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import services.PlayerService;
import utils.LogCollector;

public abstract class BaseTest {

    protected final PlayerService playerService = new PlayerService();

    @BeforeSuite
    public void setupRestAssured() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(
                        ObjectMapperConfig.objectMapperConfig()
                                .jackson2ObjectMapperFactory((cls, charset) -> mapper)
                );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        TestDataManager.cleanup();
        attachLogs();
    }

    public void attachLogs() {
        String logs = LogCollector.getLogs();

        if (!logs.isEmpty()) {
            Allure.addAttachment("Execution Log", "text/plain", logs);
        }

        LogCollector.clear();
    }
}
