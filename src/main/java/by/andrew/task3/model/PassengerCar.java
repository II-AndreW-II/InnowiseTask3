package by.andrew.task3.model;

public class PassengerCar extends Car {
  public PassengerCar(String id, double weight, double area) {
    super(id, weight, area);
  }
  
  @Override
  public String getType() {
    return "PassengerCar";
  }
}



