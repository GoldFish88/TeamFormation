package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import exceptions.*;

public class PrimaryModel implements Serializable{
	// singleton implementation
	private static PrimaryModel pmInstance = null;
	
	// Collections to hold data
	private HashMap<String, Student> students = new HashMap<String, Student>();
	private HashMap<String, Project> projects = new HashMap<String, Project>();
	private ArrayList<Company> companies = new ArrayList<Company>();
	private ArrayList<Owner> owners = new ArrayList<Owner>();
 	
	// constant objects lists
	private ArrayList<String> subjects = new ArrayList<String>();
	private ArrayList<String> personalityTypes = new ArrayList<String>();
	
	// constants
	// private final int pLimit = 5;
	private final int maxTeamSize = 4;
	private String dir = "src/recovery/";
	
	private PrimaryModel() {
		// load subjects
		subjects.add("P");
		subjects.add("N");
		subjects.add("A");
		subjects.add("W");

		// load personality types
		personalityTypes.add("A");
		personalityTypes.add("B");
		personalityTypes.add("C");
		personalityTypes.add("D");
	}
	
	public static PrimaryModel getInstance() {
		if(pmInstance == null)  
			pmInstance = new PrimaryModel(); 
		return pmInstance;
	}
		
	/*
	 * *************** GETTER METHODS *******************
	 */
	
	
	public int studentCount() {
		return students.size();
	}
	
	public int projectCount() {
		return projects.size();
	}
	
	public ArrayList<Student> getStudentsList(){
		ArrayList<Student> studentsList = new ArrayList<Student>();
		
		studentsList.addAll(students.values());
		
		return studentsList;
	}
	
	public ArrayList<String> getProjectTitles(){
		ArrayList<String> projectsList = new ArrayList<String>();
		
		for(Project project : projects.values()) {
			projectsList.add(project.getTitle());
		}
		
		return projectsList;
	}
	
	public ArrayList<Project> getProjectsAsList(){
		ArrayList<Project> projectsList = new ArrayList<Project>();
		
		projectsList.addAll(projects.values());
		
		return projectsList;
	}
	
	public ArrayList<Student> getAvailableStudents() {
		
		ArrayList<Student> studentsInTeam = new ArrayList<Student>();
		ArrayList<Student> availStudents = new ArrayList<Student>();
		
		// get all the students that are already in a team
		for(Project p : projects.values()) {
			studentsInTeam.addAll(p.getMembers());
		}
		
		// get all students
		for(Student s : students.values()) {
			availStudents.add(s);
		}
		
		availStudents.removeAll(studentsInTeam);
		
		return availStudents;
	}
	
	public Project getProject(String title) {
		Project rProject = null;
		
		for(Project p : projects.values()) {
			if(p.getTitle() == title)
				rProject = p;
		}
		
		return rProject;
	}
	
	public Student getStudent(String sId) {
		return students.get(sId);
	}
	
	public HashMap<String, Double> getSkillsGapXY(){
		HashMap<String, Double> skillsGap = new HashMap<String, Double>();
		
		for(Project p : projects.values()) {
			skillsGap.put(p.getId(), p.caclSkillShort());
		}
		
		return skillsGap;
	}
	
	public HashMap<String, Double> getGradesXY(){
		HashMap<String, Double> meanGrades = new HashMap<String, Double>();
		
		for(Project p : projects.values()) {
			meanGrades.put(p.getId(), p.calcSkillAve());
		}
		
		return meanGrades;
	}
	
	public HashMap<String, Double> getPrefPercentageXY(){
		HashMap<String, Double> prefPercentage = new HashMap<String, Double>();
		
		for(Project p : projects.values()) {
			prefPercentage.put(p.getId(), p.calcPrefPercentage());
		}
		
		return prefPercentage;
	}
	
	public double getStd(HashMap<String, Double> data) {
		double sd = 0;
		double sum = 0;
		double mean = 0;
		
		// compute mean
		for(Double val : data.values()) {
			sum += val;
		}
		mean = sum/data.size();
		
		// if mean > 0, compute sd
		if(mean > 0) {
			sum = 0;
			for(Double val : data.values()) {
				sum += Math.pow((val - mean), 2);
			}
			sd = Math.sqrt(sum/projects.size());
		}
		return sd;
	}
	
	public ArrayList<Company> getCompanies() {
		return companies;
	}
	
