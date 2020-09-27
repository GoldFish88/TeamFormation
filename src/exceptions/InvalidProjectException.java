package exceptions;

@SuppressWarnings("serial")
public class InvalidProjectException extends Exception{

	public InvalidProjectException(String message) {
		super(message);
	}
}
