package by.andrew.task3.service;

import by.andrew.task3.model.Car;
import java.util.concurrent.locks.ReentrantLock;

public interface FerryService {
  boolean tryLoadCar(Car car);
  void waitForFerryReady() throws InterruptedException;
  void waitForFerryFull() throws InterruptedException;
  void startCrossing() throws InterruptedException;
  void waitForFerryArrival() throws InterruptedException;
  void shutdown();
  boolean isShutdown();
  ReentrantLock getServiceLock();
}