	public ArrayList<Owner> getOwners() {
		return owners;
	}
	
	public int getMaxTeamSize() {
		return maxTeamSize;
	}
	
	public int getShortlistCount() {
		int n = 0;
		
		if(students.size() > 0) {
			n = Math.floorDiv(students.size(), maxTeamSize);
		}
		
		return n;
	}
	
	public int getExcessStudents() {
		int n = 0;
		
		if(getShortlistCount() > 0) {
			n = students.size() % getShortlistCount();
		}
		
		return n;
	}
	
	public int getCompanyCount() {
		return companies.size();
	}
	
	public int getOwnerCount() {
		return owners.size();
	}
	
	/*
	 * *************** TEAM METHODS *******************
	 */	
	public void swapMembers(Project p1, Project p2, Student s1, Student s2) throws NoLeaderException, 
	PersonalityImbalanceException, StudentConflictException {
		// memory for temporary teams
		ArrayList<Student> tempTeam1 = new ArrayList<Student>();
		ArrayList<Student> tempTeam2 = new ArrayList<Student>();
		
    	// make temporary teams
    	tempTeam1.addAll(p1.getMembers());
    	tempTeam1.remove(s1);
    	tempTeam1.add(s2);
    	
    	tempTeam2.addAll(p2.getMembers());
    	tempTeam2.remove(s2);
    	tempTeam2.add(s1);
		
    	validateTeam(tempTeam1);
    	validateTeam(tempTeam2);
    	
		p1.removeMember(s1);
		p2.removeMember(s2);
		
		p1.addMember(s2);
		p2.addMember(s1);
	}
	
	public void addMember(Project p, Student s) throws MaxSizeException, 
	NoLeaderException, PersonalityImbalanceException, RepeatedMemberException, StudentConflictException {
		// validate team if member size = max size - 1
		if(p.getMembers().size() == maxTeamSize)
			throw new MaxSizeException();
		
		// check for repeated member
		if(p.getMembers().contains(s))
			throw new RepeatedMemberException();
		
		// do validation if team is final team
		if(p.getMembers().size() == maxTeamSize - 1) {
			// create temporary team for validation
			ArrayList<Student> temp = new ArrayList<Student>();
			temp.addAll(p.getMembers());
			temp.add(s);
			// successful validation, add student
			validateTeam(temp);
		}
		
		p.addMember(s);
	}
	
	public void validateTeam(ArrayList<Student> team) throws NoLeaderException, 
	PersonalityImbalanceException, StudentConflictException {		
		Set<String> personalities = new HashSet<String>();

		for(Student s: team) {
			personalities.add(s.getPersonality());
			hasConflict(s, team);
		}
		
		// check if no leader
		if(!personalities.contains("A")) 
			throw new NoLeaderException();

		// check for personality imbalance
		if(personalities.size() < 3)
			throw new PersonalityImbalanceException();
		
	}
	
	public boolean inAnotherTeam(Student student, Project project) {
		// create map of all projects except this project
		Map<String, Project> other = projects.entrySet()
				.stream()
				.filter(e -> e.getKey().compareTo(project.getId()) != 0)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
				
		for(String pId : other.keySet()) {
			if(projects.get(pId).isMember(student)) {
				return true; 
			}		
		}
		
		return false;
	}
	
	public void hasConflict(Student student, ArrayList<Student> members) throws StudentConflictException {
		
		for(Student studentY : members) {
			if(studentY.inConflict(student.getId())) 
				throw new StudentConflictException(studentY.getId() + " in conflict with " + student.getId());
			
			if(student.inConflict(studentY.getId()))
				throw new StudentConflictException(studentY.getId() + " in conflict with " + student.getId());
		}
	}
	
	/*
	 * *************** DATA HANDLING METHODS *******************
	 */
	
	public void readCompany(File f) {
		try {
			// clear companies collection in model
			companies.clear();
			
			Scanner sc = new Scanner(f);
			
			// retrieve information from text file and put them on map
			while(sc.hasNextLine()) {
				// split each line with max 5 elements
				String[] s = sc.nextLine().split(",", 5);
				
				// create company if complete details
				if(s.length == 5) {
					Company temp = new Company(s[0], s[1], s[2], s[3], s[4]);
					companies.add(temp);
				}	
			}			
			sc.close();
		}
		catch(Exception e) {
			
		}
	}
	
