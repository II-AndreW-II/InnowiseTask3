package by.andrew.task3.ferry;

import by.andrew.task3.model.Car;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public interface Ferry {
  ReentrantLock getLock();
  boolean canLoadCar(Car car);
  void loadCar(Car car);
  List<Car> unloadCars();
  FerryState getState();
  void setState(FerryState state);
  boolean isEmpty();
  int getCarCount();
  double getCurrentWeight();
  double getCurrentArea();
  double getMaxWeight();
  double getMaxArea();
}



