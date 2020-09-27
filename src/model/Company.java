package model;

import java.io.Serializable;

public class Company implements Serializable {

	// variables
 	private String id;
 	private String name;
 	private String abn;
 	private String url;
 	private String address;
 	
 	// constructor
 	public Company(String id, String name, String abn, String url, String address) {
		this.id = id;
		this.name = name;
		this.abn = abn;
		this.url = url;
		this.address = address;
	}
 	
 	public String getId() {
		return id;
	}
 	
 	public String getName() {
		return name;
	}
 	
 	public String getAbn() {
		return abn;
	}
 	
 	public String getUrl() {
		return url;
	}
 	
 	public String getAddress() {
		return address;
	}
 	
 	public String toString() {
 		return(id + ", " + name + ", " + abn + ", " + url + ", " + address);
 	}
 	
 	
}