	public void readOwner(File f) {
		try {
			// clear owners collection in model
			owners.clear();
			
			Scanner sc = new Scanner(f);
			
			// retrieve information from text file and put them on map
			while(sc.hasNextLine()) {
				// split each line with max 5 elements
				String[] s = sc.nextLine().split(",", 6);
				
				// create company if complete details
				if(s.length == 6) {
					Owner temp = new Owner(s[0].strip(), 
							s[1].strip(),
							s[2].strip(),
							s[3].strip(), 
							s[4].strip(), 
							s[5].strip());
					owners.add(temp);
				}	
			}			
			sc.close();
		}
		catch(Exception e) {
			
		}
	}
	
	
	/**
	 * method to read student details from students.txt and put them on students
	 * 
	 */
	public void readStudents(File f) {
		try {
			// empty students map before loading the file
			students.clear();
			Scanner sc = new Scanner(f);
			
			// retrieve information from text file and put them on map
			while(sc.hasNextLine()) {
				String[] s = sc.nextLine().split(" ", 9);

				// handle blank lines by skipping them
				if(s.length == 1 && s[0].compareToIgnoreCase("") == 0) {
					continue;
				}
				
				String id = s[0];			 	
				Student temp = new Student(id);
				
				// add student grades
			 	for(int i = 1; i< s.length; i+=2) {
			 		temp.addGrade(s[i], Integer.parseInt(s[i+1]));
			 	}
			 	
				// put student to students
				students.put(id, temp);
			}			
			sc.close();
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("students.txt not found!");
		}
		catch(Exception e) {
			System.out.print("Something went wrong");
		}	
	}
	
	/**
	 * Read additional information from studentsinfo.txt and adds the additional
	 * information to students. The readStudents method should be called before
	 * this method to ensure that all students are on the system before adding 
	 * personality and blacklist.
	 */
	public void readStudentInfo(File f) {
		try {
			Scanner sc = new Scanner(f);
			
			// loop through each line
			while(sc.hasNextLine()) {
				String[] s = sc.nextLine().split(" ", 13);
				
				String id = s[0];	
				String name = s[1];
				Student temp = new Student(id, name);
				
				// add student grades
			 	for(int i = 2; i < 10; i+=2) {
			 		String subj = s[i];
			 		int grade = Integer.parseInt(s[i+1]);
			 		temp.addGrade(subj, grade);
			 	}
				
				String personality = s[10];
				
				// add preference
				temp.setPersonality(personality);
				
				if(s.length >= 12) {
					temp.addStudent(s[11]);
				}
				
				if(s.length == 13) {
					temp.addStudent(s[12]);
				}		
				
				// put student to students
				students.put(id, temp);
				System.out.println(temp.getName());
			}
			sc.close();
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("studentsinfo.txt not found!");
		}
		
	}
	
	/**
	 * Loads preference.txt to students. The readStudents() method should 
	 * be called before calling this method.
	 */
	public void readStudentPref(File f) {
		try {
			Scanner sc = new Scanner(f);
			
			// loop through each line
			while(sc.hasNextLine()) {
				// get student id from file
				String id = sc.nextLine().strip();
				
				// get student with student Id = id
				Student student = students.get(id);
				
				// add preferences from file
				String[] pref = sc.nextLine().strip().split(" ", 8);
				for(int i = 0; i < pref.length; i+=2) {
					student.addPref(pref[i], Integer.parseInt(pref[i+1]));
				}
				
				// put student to students
				students.put(id, student);
			}
			
			sc.close();
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("preferences.txt not found!");
		}
		catch(Exception e) {
			System.out.print("Something went wrong");
		}
	}

	
	/**
	 * method to read project details from project.txt and put them on projects
	 * 
	 */
	public void readProjects(File f) {
		try {
			Scanner sc = new Scanner(f);
			
			// clear projects
			projects.clear();
			
			// loop through each line
			while(sc.hasNextLine()) {
				// split line on ","
				String[] p = sc.nextLine().split(",", 5);
				// handle blank lines by skipping them
				if(p.length == 1 && p[0].compareToIgnoreCase("") == 0) {
					continue;
				}
				// extract company details
				String title = p[0].strip(); 
			    String id = p[1].strip();
			    String description = p[2].strip();
			    String ownerId = p[3].strip();
			 	
			    // create temporary project
			 	Project project = new Project(title, id, description, ownerId);
			 	
			 	String[] skillsPref = p[4].strip().split(" ");
			 	for(int i = 0; i< skillsPref.length; i+=2) {
			 		project.setPref(skillsPref[i], Integer.parseInt(skillsPref[i+1]));
			 	}
			 	
				// put project to projects
				projects.put(id, project);
			}
			
			sc.close();
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("projects.txt not found!");
		}
		catch(Exception e) {
			System.out.print("Something went wrong");
		}
	}
	
