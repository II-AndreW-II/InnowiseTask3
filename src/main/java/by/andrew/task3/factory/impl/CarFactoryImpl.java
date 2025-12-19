package by.andrew.task3.factory.impl;

import by.andrew.task3.exception.FerryException;
import by.andrew.task3.factory.CarFactory;
import by.andrew.task3.model.Car;
import by.andrew.task3.model.PassengerCar;
import by.andrew.task3.model.Truck;

public class CarFactoryImpl implements CarFactory {
  
  @Override
  public Car createCar(String type, String id, double weight, double area) throws FerryException {
    switch (type.toLowerCase()) {
      case "passenger":
      case "passengercar":
        return new PassengerCar(id, weight, area);
      case "truck":
        return new Truck(id, weight, area);
      default:
        throw new FerryException("Unknown car type: " + type);
    }
  }
  
}
