package analyzerWord2Vec.controllers;

import analyzerWord2Vec.ArticleAnalyzer;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Map;


public class ControllerAppPane {
    Pane pane;
    BarChart<String, Number> barChart;
    BorderPane mainBP;
    TextArea textArea;
    Button btRun;
    Button btBack;
    Text resultMsg;
    BorderPane resultBP;

    public ControllerAppPane(Pane pane, BorderPane mainBP, BorderPane resultBP, TextArea textArea, BarChart<String, Number> barChart, Button btRun, Button btBack, Text resultMsg) throws Exception {
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

    private void initialize() {
        setActionBtStart();
        setActionBtBack();
    }

    private void setActionBtBack() {
        btBack.setOnAction(event -> {
            mainBP.setVisible(true);
            resultBP.setVisible(false);
        });
    }

    public void setActionBtStart() {
        btRun.setOnAction(event -> {

            ArticleAnalyzer articleAnalyzer = new ArticleAnalyzer();
            String textForAnalyzer = textArea.getText();
//            //textForAnalyzer =
//                    "в статье рассматриваются теоретические проблемы содержания" +
//                            " трудового договора о дистанционной работе в отечественном трудовом праве";
            try {
                Map<String, Double> resultAnalyze =
                        articleAnalyzer.startAnalyzer(textForAnalyzer);

                resultMsg.setText("Subject area of a scientific article: " +
                        maxInCompare(resultAnalyze).getKey());
                crXYChart(resultAnalyze);
                mainBP.setVisible(false);
                resultBP.setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Map.Entry<String, Double> maxInCompare(Map<String, Double> resultCompare) {
        Map.Entry<String, Double> maxValue = null;


        for (Map.Entry<String, Double> entrySet : resultCompare.entrySet()) {
            if (maxValue == null || maxValue.getValue() < entrySet.getValue()) {
                maxValue = entrySet;
            }
        }
        return maxValue;
    }

    private void crXYChart(Map<String, Double> resultAnalyze) {

        for (Map.Entry<String, Double> result : resultAnalyze.entrySet()) {

            // Создаем серию данных
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(result.getKey());
            // Добавляем графики
            series.getData().add(new XYChart.Data<>("", result.getValue() * 100));
            barChart.getData().add(series);
        }
    }
}