	/*
	 * *************** SERIALIZATION METHODS **************
	 */
	
	public void save(String fName) throws FileNotFoundException, IOException {
		
		// add file extension if needed
		if(!fName.endsWith(".ser")) {
			fName += ".ser";
		}
		
		ObjectOutputStream out = new ObjectOutputStream
                (new FileOutputStream(dir + fName));
		out.writeObject(companies);
		out.writeObject(owners);
		out.writeObject(students);
		out.writeObject(projects);
		out.close();
	}
	
	public void recover(File recover) throws ClassNotFoundException, FileNotFoundException, IOException {

		ObjectInputStream oIn = new ObjectInputStream(new FileInputStream(recover));
		
		companies = (ArrayList<Company>) oIn.readObject();
		owners = (ArrayList<Owner>) oIn.readObject();
		students =  (HashMap<String, Student>) oIn.readObject();
		projects = (HashMap<String, Project>) oIn.readObject();
		System.out.println("Recovered data from recovery.ser");

		// print recovered students
		System.out.println("Students from recovery file:");
		for(Student s : students.values()) {
			System.out.println(s.toStudentInfo());
		}
		System.out.println();

		// print recovered projects
		System.out.println("Projects from recovery file:");
		for(Project p : projects.values()) {
			System.out.println(p.toString());
		}
		System.out.println();

		oIn.close();
	}
	
	public void recover(String fName) throws ClassNotFoundException, FileNotFoundException, IOException {

		// add file extension if needed
		if(!fName.endsWith(".ser")) {
			fName += ".ser";
		}
		
		ObjectInputStream oIn = new ObjectInputStream(new FileInputStream(dir + fName));

		companies = (ArrayList<Company>) oIn.readObject();
		owners = (ArrayList<Owner>) oIn.readObject();
		students =  (HashMap<String, Student>) oIn.readObject();
		projects = (HashMap<String, Project>) oIn.readObject();
		System.out.println("Recovered data from recovery.ser");

		// print recovered students
		System.out.println("Students from recovery file:");
		for(Student s : students.values()) {
			System.out.println(s.toStudentInfo());
		}
		System.out.println();

		// print recovered projects
		System.out.println("Projects from recovery file:");
		for(Project p : projects.values()) {
			System.out.println(p.toString());
		}
		System.out.println();

		oIn.close();
	}
	
	/*
	 * *********** DATABASE METHODS *****************
	 */
	
