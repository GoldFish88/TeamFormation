package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Project implements Serializable{

	// variables
	private String title; 
    private String id; 
    private String description;
    private String ownerId;
    private LinkedHashMap<String, Integer> skillsPref = new LinkedHashMap<String, Integer>();
    
	private ArrayList<Student> members = new ArrayList<Student>();
	private final int maxTeamSize = 4;
    
    // constructor
	public Project(String id) {
		this.id = id;
	}
	
    public Project(String title, String id, String description, String ownerId) {
    	this.title = title;
    	this.id = id;
    	this.description = description;
    	this.ownerId = ownerId;
    }
    
    public String getId() {
		return id;
	}
    
    public String getTitle() {
		return title;
	}
    
    public String getDescription() {
		return description;
	}
    
    public String getOwnerId() {
		return ownerId;
	}
    
    public LinkedHashMap<String, Integer> getSkillsPref(){
    	return(skillsPref);
    }
    
    public void setPref(String subject, Integer pref) {
    	skillsPref.put(subject, pref);
    }
    
    public ArrayList<Student> getMembers() {
		return members;
	}
    
    public ArrayList<String> getMemberIds(){
    	ArrayList<String> res = new ArrayList<String>();
    	
    	for(Student s : members) {
    		res.add(s.getId());
    	}
    	
    	return res;
    }
    
    public ArrayList<String> getConflictList(){
    	ArrayList<String> res = new ArrayList<String>();
    	
    	for(Student s : members) {
    		for(String sId : s.getBlackList()) {
    			if(!res.contains(sId)) {
    				res.add(sId);
    			}
    		}
    	}
    	
    	return res;
    }
    
    public Integer getNumMembers() {
    	return members.size();
    }
    
    public boolean removeMember(Student sX) {
    	return members.remove(sX);
    }
    
    public String prefToString() {
    	// convert linked hash map of preference to space separated string
    	String skillString = "";
		for(String key : skillsPref.keySet()) {
			skillString += key + " " + Integer.toString(skillsPref.get(key)) + " ";
		}
		return skillString.strip();
    }
    
    public String toString() {
    	// convert preference hashmap to space separated string for writing
    	return(title + ", " + id + ", " + description + ", " + ownerId + "," + prefToString());
    }
    
	public boolean addMember(Student student) {
		if(members.size() >= maxTeamSize) {
			return false;
		}
		
		members.add(student);
		//System.out.println("Added " + student.getId());
		return true;
	}
	
	public boolean isMember(Student s1) {
		return members.contains(s1);
	}
	
	public double calcSkillAve() {
		double total = 0;
		int size = members.size();
		double res = 0;
		
		if(size > 0) {
			for(Student s: members) {
				total = total + s.getGradeAverage();
			}
			res = total/size;
		}


		return res;
	}
	
	public double calcPrefPercentage() {
		int yesCounter = 0;
		double size = (double) members.size();
		double res = 0;
		
		if(size > 0) {
			for(Student s : members) {
				if(s.inTop2(id)) {
					yesCounter++;
				}
			}
			res = (double) yesCounter/size;
		}
		
		return res;
	}
	
	public double caclSkillShort() {
		double skillShort = 0;
		HashMap<String, Double> ave = calcAveByCategory();
		
//		for(Student s: members) {
//			skillShort += s.calcShortFall(skillsPref);
//		}
		
		for(String subject : ave.keySet()) {
			skillShort += Math.max(skillsPref.get(subject) - ave.get(subject), 0);
//			if(skillsPref.get(subject) > ave.get(subject)) {
//
//				skillShort += (skillsPref.get(subject) - ave.get(subject));
//			}
		}
		
		return skillShort;
	}
	
	public HashMap<String, Double> calcAveByCategory() {
		HashMap<String, Double> ave = new HashMap<String,Double>();
		double size = (double) members.size();
		// loop through each student
		for(Student s: members) {
			// loop through each subject
			for(String subject : s.getGrades().keySet()) {
				// get grade for subject
				int grade = s.getGrades().get(subject);
				// put in HashMap
				ave.compute(subject, (key, val) -> 
							(val == null) ? 
							(grade/size) : 
							val + (grade/size));
			}
		}
		
		return ave;
	}
	
	public boolean isFull() {
		return (members.size() == maxTeamSize);
	}
     	
}
