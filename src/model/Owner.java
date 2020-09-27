package model;

import java.io.Serializable;

public class Owner implements Serializable{
	// variables
 	private String firstName; 
 	private String lastName;
 	private String id;
 	private String role;
 	private String email;
 	private String companyId;
 	
 	// constructor
 	public Owner(String fName, String lName, String id, String role, String email, String companyId) {
		this.firstName = fName;
		this.lastName = lName;
		this.id = id;
		this.role = role;
		this.email = email;
		this.companyId = companyId;
	}
 	
 	public String getId() {
		return id;
	}
 	
 	public String getFirstName() {
		return firstName;
	}
 	
 	public String getLastName() {
		return lastName;
	}
 	
 	public String getRole() {
		return role;
	}
 	
 	public String getEmail() {
		return email;
	}
 	
 	public String getCompanyId() {
		return companyId;
	}
 	
 	public String toString() {
 		return(firstName + ", " + lastName + ", " + id + 
 				", " + role + ", " + email + ", " + companyId);
 	}
}
