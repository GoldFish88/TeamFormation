package Controller;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import model.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OwnerTableDialogController {

	private PrimaryModel pModel;
	
    @FXML
    private TableView<Owner> ownerTbl;

    @FXML
    private TableColumn<Owner, String> idCol;

    @FXML
    private TableColumn<Owner, String>fNameCol;

    @FXML
    private TableColumn<Owner, String>lNameCol;

    @FXML
    private TableColumn<Owner, String> roleCo;

    @FXML
    private TableColumn<Owner, String> emailCol;

    @FXML
    private TableColumn<Owner, String> companyCol;

    @FXML
    private Button loadBtn;

    @FXML
    private Button addBtn;

    public OwnerTableDialogController() {
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
    		pModel.readOwner(f);
    		
    		updateTable();
    	}
    }

    public void updateTable() {
    	ObservableList<Owner> owners = FXCollections.observableArrayList();
    	owners.addAll(pModel.getOwners());
    	
    	idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
    	fNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName()));
    	lNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));
    	roleCo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
    	emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
    	companyCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCompanyId()));
    	
    	ownerTbl.setItems(owners);
    }
    
}

