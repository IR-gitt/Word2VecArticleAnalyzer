package analyzerWord2Vec.controllers;

import analyzerWord2Vec.ArticleAnalyzer;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


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

    private void initialize(){
        setActionBtStart();
        setActionBtBack();
    }

    private void setActionBtBack() {
        btBack.setOnAction(event -> {
            mainBP.setVisible(true);
            resultBP.setVisible(false);
        } );
    }

    public void setActionBtStart() {
        // todo: может быть не одно решение, сделать аналитический круг или диаграмму
        btRun.setOnAction(event -> {

            ArticleAnalyzer articleAnalyzer = new ArticleAnalyzer();
            String textForAnalyzer = textArea.getText();
            textForAnalyzer =
                    "в статье рассматриваются теоретические проблемы содержания" +
                            " трудового договора о дистанционной работе в отечественном трудовом праве";
            try {
                String resultAnalyze =
                        articleAnalyzer.startAnalyzer(textForAnalyzer);
                resultMsg.setText(resultAnalyze);
                crXYChart();

                mainBP.setVisible(false);
                resultBP.setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void crXYChart() {
        // Создаем серию данных
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("S1");
        series1.getData().add(new XYChart.Data<>("s1", 35));

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("S2");
        series2.getData().add(new XYChart.Data<>("s2", 35));


        barChart.getData().addAll(series1, series2);
    }

}