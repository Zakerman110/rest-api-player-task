package listeners;

import config.ConfigKeys;
import config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private final Logger LOGGER = LogManager.getLogger(RetryAnalyzer.class);
    private final int maxRetryCount = ConfigManager.getInt(ConfigKeys.RETRY_COUNT);
    private int currentRetryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (currentRetryCount < maxRetryCount) {
            currentRetryCount++;
            LOGGER.warn("Retrying test: {} | Attempt: {}/{}", result.getName(), currentRetryCount, maxRetryCount);
            return true;
        }
        LOGGER.error("Test failed after {} attempts: {}", maxRetryCount, result.getName());
        return false;
    }
}
