package Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import model.PrimaryModel;

public class ExportDialogController {

	private PrimaryModel pModel = null;
	private File recoveryDir = new File("src/recovery/");
	
	@FXML
	private RadioButton serRadioBtn;

	@FXML
	private ToggleGroup exportTo;

	@FXML
	private TextField serFileName;

	@FXML
	private ListView<String> serList;

	@FXML
	private RadioButton dBradioBtn;

	@FXML
	private TextField dbFileName;

	@FXML
	private ListView<String> dbList;

	@FXML
	private Button doExportBtn;

    public ExportDialogController() {
		pModel = PrimaryModel.getInstance();
	}
    
    @FXML
    void initialize() {
    	serRadioBtn.setSelected(true);
    	
    	dbFileName.setDisable(true);
    	dbList.setDisable(true);
    	
    	loadRecoveryFiles();
    }
    
    public void loadRecoveryFiles() {
    	// create filename filters to select .ser and .db files respectively
    	FilenameFilter serFilter = new FilenameFilter() {
    		public boolean accept(File dir, String name) {
    			if(name.toLowerCase().endsWith(".ser")) 
    				return true;
    			else
    				return false;
    		}
    	};
    	
    	FilenameFilter dbFilter = new FilenameFilter() {
    		public boolean accept(File dir, String name) {
    			if(name.toLowerCase().endsWith(".db"))
    				return true;
    			else
    				return false;
    		}
    	};
    	
    	// get files from recovery directory
    	String serFiles[] = recoveryDir.list(serFilter);
    	String dbFiles[] = recoveryDir.list(dbFilter);
    	
    	// populate listview with files 
    	ObservableList<String> serFileList = FXCollections.observableArrayList();
    	ObservableList<String> dbFileList = FXCollections.observableArrayList();
    	
    	for(String file: serFiles) {
    		serFileList.add(file);
    	}
    	for(String file: dbFiles) {
    		dbFileList.add(file);
    	}
    	
    	serList.setItems(serFileList);
    	dbList.setItems(dbFileList);
    	
    	// add listener to entries of list view
    	serList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
    		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    			serFileName.setText(newValue);
    		}
    	});
    	
    	dbList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
    		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    			dbFileName.setText(newValue);
    		}
    	});
    	
    }
    // https://www.tutorialspoint.com/how-to-get-list-of-all-files-folders-from-a-folder-in-java#:~:text=The%20ListFiles()%20method,file%2Fdirectory%20in%20a%20folder.
    // https://stackoverflow.com/questions/12459086/how-to-perform-an-action-by-selecting-an-item-from-listview-in-javafx-2
    	
    @FXML
    void exportFile(MouseEvent event) {
    	if(serRadioBtn.isSelected()) {
    		if(serFileName.getText() != null) {
    			try {
					pModel.save(serFileName.getText());
					serFileName.getScene().getWindow().hide();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	else if(dBradioBtn.isSelected()) {
    		if(dbFileName.getText() != null) {
    			pModel.saveToDatabase(dbFileName.getText());
    			serFileName.getScene().getWindow().hide();
    		}
    		
    	}
    }

    @FXML
    void toggleDb(MouseEvent event) {
    	serFileName.setDisable(true);
    	serList.setDisable(true);
    	
    	dbFileName.setDisable(false);
    	dbList.setDisable(false);
    }

    @FXML
    void toggleSer(MouseEvent event) {
    	serFileName.setDisable(false);
    	serList.setDisable(false);
    	
    	dbFileName.setDisable(true);
    	dbList.setDisable(true);
    }
}

