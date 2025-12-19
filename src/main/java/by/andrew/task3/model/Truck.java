package by.andrew.task3.model;

public class Truck extends Car {
  public Truck(String id, double weight, double area) {
    super(id, weight, area);
  }
  
  @Override
  public String getType() {
    return "Truck";
  }
}



