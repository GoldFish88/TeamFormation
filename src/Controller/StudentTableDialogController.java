package Controller;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import model.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StudentTableDialogController {

	private PrimaryModel pModel;
	
    @FXML
    private TableView<Student> studentsTbl;

    @FXML
    private TableColumn<Student, String> idCol;

    @FXML
    private TableColumn<Student, String> nameCol;
    
    @FXML
    private TableColumn<Student, Integer> aCol;

    @FXML
    private TableColumn<Student, Integer> nCol;

    @FXML
    private TableColumn<Student, Integer> pCol;

    @FXML
    private TableColumn<Student, Integer> wCol;

    @FXML
    private TableColumn<Student, String> personalityCol;

    @FXML
    private TableColumn<Student, String> conflictsCol;

    @FXML
    private TableColumn<Student, String> prefCol;

    @FXML
    private Button loadBtn;

    @FXML
    private Button addPrefBtn;

    @FXML
    private Button addBtn;
    
    public StudentTableDialogController() {
		pModel = PrimaryModel.getInstance();
	}

    @FXML
    void initialize() {
    	updateTable();
    }
    
    @FXML
    void doAdd(MouseEvent event) {

    }

    @FXML
    void doAddPref(MouseEvent event) {
    	FileChooser fc = new FileChooser();
	  	Stage s = new Stage();
	  	s.initModality(Modality.APPLICATION_MODAL);
    	File f = fc.showOpenDialog(s);
    	if(f != null) {
    		pModel.readStudentPref(f);
    		updateTable();
    	}
    }

    @FXML
    void doLoad(MouseEvent event) {
    	FileChooser fc = new FileChooser();
	  	Stage s = new Stage();
	  	s.initModality(Modality.APPLICATION_MODAL);
    	File f = fc.showOpenDialog(s);
    	if(f != null) {
    		pModel.readStudentInfo(f);
    		//System.out.println(pModel.getCompanies());
    		updateTable();
    	}
    }
    
    public void updateTable() {
    	ObservableList<Student> students = FXCollections.observableArrayList();
    	students.addAll(pModel.getStudentsList());
    	
    	idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
    	nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
    	aCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("A")).asObject());
    	nCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("N")).asObject());
    	pCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("P")).asObject());
    	wCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGrades().get("W")).asObject());
    	personalityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersonality()));
    	conflictsCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().toStringBlackList()));
    	prefCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().toStringPref()));
    	
    	studentsTbl.setItems(students);
    }

}

