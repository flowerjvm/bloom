package tsb.li.common.eventflow;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventflowBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(EventflowBootstrap.class);

    private static final List<String[]> versionHistory = new ArrayList<>();
    
    static {
        initVersionHistory();
        String[] latest = getLatestVersionEntry();
        if (latest != null) {
        	logger.debug("");
        	logger.debug("=============================================================================================");
            logger.debug("  EventFlow Orchestration Framework(FSM-based) Loading (version : {})", latest[0]);
            logger.debug("   - Latest Change : {}", latest[1]);
            logger.debug("   - By            : {}", latest[2]);
            logger.debug("=============================================================================================");
        } else {
            logger.warn("No changelog entries found.");
        }
    } 
    
    private static void addVersionHistory(String version, String comment, String author) {
        versionHistory.add(new String[]{version, comment, author});
    }

    private static String[] getLatestVersionEntry() {
        if (!versionHistory.isEmpty()) {
            return versionHistory.get(versionHistory.size() - 1);
        }
        return null;
    }
    
    /**
     * Log History 변경 내역 기록 YYYY.MM.DD.N
     */    
    private static void initVersionHistory() {
        addVersionHistory("25.04.04.01", "Initial release of eventflow Framework", "SB.Park");
        addVersionHistory("25.07.09.01", "DSL StateMachine logic added, refactoring", "SB.Park");
    }
}
