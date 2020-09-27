package exceptions;

@SuppressWarnings("serial")
public class InvalidStudentException extends Exception {

	public InvalidStudentException() {
		super("Student not in system");
	}
}
