package by.andrew.task3.service.impl;

import by.andrew.task3.ferry.Ferry;
import by.andrew.task3.ferry.FerryState;
import by.andrew.task3.model.Car;
import by.andrew.task3.service.FerryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FerryServiceImpl implements FerryService {
  private static final Logger logger = LogManager.getLogger();
  private final Ferry ferry;
  private final ReentrantLock serviceLock;
  private final Condition ferryReady;
  private final Condition ferryFull;
  private final Condition ferryArrived;
  private final Condition ferryCanStart;
  private boolean shutdown;
  
  public FerryServiceImpl(Ferry ferry) {
    this.ferry = ferry;
    this.serviceLock = new ReentrantLock();
    this.ferryReady = serviceLock.newCondition();
    this.ferryFull = serviceLock.newCondition();
    this.ferryArrived = serviceLock.newCondition();
    this.ferryCanStart = serviceLock.newCondition();
    this.shutdown = false;
  }
  
  @Override
  public boolean tryLoadCar(Car car) {
    serviceLock.lock();
    try {
      if (shutdown) {
        logger.debug("Car {} cannot be loaded: ferry service is shutdown", car.getId());
        return false;
      }
      
      ReentrantLock ferryLock = ferry.getLock();
      ferryLock.lock();
      try {
        while (!shutdown && ferry.getState() != FerryState.WAITING && ferry.getState() != FerryState.LOADING) {
          logger.debug("Car {} waiting for ferry to be ready", car.getId());
          ferryReady.await();
        }
        
        if (shutdown) {
          logger.debug("Car {} cannot be loaded: ferry service is shutdown", car.getId());
          return false;
        }
        
        if (ferry.canLoadCar(car)) {
          ferry.setState(FerryState.LOADING);
          ferry.loadCar(car);
          logger.debug("Car {} loaded. Ferry: {}/{} weight, {}/{} area", 
              car.getId(), ferry.getCurrentWeight(), ferry.getMaxWeight(), 
              ferry.getCurrentArea(), ferry.getMaxArea());
          
          ferryReady.signalAll();
          ferryCanStart.signalAll();
          return true;
        } else {
          logger.debug("Car {} cannot be loaded: ferry is full", car.getId());
          ferryCanStart.signalAll();
          return false;
        }
      } finally {
        ferryLock.unlock();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Thread interrupted while loading car {}", car.getId(), e);
      return false;
    } finally {
      serviceLock.unlock();
    }
  }
  
  @Override
  public void waitForFerryReady() throws InterruptedException {
    serviceLock.lock();
    try {
      while (!shutdown && ferry.getState() != FerryState.WAITING && ferry.getState() != FerryState.LOADING) {
        ferryReady.await();
      }
    } finally {
      serviceLock.unlock();
    }
  }
  
  @Override
  public void waitForFerryFull() throws InterruptedException {
    serviceLock.lock();
    try {
      ferryFull.await();
    } finally {
      serviceLock.unlock();
    }
  }
  
  @Override
  public void startCrossing() throws InterruptedException {
    serviceLock.lock();
    try {
      ReentrantLock ferryLock = ferry.getLock();
      ferryLock.lock();
      try {
        boolean waited = false;
        while (!shutdown && ferry.isEmpty() && ferry.getState() == FerryState.WAITING) {
          if (!waited) {
            logger.debug("Ferry operator waiting for cars to load");
            waited = true;
          }
          boolean signaled = ferryCanStart.await(2, TimeUnit.SECONDS);
          if (!signaled && ferry.isEmpty()) {
            logger.debug("Ferry is empty and no cars arrived, skipping trip");
            return;
          }
        }
        
        if (shutdown) {
          logger.debug("Ferry service is shutdown, skipping trip");
          return;
        }
        
        if (!ferry.isEmpty()) {
          ferry.setState(FerryState.CROSSING);
          logger.info("Ferry started crossing with {} cars", ferry.getCarCount());
          ferryReady.signalAll();
        } else {
          logger.debug("Ferry is empty, skipping trip");
          return;
        }
      } finally {
        ferryLock.unlock();
      }
      
      TimeUnit.SECONDS.sleep(2);
      
      ferryLock.lock();
      try {
        ferry.setState(FerryState.UNLOADING);
        int carCount = ferry.getCarCount();
        ferry.unloadCars();
        logger.info("Ferry arrived, unloaded {} cars", carCount);
        ferry.setState(FerryState.WAITING);
        logger.info("Ferry is ready for next trip");
        ferryArrived.signalAll();
        ferryReady.signalAll();
        ferryCanStart.signalAll();
      } finally {
        ferryLock.unlock();
      }
    } finally {
      serviceLock.unlock();
    }
  }
  
  @Override
  public void waitForFerryArrival() throws InterruptedException {
    serviceLock.lock();
    try {
      while (ferry.getState() != FerryState.WAITING) {
        ferryArrived.await();
      }
    } finally {
      serviceLock.unlock();
    }
  }
  
  @Override
  public void shutdown() {
    serviceLock.lock();
    try {
      shutdown = true;
      logger.info("Ferry service is shutting down");
      ferryReady.signalAll();
      ferryFull.signalAll();
      ferryArrived.signalAll();
      ferryCanStart.signalAll();
    } finally {
      serviceLock.unlock();
    }
  }
  
  @Override
  public boolean isShutdown() {
    serviceLock.lock();
    try {
      return shutdown;
    } finally {
      serviceLock.unlock();
    }
  }
  
  @Override
  public ReentrantLock getServiceLock() {
    return serviceLock;
  }
}

