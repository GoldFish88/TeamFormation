package exceptions;

@SuppressWarnings("serial")
public class InvalidMemberException extends Exception {
	public InvalidMemberException() {
		super("Student has already been assigned to another team");
	}
}
