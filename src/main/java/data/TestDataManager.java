package data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TestDataManager {

    private static final Logger LOGGER = LogManager.getLogger(TestDataManager.class);

    private static final ThreadLocal<TestDataManager> INSTANCE = ThreadLocal.withInitial(TestDataManager::new);

    private final List<Runnable> cleanupActions = new ArrayList<>();

    private TestDataManager() {}

    public static TestDataManager getInstance() {
        return INSTANCE.get();
    }

    public static void registerCleanup(Runnable action) {
        getInstance().cleanupActions.add(action);
    }

    public static void cleanup() {
        TestDataManager manager = INSTANCE.get();

        if (manager.cleanupActions.isEmpty()) {
            LOGGER.info("No cleanup needed");
            INSTANCE.remove();
            return;
        }

        LOGGER.info("Starting cleanup of {} action(s)", manager.cleanupActions.size());

        for (Runnable action : manager.cleanupActions) {
            try {
                action.run();
            } catch (Exception e) {
                LOGGER.error("Cleanup action failed", e);
            }
        }

        manager.cleanupActions.clear();
        LOGGER.info("Cleanup completed");
        INSTANCE.remove();
    }
}
