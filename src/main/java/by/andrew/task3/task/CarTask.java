package by.andrew.task3.task;

import by.andrew.task3.model.Car;
import by.andrew.task3.service.FerryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CarTask implements Callable<Boolean> {
  private static final Logger logger = LogManager.getLogger();
  private final Car car;
  private final FerryService ferryService;
  
  public CarTask(Car car, FerryService ferryService) {
    this.car = car;
    this.ferryService = ferryService;
  }
  
  @Override
  public Boolean call() {
    try {
      logger.info("Car {} arrived at the ferry", car.getId());
      
      while (!ferryService.isShutdown()) {
        ferryService.waitForFerryReady();
        
        if (ferryService.isShutdown()) {
          logger.warn("Car {} cannot be loaded: ferry service is shutdown", car.getId());
          return false;
        }
        
        boolean loaded = ferryService.tryLoadCar(car);
        if (loaded) {
          logger.info("Car {} successfully loaded on ferry", car.getId());
          TimeUnit.MILLISECONDS.sleep(100);
          return true;
        } else {
          if (ferryService.isShutdown()) {
            logger.warn("Car {} cannot be loaded: ferry service is shutdown", car.getId());
            return false;
          }
          logger.info("Car {} waiting in queue (ferry is full)", car.getId());
          TimeUnit.MILLISECONDS.sleep(500);
        }
      }
      
      logger.warn("Car {} cannot be loaded: ferry service is shutdown", car.getId());
      return false;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Car {} task interrupted", car.getId(), e);
      return false;
    }
  }
  
  public Car getCar() {
    return car;
  }
}

