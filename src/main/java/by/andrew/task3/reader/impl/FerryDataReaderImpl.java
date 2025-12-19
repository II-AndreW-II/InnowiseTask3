package by.andrew.task3.reader.impl;

import by.andrew.task3.exception.FerryException;
import by.andrew.task3.model.Car;
import by.andrew.task3.parser.FerryDataParser;
import by.andrew.task3.parser.impl.FerryDataParserImpl;
import by.andrew.task3.reader.FerryDataReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FerryDataReaderImpl implements FerryDataReader {
  private static final Logger logger = LogManager.getLogger();
  private final FerryDataParser parser;
  
  public FerryDataReaderImpl() {
    this.parser = new FerryDataParserImpl();
  }
  
  public FerryDataReaderImpl(FerryDataParser parser) {
    this.parser = parser;
  }
  
  @Override
  public FerryData readData(String filename) throws IOException {
    List<Car> cars = new ArrayList<>();
    double maxWeight = 0;
    double maxArea = 0;
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      boolean firstLine = true;
      
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }
        
        try {
          if (firstLine) {
            FerryDataParser.FerryCapacity capacity = parser.parseCapacity(line);
            maxWeight = capacity.getMaxWeight();
            maxArea = capacity.getMaxArea();
            logger.info("Ferry capacity: maxWeight={}, maxArea={}", maxWeight, maxArea);
            firstLine = false;
          } else {
            Car car = parser.parseCar(line);
            cars.add(car);
            logger.debug("Loaded car from config: {}", car);
          }
        } catch (FerryException e) {
          logger.error("Error parsing line: {}", line, e);
          throw new IOException("Failed to parse config file", e);
        }
      }
    }
    
    return new FerryData(maxWeight, maxArea, cars);
  }
}


