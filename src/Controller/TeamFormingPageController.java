package Controller;

import java.io.IOException;

import exceptions.MaxSizeException;
import exceptions.NoLeaderException;
import exceptions.PersonalityImbalanceException;
import exceptions.RepeatedMemberException;
import exceptions.StudentConflictException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import model.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TeamFormingPageController {

	private PrimaryModel pModel = null;
	
    @FXML
    private Button loadBtn;

    @FXML
    private Label maxTeamLbl;
    
    @FXML
    private Label shortlistLbl;

    @FXML
    private Label ExcessLbl;
    
    @FXML
    private Button studentBtn;
    
    @FXML
    private Label studentCountLbl;

    @FXML
    private Button projectBtn;
    
    @FXML
    private Label projectCountLbl;

    @FXML
    private Button companyBtn;
    
    @FXML
    private Label companiesCountLbl;

    @FXML
    private Button ownerBtn;
    
    @FXML
    private Label ownersCountLbl;

    @FXML
    private TableView<Student> studentTable;
    
    @FXML
    private TableColumn<Student, String> sIdCol;

    @FXML
    private TableColumn<Student, Integer> aGradeCol;

    @FXML
    private TableColumn<Student, Integer> nGradeCol;

    @FXML
    private TableColumn<Student, Integer> pGradeCol;

    @FXML
    private TableColumn<Student, Integer> wGradeCol;

    @FXML
    private TableColumn<Student, Double> aveGradeCol;

    @FXML
    private TableColumn<Student, String> personalityCol;

    @FXML
    private TableColumn<Student, String> prefCol;


    @FXML
    private Button addBtn;

    @FXML
    private TableView<Project> projectTable;
    
    @FXML
    private TableColumn<Project, String> pIdCol;

    @FXML
    private TableColumn<Project, String> titleCol;

    @FXML
    private TableColumn<Project, Integer> sizeCol;

    @FXML
    private ListView<Student> memberList;
    
    @FXML
    private Button shortlistBtn;

    @FXML
    private Button removeBtn;

    @FXML
    private TableView<Student> recommendTable;
    
    @FXML
    private TableColumn<Student, String> recoIdCol;

    @FXML
    private TableColumn<Student, Integer> gapCol;

    @FXML
    private TableColumn<Student, Boolean> isPrefCol;

    @FXML
    private ListView<String> conflicList;
    
    public TeamFormingPageController() {
		pModel = PrimaryModel.getInstance();
	}
    
    @FXML
    void initialize() {
    	updateStudentTable();
    	updateProjectTable();
    	updateStats();
    }
    
    public void loadAll() {
    	updateStudentTable();
    	updateProjectTable();
    	updateStats();
    }
    
    public void updateStats() {
    	// load
    	maxTeamLbl.setText(String.valueOf(pModel.getMaxTeamSize()));
    	
    	// student
    	studentCountLbl.setText(String.valueOf(pModel.studentCount()));
    	
    	// project
    	projectCountLbl.setText(String.valueOf(pModel.projectCount()));
    	shortlistLbl.setText(String.valueOf(pModel.getShortlistCount()));
    	ExcessLbl.setText(String.valueOf(pModel.getExcessStudents()));
    	
    	// companies
    	companiesCountLbl.setText(String.valueOf(pModel.getCompanyCount()));
    	
    	// owners
    	ownersCountLbl.setText(String.valueOf(pModel.getOwnerCount()));
    }
    
    /** Update methods **/
    public void updateStudentTable() {
    	// get all student from primary model and add it to an observableList
    	ObservableList<Student> students = FXCollections.observableArrayList();
    	students.addAll(pModel.getAvailableStudents());
    	
    	// set CellValueFactory to studentTable
    	sIdCol.setCellValueFactory(new PropertyValueFactory<Student, String>("id"));
    	aGradeCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("A")).asObject());
    	nGradeCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("N")).asObject());
    	pGradeCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("P")).asObject());
    	wGradeCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("W")).asObject());
    	aveGradeCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getGradeAverage()).asObject());
    	personalityCol.setCellValueFactory(new PropertyValueFactory<Student, String>("personality"));
    	prefCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().toStringPreferences()));
    	
    	studentTable.setItems(students);
    	
    }
    
    public void updateProjectTable() {
    	// get all projects in primary model and add it to an observableList
    	projectTable.getItems().clear();
    	ObservableList<Project> projects = FXCollections.observableArrayList();
    	projects.addAll(pModel.getProjectsAsList());
    	
    	// set set CellValueFactory of projectTable columns
    	pIdCol.setCellValueFactory(new PropertyValueFactory<Project, String>("id"));
    	titleCol.setCellValueFactory(new PropertyValueFactory<Project, String>("title"));
    	sizeCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumMembers()).asObject());
    	
    	// add listener
    	projectTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Project>() {
    		public void changed(ObservableValue<? extends Project> observable, Project oldValue, Project newValue) {
    			updateMemberList();
    			updateBlackList();
    			// updateRecommendedList();
    		}
    	});
    	
    	projectTable.setItems(projects);
    	

    }
    
    public void updateMemberList() {
    	// get selected project
    	Project selected = projectTable.getSelectionModel().getSelectedItem();
    	
    	if(selected != null) {
    		// get members and put into observableList
        	ObservableList<Student> members = FXCollections.observableArrayList();
        	members.addAll(selected.getMembers());
        	
        	memberList.setItems(members);
    	}
    }
    
    public void updateRecommendedList() {
    	
    }
    
    public void updateBlackList() {
    	// get selected project
    	Project selected = projectTable.getSelectionModel().getSelectedItem();
    	
    	if(selected != null) {
        	// get members and put into observableList
        	ObservableList<String> conflicts = FXCollections.observableArrayList();
        	conflicts.addAll(selected.getConflictList());
        	
        	conflicList.setItems(conflicts);	
    	}
    }

    @FXML
    void addSelected(MouseEvent event) {
    	// get selected items
    	Student s = studentTable.getSelectionModel().getSelectedItem();
    	Project p = projectTable.getSelectionModel().getSelectedItem();
    	
    	if(s != null && p != null) {
    		// do add selected
        	try {
    			pModel.addMember(p, s);
    		} catch (MaxSizeException e) {
    			showError(e);
    		} catch (NoLeaderException e) {
    			// TODO Auto-generated catch block
    			showError(e);
    		} catch (PersonalityImbalanceException e) {
    			// TODO Auto-generated catch block
    			showError(e);
    		} catch (RepeatedMemberException e) {
    			// TODO Auto-generated catch block
    			showError(e);
    		} catch (StudentConflictException e) {
    			// TODO Auto-generated catch block
    			showError(e);
    		}
        	
        	// update views
        	updateMemberList();
        	updateBlackList();
        	updateStudentTable();
        	updateProjectTable();
    	}
    }


    @FXML
    void doShortlist(MouseEvent event) {

    }
    
    @FXML
    void removeSelected(MouseEvent event) {
    	// get selected items
    	Student sId = memberList.getSelectionModel().getSelectedItem();
    	Project p = projectTable.getSelectionModel().getSelectedItem();
    	
    	if(sId != null && p != null) {
        	p.removeMember(sId);
        	
        	updateMemberList();
        	updateBlackList();
        	updateStudentTable();
        	updateProjectTable();
    	}
    }

    @FXML
    void showCompany(MouseEvent event) {
    	openDialog("Companies", "CompanyTableDialog", 600, 400);
    }

    @FXML
    void showOwner(MouseEvent event) {
    	openDialog("Owners", "OwnerTableDialog", 600, 400);
    }

    @FXML
    void showProject(MouseEvent event) {
    	openDialog("Projects", "ProjectTableDialog", 600, 400);
    }

    @FXML
    void showStudent(MouseEvent event) {
    	openDialog("Students", "StudentTableDialog", 900, 600);
    }
    
    @FXML
    void doImport(MouseEvent event) {
    	openDialog("Import", "ImportDialog", 350, 400);
    }
    
	public void showError(Exception e) {
		Alert a = new Alert(AlertType.ERROR, e.getMessage());
		
		a.show();
	}

	public void openDialog(String title, String view, int w, int l) {
		try {
		  	Stage importStage = new Stage();
		  	importStage.initModality(Modality.APPLICATION_MODAL);
		  	importStage.setTitle(title);
			
			Parent root = FXMLLoader.load(getClass().getResource("../view/" + view +  ".fxml"));
			
			Scene scene = new Scene(root,w,l);
			
			importStage.setScene(scene);
			importStage.showAndWait();
			loadAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
}
