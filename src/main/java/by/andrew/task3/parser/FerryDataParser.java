package by.andrew.task3.parser;

import by.andrew.task3.exception.FerryException;
import by.andrew.task3.model.Car;

public interface FerryDataParser {
  FerryCapacity parseCapacity(String line) throws FerryException;
  Car parseCar(String line) throws FerryException;
  
  class FerryCapacity {
    private final double maxWeight;
    private final double maxArea;
    
    public FerryCapacity(double maxWeight, double maxArea) {
      this.maxWeight = maxWeight;
      this.maxArea = maxArea;
    }
    
    public double getMaxWeight() {
      return maxWeight;
    }
    
    public double getMaxArea() {
      return maxArea;
    }
  }
}


