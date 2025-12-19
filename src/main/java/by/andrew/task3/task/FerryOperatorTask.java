package by.andrew.task3.task;

import by.andrew.task3.service.FerryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class FerryOperatorTask implements Callable<Void> {
  private static final Logger logger = LogManager.getLogger();
  private final FerryService ferryService;
  private final int maxTrips;
  
  public FerryOperatorTask(FerryService ferryService, int maxTrips) {
    this.ferryService = ferryService;
    this.maxTrips = maxTrips;
  }
  
  @Override
  public Void call() {
    try {
      for (int trip = 0; trip < maxTrips; trip++) {
        logger.info("Ferry operator: preparing trip {}", trip + 1);
        
        TimeUnit.SECONDS.sleep(3);
        
        ferryService.startCrossing();
        
        if (trip < maxTrips - 1) {
          logger.info("Ferry operator: waiting before next trip");
        }
      }
      
      logger.info("Ferry operator: completed all trips");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Ferry operator task interrupted", e);
    }
    return null;
  }
}