	public void saveToDatabase(String dbName) {
		
		// add file extension if needed
		if(!dbName.endsWith(".db")) {
			dbName += ".db";
		}
		System.out.println(dbName);
		String url = "jdbc:sqlite:" + dir + dbName;
		try {
			Connection conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			
			// drop table statements
			st.execute("drop table IF EXISTS Students;");
			st.execute("drop table IF EXISTS Projects;");
			st.execute("drop table IF EXISTS Subjects;");
			st.execute("drop table IF EXISTS Members;");
			st.execute("drop table IF EXISTS Grades;");
			st.execute("drop table IF EXISTS Companies;");
			st.execute("drop table IF EXISTS Owners;");
			
			// create table statements
			st.execute("CREATE TABLE Companies ("
					+ "c_id TEXT PRIMARY KEY,"
					+ "name TEXT,"
					+ "abn TEXT,"
					+ "url TEXT,"
					+ "address TEXT);");
			System.out.println("Created Companies Table");
			
			st.execute("CREATE TABLE Owners ("
					+ "o_id TEXT PRIMARY KEY,"
					+ "first_name TEXT,"
					+ "last_name TEXT,"
					+ "role TEXT,"
					+ "email TEXT,"
					+ "c_id TEXT,"
					+ "FOREIGN KEY(c_id) REFERENCES Companies);");
			System.out.println("Created Owners Table");
			
			st.execute("create table Students ("
					+ "s_id TEXT PRIMARY KEY,"
					+ "name TEXT,"
					+ "personality TEXT);");
			System.out.println("Created Students Table");
			
			st.execute("CREATE TABLE Projects ("
					+ "p_id TEXT PRIMARY KEY,"
					+ "title TEXT NOT NULL,"
					+ "description TEXT,"
					+ "o_id TEXT,"
					+ "FOREIGN KEY(o_id) REFERENCES Owners);");
			System.out.println("Created Projects Table");
			
			st.execute("CREATE TABLE Subjects ("
					+ "subject TEXT PRIMARY KEY);");
			System.out.println("Created Subjects Table");
			
			st.execute("CREATE TABLE Members ("
					+ "s_id TEXT,"
					+ "p_id TEXT,"
					+ "PRIMARY KEY(s_id, p_id),"
					+ "FOREIGN KEY(s_id) REFERENCES Students,"
					+ "FOREIGN KEY(p_id) REFERENCES Projects);");
			System.out.println("Created Members Table");
			
			st.execute("CREATE TABLE Grades ("
					+ "s_id TEXT,"
					+ "subject TEXT,"
					+ "grade INTEGER,"
					+ "PRIMARY KEY(s_id, subject),"
					+ "FOREIGN KEY(s_id) REFERENCES Students,"
					+ "FOREIGN KEY(subject) REFERENCES Subjects);");
			System.out.println("Created Grades Table");
			
			// populate tables
			writeCompanies(st);
			writeOwners(st);
			writeSubjects(st);
			writeStudents(st);
			writeProjects(st);
			
			conn.close();
			System.out.println("Sent");
		}
		catch(SQLException se) {
			se.printStackTrace();
		}
	}
	
	public void writeCompanies(Statement st) {
		for(Company c: companies) {
			// insert into Companies table
			try {
				st.executeUpdate("INSERT INTO Companies (c_id, name, abn, url, address)"
						+ "VALUES('" + c.getId() 
						+ "','" + c.getName() 
						+ "','" + c.getAbn()
						+ "','" + c.getUrl()
						+ "','" + c.getAddress() + "');");
				System.out.println("Inserted " + c.getId());
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeOwners(Statement st) {
		for(Owner o: owners) {
			// insert into Companies table
			try {
				st.executeUpdate("INSERT INTO Owners (o_id, first_name, last_name, role, email, c_id)"
						+ "VALUES('" + o.getId() 
						+ "','" + o.getFirstName()
						+ "','" + o.getLastName()
						+ "','" + o.getRole()
						+ "','" + o.getEmail()
						+ "','" + o.getCompanyId() + "');");
				System.out.println("Inserted " + o.getId());
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeSubjects(Statement st) {
		for(String subj : subjects) {
			try {
				st.executeUpdate("INSERT INTO Subjects (subject)"
						+ "VALUES('" + subj + "');");
				System.out.println("Inserted " + subj);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeStudents(Statement st) {
		for(String sId : students.keySet()) {
			Student s = students.get(sId);
			
			try {
				// insert into Students table
				st.executeUpdate("INSERT INTO Students (s_id, name, personality)"
						+ "VALUES('" + s.getId() + "','" + s.getName() + "','" + s.getPersonality() + "');");
				System.out.println("Inserted " + s.getId());
				// insert into grades
				HashMap<String, Integer> grades = s.getGrades();
				for(String subject : grades.keySet()) {
					st.executeUpdate("INSERT INTO Grades (s_id, subject, grade)"
							+ "VALUES('" + s.getId() + "','" + subject + "','" + grades.get(subject) + "');");
					System.out.println("Inserted Grade: " + s.getId() + " " + subject);
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeProjects(Statement st) {
		for(String pId : projects.keySet()) {
			Project p = projects.get(pId);
			
			try {
				// insert into Projects
				st.executeUpdate("INSERT INTO Projects (p_id, title, description, o_id)"
						+ "VALUES('" + pId + "','" + p.getTitle() + "','" + p.getDescription()
						+ "','" + p.getOwnerId() +"');");
				System.out.println("Inserted: " + p.getId());
				
				// insert into Members
				for(Student s : p.getMembers()) {
					st.executeUpdate("INSERT INTO Members (s_id, p_id)"
							+ "VALUES('" + s.getId() + "','" + pId + "');");
					System.out.println("Inserted Member: " + pId + " " + s.getId());
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
