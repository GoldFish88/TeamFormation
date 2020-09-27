package exceptions;

@SuppressWarnings("serial")
public class NoLeaderException extends Exception {
	public NoLeaderException() {
		super("Team has no leader");
	}
	
	public NoLeaderException(String message) {
		super(message);
	}
}
