package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Student implements Serializable {
	//variables
	private String id;
	private String name;
	private HashMap<String, Integer> grades = new HashMap<String, Integer>();
    private String personality;
    private HashSet<String> blackList = new HashSet<String>();
    private HashMap<String, Integer> projPref = new HashMap<String, Integer>();
 
    
    public Student(String id) {
    	this.id = id;
    	this.personality = "";
    }
    
    public Student(String id, String name) {
    	this.id = id;
    	this.name = name;
    	this.personality = "";
    }
    
    /*
     *  ********** GETTER METHODS *************
     */
    
    public String getPersonality() {
		return personality;
	}
    
    public String getId() {
		return id;
	}
    
    public HashMap<String, Integer> getProjPref() {
		return projPref;
	}
    
    public HashSet<String> getBlackList() {
    	return blackList;
    }
    
    public void setPersonality(String personality) {
		this.personality = personality;
	}
    
    public HashMap<String, Integer> getGrades() {
		return grades;
	}
    
    public String getName() {
		return name;
	}
    
    /*
     *  ********** ADD TO COLLECTIONS METHODS *************
     */
    
    public void addGrade(String subject, Integer grade) {
    	grades.put(subject, grade);
    }
    
    public void addPref(String projId, Integer pref) {
    	String rId = null;
    	
    	// remove entry if preference is already assigned
    	if(projPref.containsValue(pref)) {
    		for(String oId : projPref.keySet()) {
    			Integer preference = projPref.get(oId);
    			if(preference == pref) {
    				rId = oId;
    			}
    		}
    	}
    	
    	if(rId != null) {
    		System.out.println("Updated preference " + pref + " : " + rId + " -> " + projId);
			projPref.remove(rId);
    	}
    	Integer prev = projPref.put(projId, pref);
    	
    	// print replacement done
    	if (prev != null) {
    		System.out.println("Updated " + projId + " : " + prev + " -> " + pref);
    	}
    }
    
    public boolean addStudent(String studentId) {
    	
    	if(id.compareToIgnoreCase(studentId) == 0) {
    		System.out.println("Can't add self to blacklist");
    		return false;
    	}
    	
    	blackList.add(studentId);

    	return true;
    }
    
    /*
     *  ********** TO STRING METHODS *************
     */
//    public String toString() {
//    	return(id + " " + toStringGrades());
//    }
    public String toString() {
    	return (id);
    }
    
	// convert student information to space separated string
    public String toStringGrades() {
    	String gradeString = "";
		for(String key : grades.keySet()) {
			gradeString += key + " " + Integer.toString(grades.get(key)) + " ";
		}
    	return gradeString.strip();
    }
    
	// convert project preference to space separated string
    public String toStringPref() {
    	String prefString = "";
		for(String key : projPref.keySet()) {
			prefString += key + " " + Integer.toString(projPref.get(key)) + " ";
		}
		return prefString.strip();
    }
    
    public String toStringBlackList() {
    	String prefString = "";
    	
    	if(!blackList.isEmpty()) {
        	Iterator<String> it = blackList.iterator();
        	while(it.hasNext()) {
        		prefString += it.next() + " ";
        	}
    	}
		return prefString.strip();
    }
    
	// convert to student to a line entry of studentinfo.txt
    public String toStudentInfo() {
		return((id + " " + toStringGrades() + " " + personality + " " + toStringBlackList()).strip());
    }
    
	// convert to student to a line entry of preferences.txt
    public String toStudentPref() {
		return(id + "\n" + 
				toStringPref());
    }
    
    public String toStringPreferences() {
    	String res = "";
    	
    	for(String p : projPref.keySet()) {
    		res += p + " ";
    	}
    	
    	return res;
    }
    
    public boolean inPreference(String projectId) {
    	if(projPref.containsKey(projectId)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public void clearBlackList() {
    	blackList.clear();
    }
    
    public double getGradeAverage() {
    	double ave = 0;
    	int size = grades.size();
    	for(String subject : grades.keySet()) {
    		double grade = (double) grades.get(subject);
    		ave += grade/size;
    	}

		return ave;
    }
    
    public boolean inTop2(String pId) {
    	// sort in descending order
    	Map<String, Integer> sorted = projPref.entrySet()
    			.stream()
    			.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
    			.collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    	

    	// get top 2
    	int counter = 0;
    	for(String p : sorted.keySet()) {
    		if(counter < 2) {
    			if(p.compareTo(pId) == 0) {
    				return true;
    			}
    		}
    		counter++;
    	}	
    	return false;
    }
    
    public boolean inConflict(String sId) {
    	return blackList.contains(sId);
    }
    
    /**
     * calculates the gap between the expectation and students skill set.
     * 
     * @param skillsPref
     * @return (+) integer means under qualified, (-) means overqualified
     */
    public int calcShortFall(Map<String, Integer> skillsPref) {
    	
    	int gap = 0;
    	
    	for(String s : skillsPref.keySet()) {
    		gap += Math.max(skillsPref.get(s) - grades.get(s), 0);
    		//gap += (skillsPref.get(s) - grades.get(s));
    	}
    	
    	return gap;
    }
    
     	
}
