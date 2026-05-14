package tsb.li.common.eventflow.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueIDGenerator {
    private static final AtomicInteger instanceCounter = new AtomicInteger(0);    
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");

    public static synchronized String generateID(String prefix) {    	
    	try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }    	
        int counter = instanceCounter.getAndUpdate(i -> (i + 1) % (Constants.MAX_COUNTER + 1));        
        String currentTime = formatter.format(new Date(System.currentTimeMillis()));
        return prefix + "-" + currentTime + "-" + String.format("%04d", counter);
    }
}
