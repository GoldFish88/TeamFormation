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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CompanyTableDialogController {

	private PrimaryModel pModel;
	
    @FXML
    private TableView<Company> companyTbl;

    @FXML
    private TableColumn<Company, String> idCol;

    @FXML
    private TableColumn<Company, String> nameCol;

    @FXML
    private TableColumn<Company, String> abnCol;

    @FXML
    private TableColumn<Company, String> urlCol;

    @FXML
    private TableColumn<Company, String>addressCol;
    
    @FXML
    private Button loadBtn;

    @FXML
    private Button addBtn;

    public CompanyTableDialogController() {
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
    		pModel.readCompany(f);
    		//System.out.println(pModel.getCompanies());
    		updateTable();
    	}
    }
    
    public void updateTable() {
    	ObservableList<Company> companies = FXCollections.observableArrayList();
    	companies.addAll(pModel.getCompanies());
    	
    	idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
    	nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
    	abnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAbn()));
    	urlCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUrl()));
    	addressCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
    	
    	companyTbl.setItems(companies);
    }

}
