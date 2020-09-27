package Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.*;

public class MainPageController {

	protected PrimaryModel pModel = null;
	
    @FXML
    private VBox sideBar;

    @FXML
    private Button studentBtn;

    @FXML
    private Button projectBtn;

    @FXML
    private Button metricBtn;

    @FXML
    private BorderPane menuPage;

    public MainPageController() {
    	pModel = PrimaryModel.getInstance();
    }
    
    @FXML
    void openMetrics(MouseEvent event) {
    	loadPage("MetricsPage");
    }	

    @FXML
    void openProjects(MouseEvent event) {
    	loadPage("TeamFormingPage");
    }

    @FXML
    void openStudents(MouseEvent event) {
    	loadPage("StudentsPage");
    }
    
    public void initialize() {
    	
    }
    
    public void loadPage(String page) {
    	Parent root = null;
    	
    	try {
			root = FXMLLoader.load(getClass().getResource("../view/" + page + ".fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	menuPage.setCenter(root);
    }

}
