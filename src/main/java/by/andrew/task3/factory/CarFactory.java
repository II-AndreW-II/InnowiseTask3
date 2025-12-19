package by.andrew.task3.factory;

import by.andrew.task3.exception.FerryException;
import by.andrew.task3.model.Car;

public interface CarFactory {
  Car createCar(String type, String id, double weight, double area) throws FerryException;
}


