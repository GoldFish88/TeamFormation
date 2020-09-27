package Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import exceptions.*;

public class MetricsPageController{

	private PrimaryModel pModel = null;
	
    @FXML
    private TextField sdGapField;
	
	@FXML
    private BarChart<String, Double> skillsBarGraph;

    @FXML
    private TextField sdGradeField;
	
    @FXML
    private BarChart<String, Double> gradesBarGraph;

    @FXML
    private TextField sdPrefField;
    
    @FXML
    private BarChart<String, Double> preferenceBarGraph;

    @FXML
    private TableView<Project> projectsTable;

    @FXML
    private TableColumn<Project, String> id;

    @FXML
    private TableColumn<Project, String> title;
    
    @FXML
    private TableColumn<Project, Integer> size;
    
    @FXML
    private Label p1Label;

    @FXML
    private Label p1Id;

    @FXML
    private Label p1Owner;

    @FXML
    private Button p1SelectBtn;

    @FXML
    private ListView<String> p1StudentsList;

    @FXML
    private Label p2Label;

    @FXML
    private Label p2Id;

    @FXML
    private Label p2Owner;

    @FXML
    private Button p2SelectButton;
    
    @FXML
    private ListView<String> p2StudentsList;

    @FXML
    private LineChart<String, Integer> gradesLineGraph;

    @FXML
    private TextField s1CurrentFit;

    @FXML
    private TextField s1NewFit;

    @FXML
    private TextField s1Preferences;

    @FXML
    private TextField s2CurrentFit;

    @FXML
    private TextField s2NewFit;

    @FXML
    private TextField s2Preferences;
    
    @FXML
    private TextField s1IdField;

    @FXML
    private TextField s2IdField;
    
    @FXML
    private Label head;
    
    @FXML
    private Button swapBtn;
    
    @FXML
    private Button previewBtn;
    
    @FXML
    private Button exportBtn;

    @FXML
    private Button balanceBtn;
   
    @FXML
    private Button importBtn;
    

    public MetricsPageController() {
    	pModel = PrimaryModel.getInstance();
    }
    
    @FXML
    public void initialize() {
    	updateProjectsTable();
    	updateCharts();
    }
    
