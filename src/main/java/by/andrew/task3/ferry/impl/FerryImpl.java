package by.andrew.task3.ferry.impl;

import by.andrew.task3.ferry.Ferry;
import by.andrew.task3.ferry.FerryState;
import by.andrew.task3.model.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FerryImpl implements Ferry {
  private static final Logger logger = LogManager.getLogger();
  private static FerryImpl instance;
  private static final ReentrantLock instanceLock = new ReentrantLock();
  
  private final double maxWeight;
  private final double maxArea;
  private final List<Car> cars;
  private FerryState state;
  private double currentWeight;
  private double currentArea;
  private final ReentrantLock lock;
  
  private FerryImpl(double maxWeight, double maxArea) {
    this.maxWeight = maxWeight;
    this.maxArea = maxArea;
    this.cars = new ArrayList<>();
    this.state = FerryState.WAITING;
    this.currentWeight = 0;
    this.currentArea = 0;
    this.lock = new ReentrantLock();
    logger.info("Ferry created: maxWeight={}, maxArea={}", maxWeight, maxArea);
  }
  
  public static FerryImpl getInstance(double maxWeight, double maxArea) {
    if (instance == null) {
      instanceLock.lock();
      try {
        if (instance == null) {
          instance = new FerryImpl(maxWeight, maxArea);
        }
      } finally {
        instanceLock.unlock();
      }
    }
    return instance;
  }
  
  public static FerryImpl getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Ferry not initialized. Call getInstance(maxWeight, maxArea) first.");
    }
    return instance;
  }
  
  @Override
  public ReentrantLock getLock() {
    return lock;
  }
  
  @Override
  public boolean canLoadCar(Car car) {
    return (currentWeight + car.getWeight() <= maxWeight) && 
           (currentArea + car.getArea() <= maxArea);
  }
  
  @Override
  public void loadCar(Car car) {
    cars.add(car);
    currentWeight += car.getWeight();
    currentArea += car.getArea();
    logger.info("Car loaded: {}. Current: weight={}/{}, area={}/{}", 
        car, String.format("%.2f", currentWeight), String.format("%.2f", maxWeight), 
        String.format("%.2f", currentArea), String.format("%.2f", maxArea));
  }
  
  @Override
  public List<Car> unloadCars() {
    List<Car> unloaded = new ArrayList<>(cars);
    cars.clear();
    currentWeight = 0;
    currentArea = 0;
    logger.info("Unloaded {} cars", unloaded.size());
    return unloaded;
  }
  
  @Override
  public FerryState getState() {
    return state;
  }
  
  @Override
  public void setState(FerryState state) {
    this.state = state;
    logger.debug("Ferry state changed to: {}", state);
  }
  
  @Override
  public boolean isEmpty() {
    return cars.isEmpty();
  }
  
  @Override
  public int getCarCount() {
    return cars.size();
  }
  
  @Override
  public double getCurrentWeight() {
    return currentWeight;
  }
  
  @Override
  public double getCurrentArea() {
    return currentArea;
  }
  
  @Override
  public double getMaxWeight() {
    return maxWeight;
  }
  
  @Override
  public double getMaxArea() {
    return maxArea;
  }
}



