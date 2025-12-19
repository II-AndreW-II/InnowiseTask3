package by.andrew.task3.exception;

public class FerryException extends Exception{
    
  public FerryException() {
  }
  
  public FerryException(String message) {
    super(message);
  }
  
  public FerryException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public FerryException(Throwable cause) {
    super(cause);
  }
}