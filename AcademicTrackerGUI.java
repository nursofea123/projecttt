package ProjectCsc3104;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class AcademicTrackerGUI extends Application {

	//stores weightings fora each sibject and its components
    private final Map<String, Map<String, Double>> subjectWeightings = new HashMap<>();
    //stores marks input by the user for each component
    private final Map<String, Double> marksObtained = new HashMap<>();
    //label to display calculation result
    private Label resultLabel = new Label();
    //for user input of predicted final exam marks 
    private TextField finalExamInput = new TextField();
    
    //list of grade and the score 
    private final String[] GradeScore = {"A", "A-", "B+", "B", "B-", "C"};
    private final double[] GradeThresholds = {75, 70, 65, 60, 55, 0}; // Minimum scores for each grade
    //combobox for selected desired grade 
    private ComboBox<String> cbo = new ComboBox<>();
    //main layout pane
    private BorderPane pane = new BorderPane();

    @Override
    public void start(Stage primaryStage) {
        // Initialize subject weightings
        initializeSubjectWeightings();

        // ComboBox Setup
        cbo.setPrefWidth(200);
        //populate combobox with grade options
        ObservableList<String> items = FXCollections.observableArrayList(GradeScore);
        cbo.getItems().addAll(items);
        cbo.setValue(GradeScore[0]); // Set default value (A)

        // Pane for ComboBox
        BorderPane paneForComboBox = new BorderPane();
        paneForComboBox.setLeft(new Label("Select a grade: "));
        paneForComboBox.setCenter(cbo);

        // Final Exam Input
        HBox finalExamBox = new HBox(10);
        finalExamBox.setPadding(new Insets(10));
        finalExamBox.getChildren().addAll(new Label("Predicted Final Exam (/30):"), finalExamInput);

        // Buttons for Subjects
        VBox buttonPanel = new VBox(10);
        buttonPanel.setPadding(new Insets(10));
        Button csc3300Button = new Button("CSC 3300");
        Button csc3400Button = new Button("CSC 3400");
        Button csc3202Button = new Button("CSC 3202");
        Button csc3104Button = new Button("CSC 3104");
        Button cnsxxxButton = new Button("CNS XXX");

        buttonPanel.getChildren().addAll(csc3300Button, csc3400Button, csc3202Button, csc3104Button, cnsxxxButton);
        pane.setLeft(buttonPanel);

        // Content Area
        VBox contentArea = new VBox(10);
        contentArea.setPadding(new Insets(10));
        pane.setCenter(contentArea);

        // Add Action Listeners for subject buttons
        csc3300Button.setOnAction(e -> displaySubjectInput("CSC 3300", contentArea));
        csc3400Button.setOnAction(e -> displaySubjectInput("CSC 3400", contentArea));
        csc3202Button.setOnAction(e -> displaySubjectInput("CSC 3202", contentArea));
        csc3104Button.setOnAction(e -> displaySubjectInput("CSC 3104", contentArea));
        cnsxxxButton.setOnAction(e -> displaySubjectInput("CNS XXX", contentArea));

        // Scene Setup
        VBox topArea = new VBox(10, paneForComboBox, finalExamBox);
        pane.setTop(topArea);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("Academic Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Initializes the weightings for different subjects and their components.
     */
    private void initializeSubjectWeightings() {
        subjectWeightings.put("CSC 3300", Map.of("Test1", 15.0, "Test2", 15.0, "Quizzes", 10.0, "Lab", 20.0, "GroupAssignment", 10.0));
        subjectWeightings.put("CSC 3400", Map.of("Test1", 10.0, "Test2", 10.0, "Lab", 30.0, "Assignment", 20.0));
        subjectWeightings.put("CSC 3202", Map.of("Test1", 20.0, "Quizzes", 15.0, "Assignment", 25.0));
        subjectWeightings.put("CSC 3104", Map.of("Test", 20.0, "Project", 20.0, "Lab", 30.0));
        subjectWeightings.put("CNS XXX", Map.of("Test1", 20.0, "Lab", 30.0, "Project", 20.0));
    }

    private void displaySubjectInput(String subjectName, VBox contentArea) {
        contentArea.getChildren().clear();

        Map<String, Double> components = subjectWeightings.get(subjectName);

        contentArea.getChildren().add(new Label("Enter marks for " + subjectName + ":"));
        for (String component : components.keySet()) {
            HBox inputRow = new HBox(10);
            Label componentLabel = new Label(component + " (" + components.get(component) + "%):");
            TextField componentInput = new TextField();
            inputRow.getChildren().addAll(componentLabel, componentInput);
            contentArea.getChildren().add(inputRow);

            componentInput.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double mark = Double.parseDouble(newValue);
                    marksObtained.put(component, mark);
                } catch (NumberFormatException ex) {
                    marksObtained.remove(component);
                }
            });
        }

        Button calculateButton = new Button("Calculate");
        calculateButton.setOnAction(e -> calculateResult(subjectName));
        contentArea.getChildren().addAll(calculateButton, resultLabel);
    }

    private void calculateResult(String subjectName) {
        Map<String, Double> components = subjectWeightings.get(subjectName);
        double carryMark = 0.0;

        // Calculate carry marks based on input
        for (String component : components.keySet()) {
            carryMark += marksObtained.getOrDefault(component, 0.0);
        }

        // Parse the selected grade threshold
        int selectedIndex = cbo.getSelectionModel().getSelectedIndex();
        double targetGrade = 0.0;
        switch (selectedIndex) {
            case 0: targetGrade = 75; break; // A
            case 1: targetGrade = 70; break; // A-
            case 2: targetGrade = 65; break; // B+
            case 3: targetGrade = 60; break; // B
            case 4: targetGrade = 55; break; // B-
            default: targetGrade = 50; break; // C
        }
        // Parse predicted final exam score from user input
        // Read predicted final exam score
        double predictedFinalExamScore = 0.0;
        try {
            predictedFinalExamScore = Double.parseDouble(finalExamInput.getText());
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid final exam prediction (0-30).");
            return;
        }

        // Total score and feedback
        double totalScore = carryMark + predictedFinalExamScore;
        if (totalScore >= targetGrade) {
            resultLabel.setText(String.format(
                "Carry Mark: %.2f/70\nFinal Exam: %.2f/30\nTotal: %.2f/100\nCongratulations! You meet the grade %s threshold.",
                carryMark, predictedFinalExamScore, totalScore, cbo.getValue()
            ));
        } else {
            double marksNeeded = targetGrade - totalScore;
            resultLabel.setText(String.format(
                "Carry Mark: %.2f/70\nFinal Exam: %.2f/30\nTotal: %.2f/100\nYou need %.2f more marks to achieve grade %s.",
                carryMark, predictedFinalExamScore, totalScore, marksNeeded, cbo.getValue()
            ));
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
