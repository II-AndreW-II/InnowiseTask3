package by.andrew.task3;

import by.andrew.task3.ferry.Ferry;
import by.andrew.task3.ferry.impl.FerryImpl;
import by.andrew.task3.model.Car;
import by.andrew.task3.service.FerryService;
import by.andrew.task3.service.impl.FerryServiceImpl;
import by.andrew.task3.task.CarTask;
import by.andrew.task3.task.FerryOperatorTask;
import by.andrew.task3.reader.FerryDataReader;
import by.andrew.task3.reader.impl.FerryDataReaderImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final Logger logger = LogManager.getLogger();
  
  public static void main(String[] args) {
    try {
      String configFile = "data/ferry-data.txt";
      if (args.length > 0) {
        configFile = args[0];
      }
      
      logger.info("Starting ferry application");
      FerryDataReader dataReader = new FerryDataReaderImpl();
      FerryDataReader.FerryData data = dataReader.readData(configFile);
      
      Ferry ferry = FerryImpl.getInstance(data.getMaxWeight(), data.getMaxArea());
      FerryService ferryService = new FerryServiceImpl(ferry);
      
      ExecutorService executor = Executors.newCachedThreadPool();
      List<Future<Boolean>> carFutures = new ArrayList<>();
      
      logger.info("Starting {} cars", data.getCars().size());
      for (Car car : data.getCars()) {
        CarTask carTask = new CarTask(car, ferryService);
        Future<Boolean> future = executor.submit(carTask);
        carFutures.add(future);
        TimeUnit.MILLISECONDS.sleep(200);
      }
      
      int maxTrips = 5;
      FerryOperatorTask operatorTask = new FerryOperatorTask(ferryService, maxTrips);
      Future<Void> operatorFuture = executor.submit(operatorTask);
      
      logger.info("Waiting for all cars to be transported");
      for (Future<Boolean> future : carFutures) {
        try {
          future.get();
        } catch (Exception e) {
          logger.error("Error waiting for car task", e);
        }
      }
      
      operatorFuture.get();
      
      logger.info("All trips completed, shutting down ferry service");
      ferryService.shutdown();
      
      executor.shutdown();
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        logger.warn("Forcing shutdown of executor service");
        executor.shutdownNow();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
          logger.error("Executor service did not terminate");
        }
      }
      
      logger.info("Ferry application completed");
    } catch (Exception e) {
      logger.error("Error in main application", e);
      e.printStackTrace();
    }
  }
}

