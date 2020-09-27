package exceptions;

@SuppressWarnings("serial")
public class RepeatedMemberException extends Exception {
	public RepeatedMemberException() {
		super("A team member has been repeated");
	}
}
