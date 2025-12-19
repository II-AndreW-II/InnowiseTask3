package by.andrew.task3.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Car {
  private static final Logger logger = LogManager.getLogger();
  
  private final String id;
  private final double weight;
  private final double area;
  
  public Car(String id, double weight, double area) {
    this.id = id;
    this.weight = weight;
    this.area = area;
    logger.debug("Created car: id={}, weight={}, area={}", id, weight, area);
  }
  
  public String getId() {
    return id;
  }
  
  public double getWeight() {
    return weight;
  }
  
  public double getArea() {
    return area;
  }
  
  public abstract String getType();
  
  @Override
  public String toString() {
    return String.format("%s{id='%s', weight=%.2f, area=%.2f}", getType(), id, weight, area);
  }
}



