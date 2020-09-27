package exceptions;

public class MaxSizeException extends Exception {
	public MaxSizeException(String message) {
		super(message);
	}
	
	public MaxSizeException() {
		super("Team is full");
	}
}
