package analyzerWord2Vec.controllers;

import analyzerWord2Vec.ArticleAnalyzer;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing application pane interactions and analysis workflow.
 * Handles user actions and coordinates between UI and analysis logic.
 */
public class ControllerAppPane {

    private static final Logger logger = Logger.getLogger(ControllerAppPane.class.getName());

    private final Pane pane;
    private final BarChart<String, Number> barChart;
    private final BorderPane mainBP;
    private final TextArea textArea;
    private final Button btRun;
    private final Button btBack;
    private final Text resultMsg;
    private final BorderPane resultBP;

    /**
     * Constructs controller with UI components.
     *
     * @param pane main container pane
     * @param mainBP main border pane for input view
     * @param resultBP result border pane for results view
     * @param textArea text input area
     * @param barChart chart for displaying results
     * @param btRun start analysis button
     * @param btBack back navigation button
     * @param resultMsg text element for result message
     */
    public ControllerAppPane(Pane pane, BorderPane mainBP, BorderPane resultBP,
                             TextArea textArea, BarChart<String, Number> barChart,
                             Button btRun, Button btBack, Text resultMsg) {
        this.pane = pane;
        this.mainBP = mainBP;
        this.resultBP = resultBP;
        this.barChart = barChart;
        this.btBack = btBack;
        this.btRun = btRun;
        this.resultMsg = resultMsg;
        this.textArea = textArea;
        initialize();
    }

    /**
     * Initializes event handlers for UI components.
     */
    private void initialize() {
        setActionBtStart();
        setActionBtBack();
    }

    /**
     * Sets up back button action to return to main view.
     */
    private void setActionBtBack() {
        btBack.setOnAction(event -> {
            // Clear previous chart data
            barChart.getData().clear();
            mainBP.setVisible(true);
            resultBP.setVisible(false);
        });
    }

    /**
     * Sets up start button action to initiate text analysis.
     * Performs analysis asynchronously to avoid blocking UI.
     */
    public void setActionBtStart() {
        btRun.setOnAction(event -> {
            var textForAnalyzer = textArea.getText();

            if (textForAnalyzer == null || textForAnalyzer.trim().isEmpty()) {
                showAlert("Input Error", "Please enter text for analysis");
                return;
            }

            // Disable button during analysis
            btRun.setDisable(true);
            btRun.setText("Analyzing...");

            // Run analysis asynchronously
            CompletableFuture.supplyAsync(() -> {
                try {
                    var articleAnalyzer = new ArticleAnalyzer();
                    return articleAnalyzer.startAnalyzer(textForAnalyzer);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Analysis failed", e);
                    throw new RuntimeException("Analysis failed: " + e.getMessage(), e);
                }
            }).thenAcceptAsync(resultAnalyze -> {
                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    try {
                        var maxEntry = findMaxInComparison(resultAnalyze);
                        resultMsg.setText("Subject area: " +
                                formatCategoryName(maxEntry.getKey()));

                        barChart.getData().clear();
                        createXYChart(resultAnalyze);

                        mainBP.setVisible(false);
                        resultBP.setVisible(true);
                    } finally {
                        btRun.setDisable(false);
                        btRun.setText("Start Analysis");
                    }
                });
            }, Platform::runLater).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    logger.log(Level.SEVERE, "Error during analysis", throwable);
                    showAlert("Analysis Error",
                            "Failed to analyze text: " + throwable.getMessage());
                    btRun.setDisable(false);
                    btRun.setText("Start Analysis");
                });
                return null;
            });
        });
    }

    /**
     * Finds the entry with maximum value using Stream API.
     *
     * @param resultCompare map of analysis results
     * @return entry with highest similarity score
     */
    private Map.Entry<String, Double> findMaxInComparison(Map<String, Double> resultCompare) {
        return resultCompare.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElseThrow(() -> new IllegalStateException("No results found"));
    }

    /**
     * Creates bar chart visualization from analysis results.
     *
     * @param resultAnalyze map of category names to similarity scores
     */
    private void createXYChart(Map<String, Double> resultAnalyze) {
        resultAnalyze.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(result -> {
                    var series = new XYChart.Series<String, Number>();
                    series.setName(formatCategoryName(result.getKey()));
                    series.getData().add(new XYChart.Data<>("", result.getValue() * 100));
                    barChart.getData().add(series);
                });
    }

    /**
     * Formats category name for display (capitalizes first letter).
     *
     * @param categoryName raw category name
     * @return formatted category name
     */
    private String formatCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return categoryName;
        }
        return categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);
    }

    /**
     * Shows alert dialog with given title and message.
     *
     * @param title alert title
     * @param message alert message
     */
    private void showAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}