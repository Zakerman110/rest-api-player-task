package listeners;

import config.ConfigKeys;
import config.ConfigManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.LogCollector;

import java.util.UUID;

public class TestListener implements ITestListener {

    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LogCollector.clear();

        String testName = result.getMethod().getMethodName();
        String requestId = UUID.randomUUID().toString().substring(0,8);

        ThreadContext.put("testName", testName);
        ThreadContext.put("requestId", requestId);

        LOGGER.info("START TEST: {} | RequestID: {}", testName, requestId);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        LOGGER.info("PASSED: {} ({} ms)", result.getMethod().getMethodName(), duration);
        attachLogs();
        ThreadContext.clearAll();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("FAILED: {}", result.getMethod().getMethodName());
        LOGGER.error("Exception:", result.getThrowable());
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Allure.addAttachment("Failure reason", throwable.getMessage() == null ? throwable.toString() : throwable.getMessage());
        }
        attachLogs();
        ThreadContext.clearAll();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warn("SKIPPED: {}", result.getMethod().getMethodName());
        attachLogs();
        ThreadContext.clearAll();
    }

    @Override
    public void onStart(ITestContext context) {
        String baseUrl = ConfigManager.get(ConfigKeys.BASE_URL);
        int threadCount = ConfigManager.getInt(ConfigKeys.THREAD_COUNT);

        LOGGER.info("=====================================");
        LOGGER.info("TEST SUITE STARTED");
        LOGGER.info("Base URL: {}", baseUrl);
        LOGGER.info("Thread Count: {}", threadCount);
        LOGGER.info("Suite Name: {}", context.getSuite().getName());
        LOGGER.info("=====================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("=====================================");
        LOGGER.info("TEST SUITE FINISHED");
        LOGGER.info("Passed: {}", context.getPassedTests().size());
        LOGGER.info("Failed: {}", context.getFailedTests().size());
        LOGGER.info("Skipped: {}", context.getSkippedTests().size());
        LOGGER.info("=====================================");
    }

    private void attachLogs() {
        String logs = LogCollector.getLogs();

        if (!logs.isEmpty()) {
            Allure.addAttachment("Execution Log", "text/plain", logs);
        }

        LogCollector.clear();
    }
}
