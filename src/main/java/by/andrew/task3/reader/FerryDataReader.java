package by.andrew.task3.reader;

import java.io.IOException;
import by.andrew.task3.model.Car;

public interface FerryDataReader {
  FerryData readData(String filename) throws IOException;
  
  class FerryData {
    private final double maxWeight;
    private final double maxArea;
    private final java.util.List<Car> cars;
    
    public FerryData(double maxWeight, double maxArea, java.util.List<Car> cars) {
      this.maxWeight = maxWeight;
      this.maxArea = maxArea;
      this.cars = cars;
    }
    
    public double getMaxWeight() {
      return maxWeight;
    }
    
    public double getMaxArea() {
      return maxArea;
    }
    
    public java.util.List<Car> getCars() {
      return cars;
    }
  }
}


