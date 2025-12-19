package by.andrew.task3.parser.impl;

import by.andrew.task3.exception.FerryException;
import by.andrew.task3.factory.CarFactory;
import by.andrew.task3.factory.impl.CarFactoryImpl;
import by.andrew.task3.model.Car;
import by.andrew.task3.parser.FerryDataParser;

public class FerryDataParserImpl implements FerryDataParser {
  private final CarFactory carFactory;
  
  public FerryDataParserImpl() {
    this.carFactory = new CarFactoryImpl();
  }
  
  public FerryDataParserImpl(CarFactory carFactory) {
    this.carFactory = carFactory;
  }
  
  @Override
  public FerryCapacity parseCapacity(String line) throws FerryException {
    String[] parts = line.trim().split("\\s+");
    if (parts.length < 2) {
      throw new FerryException("Invalid capacity format: " + line);
    }
    try {
      double maxWeight = Double.parseDouble(parts[0]);
      double maxArea = Double.parseDouble(parts[1]);
      return new FerryCapacity(maxWeight, maxArea);
    } catch (NumberFormatException e) {
      throw new FerryException("Invalid number format in capacity line: " + line, e);
    }
  }
  
  @Override
  public Car parseCar(String line) throws FerryException {
    String[] parts = line.trim().split("\\s+");
    if (parts.length < 4) {
      throw new FerryException("Invalid car format: " + line);
    }
    try {
      String type = parts[0];
      String id = parts[1];
      double weight = Double.parseDouble(parts[2]);
      double area = Double.parseDouble(parts[3]);
      return carFactory.createCar(type, id, weight, area);
    } catch (NumberFormatException e) {
      throw new FerryException("Invalid number format in car line: " + line, e);
    }
  }
}


