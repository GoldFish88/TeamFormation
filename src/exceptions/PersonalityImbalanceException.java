package exceptions;

@SuppressWarnings("serial")
public class PersonalityImbalanceException extends Exception {
	public PersonalityImbalanceException() {
		super("Distinct personality types in the team is less than e");
	}
}
