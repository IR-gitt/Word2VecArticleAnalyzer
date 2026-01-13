package analyzerWord2Vec;

import analyzerWord2Vec.controllers.ControllerAppPane;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.Getter;

/**
 * Main application pane that contains UI components for text analysis.
 * Uses JavaFX for GUI rendering with two main views: input and results.
 */
public class AppPane {

    @Getter
    private BorderPane mainBP;
    private BorderPane resultBP;
    private BarChart<String, Number> barChart;
    private TextArea textArea;
    private Text resultMsg;
    private Button btRun;
    private Button btBack;
    private ControllerAppPane controllerAppPane;

    private static final int PANE_WIDTH = 600;
    private static final int PANE_HEIGHT = 500;
    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_HEIGHT = 30;
    private static final int TEXT_AREA_WIDTH = 500;
    private static final int TEXT_AREA_HEIGHT = 300;

    /**
     * Creates and initializes the application pane with all UI components.
     *
     * @return initialized Pane with main and result views
     * @throws Exception if controller initialization fails
     */
    public Pane createAppPane() throws Exception {
        var pane = new Pane();
        pane.setPrefSize(PANE_WIDTH, PANE_HEIGHT);

        createMainBorderPane();
        createResultBorderPane();

        pane.getChildren().addAll(mainBP, resultBP);

        controllerAppPane = new ControllerAppPane(
                pane, mainBP, resultBP, textArea,
                barChart, btRun, btBack, resultMsg
        );

        return pane;
    }

    /**
     * Creates the result view with bar chart and navigation.
     */
    private void createResultBorderPane() {
        resultBP = new BorderPane();
        resultBP.setPrefSize(PANE_WIDTH, PANE_HEIGHT);

        var gpBottomResultBP = new GridPane();
        var gpCenterResultBP = new GridPane();

        createBarChart();

        btBack = new Button("Back");
        btBack.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        resultMsg = new Text();

        // Layout configuration
        gpCenterResultBP.add(barChart, 0, 0);
        gpBottomResultBP.add(resultMsg, 0, 1);
        gpBottomResultBP.add(btBack, 0, 2);

        GridPane.setHalignment(btBack, HPos.CENTER);
        GridPane.setHalignment(barChart, HPos.CENTER);

        gpBottomResultBP.setAlignment(Pos.CENTER);
        gpCenterResultBP.setAlignment(Pos.CENTER);
        gpBottomResultBP.setHgap(10);
        gpBottomResultBP.setVgap(10);

        resultBP.setCenter(gpCenterResultBP);
        resultBP.setBottom(gpBottomResultBP);
        resultBP.setVisible(false);
    }

    /**
     * Creates the bar chart for displaying analysis results.
     */
    private void createBarChart() {
        var xAxis = new CategoryAxis();
        var yAxis = new NumberAxis();

        xAxis.setTickLabelRotation(45);
        xAxis.setLabel("Category");
        yAxis.setLabel("Similarity (%)");

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Text Analysis Results");
        barChart.setLegendVisible(true);
    }

    /**
     * Creates the main input view with text area and start button.
     */
    private void createMainBorderPane() {
        mainBP = new BorderPane();
        mainBP.setPrefSize(PANE_WIDTH, PANE_HEIGHT);

        var gpBottomMainMenu = new GridPane();
        var gpCenterMainMenu = new GridPane();

        btRun = new Button("Start Analysis");
        Platform.runLater(() -> btRun.requestFocus());
        btRun.setPrefSize(BUTTON_WIDTH + 30, BUTTON_HEIGHT);

        textArea = new TextArea();
        textArea.setPromptText("Enter or paste text for analysis");
        textArea.setPrefSize(TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
        textArea.setWrapText(true);

        gpCenterMainMenu.add(textArea, 0, 0);
        gpBottomMainMenu.add(btRun, 0, 0);

        gpBottomMainMenu.setAlignment(Pos.CENTER);
        gpCenterMainMenu.setAlignment(Pos.CENTER);

        mainBP.setCenter(gpCenterMainMenu);
        mainBP.setBottom(gpBottomMainMenu);
    }
}