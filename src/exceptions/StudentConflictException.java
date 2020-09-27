package exceptions;

@SuppressWarnings("serial")
public class StudentConflictException extends Exception {

	public StudentConflictException() {
		super("Students assigned are in conflict");
	}
	
	public StudentConflictException(String message) {
		super(message);
	}
}
