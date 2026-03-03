package tests;

import data.TestDataManager;
import io.qameta.allure.Allure;
import org.testng.annotations.AfterMethod;
import services.PlayerService;
import utils.LogCollector;

public abstract class BaseTest {

    protected final PlayerService playerService = new PlayerService();

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
