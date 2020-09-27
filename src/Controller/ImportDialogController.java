package Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.PrimaryModel;

public class ImportDialogController {

	private PrimaryModel pModel = null;
	private File recoveryDir = new File("src/recovery/");

    @FXML
    private ListView<String> filesList;

    @FXML
    private Button importBtn;
    
    @FXML
    void initialize() {
    	pModel = PrimaryModel.getInstance();
    	System.out.println("I'm here");
    	updateFilesList();
    }
    
    public void updateFilesList() {
    	// create filename filters to select .ser and .db files respectively
    	FilenameFilter serFilter = new FilenameFilter() {
    		public boolean accept(File dir, String name) {
    			if(name.toLowerCase().endsWith(".ser")) 
    				return true;
    			else
    				return false;
    		}
    	};
    	
    	ObservableList<String> serFileList = FXCollections.observableArrayList();
    	
    	// get files from recovery directory
    	String serFiles[] = recoveryDir.list(serFilter);
    	for(String file: serFiles) {
    		serFileList.add(file);
    		System.out.println("file");
    	}
    	
    	filesList.setItems(serFileList);
    }
    
    @FXML
    void importFile(MouseEvent event) {
    	String fName = filesList.getSelectionModel().getSelectedItem();
    	if(fName != null) {
    		try {
				pModel.recover(fName);
				filesList.getScene().getWindow().hide();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}