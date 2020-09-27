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

public class ProjectTableDialogController {

	private PrimaryModel pModel;
	
    @FXML
    private TableView<Project> projectTbl;

    @FXML
    private TableColumn<Project, String> idCol;

    @FXML
    private TableColumn<Project, String>titleCol;

    @FXML
    private TableColumn<Project, String>descripCol;

    @FXML
    private TableColumn<Project, String>ownerCol;

    @FXML
    private TableColumn<Project, Integer> aCol;

    @FXML
    private TableColumn<Project, Integer> nCol;

    @FXML
    private TableColumn<Project, Integer> pCol;

    @FXML
    private TableColumn<Project, Integer> wCol;

    @FXML
    private Button loadBtn;

    @FXML
    private Button addBtn;

    public ProjectTableDialogController() {
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
    void doLoad(MouseEvent event) {
    	FileChooser fc = new FileChooser();
	  	Stage s = new Stage();
	  	s.initModality(Modality.APPLICATION_MODAL);
    	File f = fc.showOpenDialog(s);
    	if(f != null) {
    		pModel.readProjects(f);
    		//System.out.println(pModel.getCompanies());
    		updateTable();
    	}
    }
    
    public void updateTable() {
    	ObservableList<Project> projects = FXCollections.observableArrayList();
    	projects.addAll(pModel.getProjectsAsList());
    	
    	idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
    	titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
    	descripCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
    	ownerCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOwnerId()));
    	aCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSkillsPref().get("A")).asObject());
    	nCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSkillsPref().get("N")).asObject());
    	pCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSkillsPref().get("P")).asObject());
    	wCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSkillsPref().get("W")).asObject());
    	
    	projectTbl.setItems(projects);
    }
}