    public void updateProjectsTable() {
    	ObservableList<Project> projects = getProjects();
    	
    	id.setCellValueFactory(new PropertyValueFactory<Project, String>("id"));
    	title.setCellValueFactory(new PropertyValueFactory<Project, String>("title"));
    	size.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumMembers()).asObject());
    	
    	projectsTable.setItems(projects);	
    }
    
    public void updateCharts() {
    	// get data
    	HashMap<String, Double> skillsGap = pModel.getSkillsGapXY();
    	HashMap<String, Double> grades = pModel.getGradesXY();
    	HashMap<String, Double> prefPercentage = pModel.getPrefPercentageXY();
    	
    	// set-up data for XYChart 
    	XYChart.Series<String, Double> gapDataSeries = new XYChart.Series<String, Double>();
    	XYChart.Series<String, Double> gradesDataSeries = new XYChart.Series<String, Double>();
    	XYChart.Series<String, Double> prefDataSeries = new XYChart.Series<String, Double>();
    	
    	gapDataSeries.setName("Skills Gap");
    	gradesDataSeries.setName("Grades");
    	prefDataSeries.setName("in Top 2");
    	for(String pId : skillsGap.keySet()) {
    		gapDataSeries.getData().add(new XYChart.Data<String, Double>(pId, skillsGap.get(pId)));
    		gradesDataSeries.getData().add(new XYChart.Data<String, Double>(pId, grades.get(pId)));
    		prefDataSeries.getData().add(new XYChart.Data<String, Double>(pId, prefPercentage.get(pId)));
    	}
    	
    	// populate charts with data series
    	skillsBarGraph.getData().clear();
    	skillsBarGraph.getData().add(gapDataSeries);
    	
    	gradesBarGraph.getData().clear();
    	gradesBarGraph.getData().add(gradesDataSeries);
    	
    	preferenceBarGraph.getData().clear();
    	preferenceBarGraph.getData().add(prefDataSeries);
    	
    	// compute standard deviations
    	sdGapField.setText(String.format("%.2f", pModel.getStd(skillsGap)));
    	sdGradeField.setText(String.format("%.2f", pModel.getStd(grades)));
    	sdPrefField.setText(String.format("%.2f", pModel.getStd(prefPercentage)));
    	
    }
    
    @FXML
    void setProject1(MouseEvent event) {
    	Project p1 = projectsTable.getSelectionModel().getSelectedItem();
    	
    	// update project information
    	p1Id.setText(p1.getId());
    	p1Label.setText(p1.getTitle());
    	p1Owner.setText(p1.getOwnerId());
    	
    	updateStudentList(p1StudentsList, p1);
    }
    
    @FXML
    void setProject2(MouseEvent event) {
    	Project p2 = projectsTable.getSelectionModel().getSelectedItem();
    	// update project information
    	p2Id.setText(p2.getId());
    	p2Label.setText(p2.getTitle());
    	p2Owner.setText(p2.getOwnerId());
    	
    	updateStudentList(p2StudentsList, p2);
    }
    
    public void updateStudentList(ListView<String> sl, Project p) {
    	ObservableList<String> members = FXCollections.observableArrayList();
    	sl.setItems(members);
    	
    	for(Student s : p.getMembers()) {
    		sl.getItems().add(s.getId());
    	}
    }
    
    @FXML
    void swapSelected(MouseEvent event) {
    	try {
        	//get Id of members to swap
        	String s1Id = p1StudentsList.getSelectionModel().getSelectedItem();
        	String s2Id = p2StudentsList.getSelectionModel().getSelectedItem();
        	
        	if(p1Label.getText() == null || p2Label.getText() == null) {
        		throw new SwapException("Either both or one of the projects have not yet been selected");
        	}
        	
        	if(p1Label.getText() == p2Label.getText()) {
        		throw new SwapException("Can't swap members of the same team");
        	}
        	
        	if(s1Id == null || s2Id == null) {
        		throw new SwapException("Select members to swap");
        	}
        	
        	// get selected projects
    		Project p1 = pModel.getProject(p1Label.getText());
        	Project p2 = pModel.getProject(p2Label.getText());
        	
        	//get students to swap
        	Student s1 = pModel.getStudent(s1Id);
        	Student s2 = pModel.getStudent(s2Id);

        	pModel.swapMembers(p1, p2, s1, s2);
        	
        	// update views
        	updateStudentList(p1StudentsList, p1);
        	updateStudentList(p2StudentsList, p2);
        	updateCharts();
    	}
    	catch(SwapException se) {
    		showError(se);
    	} catch (StudentConflictException e) {
    		showError(e);
		} catch (NoLeaderException e) {
			showError(e);
		} catch (PersonalityImbalanceException e) {
			showError(e);
		}
    }

    public void showError(Exception e) {
		Alert a = new Alert(AlertType.ERROR, e.getMessage());
		
		a.show();
	}
    
    @FXML
    void previewSelected(MouseEvent event) {
    	// clear preview
    	clearStudentMetrics();
    	
    	// get selected details
    	String s1Id = p1StudentsList.getSelectionModel().getSelectedItem();
    	String s2Id = p2StudentsList.getSelectionModel().getSelectedItem();
    	
    	// get selected projects
		Project p1 = pModel.getProject(p1Label.getText());
    	Project p2 = pModel.getProject(p2Label.getText());
    	
    	// if p1 has selected, update graph and metrics
    	if(s1Id != null) {
    		Student s1 = pModel.getStudent(s1Id);
    		updateGradesLineGraph(s1);
    		updateStudentMetrics(s1, p1, p2, s1CurrentFit, s1NewFit, s1Preferences, s1IdField);
    	}
    	
    	// if p2 has selected, update graph and metrics
    	if(s2Id != null) {
    		Student s2 = pModel.getStudent(s2Id);
    		updateGradesLineGraph(s2);
    		updateStudentMetrics(s2, p2, p1, s2CurrentFit, s2NewFit, s2Preferences, s2IdField);
    	}
    }
    
    public void updateGradesLineGraph(Student s) {
    	// get data
    	TreeMap<String, Integer> grades = new TreeMap<String, Integer>();
    	grades.putAll(s.getGrades());
    	//HashMap<String, Integer> grades = s.getGrades();
    	
    	// set-up data for XYChart
    	XYChart.Series<String, Integer> gradesDataSeries = new XYChart.Series<String, Integer>();
    	
    	gradesDataSeries.setName(s.getId());
    	for(String subject : grades.keySet()) {
    		gradesDataSeries.getData().add(new XYChart.Data<String, Integer>(subject, grades.get(subject)));
    	}
    	gradesLineGraph.getData().add(gradesDataSeries);
    }
    
    public void updateStudentMetrics(Student s, Project p1, Project p2, 
    		TextField cFit, TextField nFit, TextField pref, TextField head) {
    	// set header
    	head.setText(s.getId());
    	// set current fit text field
    	cFit.setText(String.valueOf(s.calcShortFall(p1.getSkillsPref())));

    	// set new fit text field
    	nFit.setText(String.valueOf(s.calcShortFall(p2.getSkillsPref())));

    	// set preference field
    	pref.setText(s.toStringPreferences());
    	
    }
    
    public void clearStudentMetrics() {
    	// clear line graph
    	gradesLineGraph.getData().clear();
    	
    	// clear metrics of s1
    	s1CurrentFit.clear();
    	s1NewFit.clear();
    	s1Preferences.clear();
    	
    	// clear metrics of s2
    	s2CurrentFit.clear();
    	s2NewFit.clear();
    	s2Preferences.clear();
    }
    
    // get all projects from the main model
    public ObservableList<Project> getProjects(){
    	ObservableList<Project> projects = FXCollections.observableArrayList();
    	projects.addAll(pModel.getProjectsAsList());
    	
    	return projects;
    }
    
    @FXML
    void doBalance(MouseEvent event) {

    }

    @FXML
    void doExport(MouseEvent event) {
		try {
		  	Stage exportStage = new Stage();
	    	exportStage.setTitle("Export to..");
	    	exportStage.initModality(Modality.APPLICATION_MODAL);
			
			Parent root = FXMLLoader.load(getClass().getResource("../view/ExportDialog.fxml"));
			
			Scene scene = new Scene(root,870,380);
			// primaryStage.setScene(scene);
	    	exportStage.setScene(scene);
	    	exportStage.show();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void doImport(MouseEvent event) {
    	try {
		  	Stage importStage = new Stage();
		  	importStage.initModality(Modality.APPLICATION_MODAL);
		  	importStage.setTitle("Import file..");
			
			Parent root = FXMLLoader.load(getClass().getResource("../view/ImportDialog.fxml"));
			
			Scene scene = new Scene(root,350,400);
			
			importStage.setScene(scene);
			importStage.showAndWait();
			initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
}

