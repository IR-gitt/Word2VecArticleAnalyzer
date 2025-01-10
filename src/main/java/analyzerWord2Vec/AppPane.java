package analyzerWord2Vec;

import analyzerWord2Vec.controllers.ControllerAppPane;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.Getter;

public class AppPane {
    @Getter
    BorderPane mainBP;
    BorderPane resultBP;
    BarChart<String, Number> barChart;
    TextArea textArea;
    Text resultMsg;
    Button btRun;
    Button btBack;
    ControllerAppPane controllerAppPane;


    public Pane crAppPane() throws Exception {

        Pane pane = new Pane();
        pane.setPrefSize(600, 500);
        crMainBP();
        crResultBP();

        pane.getChildren().addAll(mainBP, resultBP);

        controllerAppPane = new ControllerAppPane
                (pane, mainBP, resultBP, textArea, barChart, btRun, btBack, resultMsg);

        return pane;
    }

    private void crResultBP() {

        resultBP = new BorderPane();
        resultBP.setPrefSize(600, 500);

        GridPane gpBottomResultBP = new GridPane();
        GridPane gpCenterResultBP = new GridPane();
        gpBottomResultBP.setGridLinesVisible(true);
        gpCenterResultBP.setGridLinesVisible(true);
        crBarChartResult();

        btBack = new Button("Back");
        btBack.setPrefSize(70, 30);

        resultMsg = new Text();

        gpCenterResultBP.add(barChart, 0, 0);
        gpBottomResultBP.add(resultMsg, 0, 1);
        gpBottomResultBP.add(btBack, 0, 2);
        GridPane.setHalignment(btBack, HPos.CENTER);
        GridPane.setHalignment(barChart, HPos.CENTER);
        gpBottomResultBP.setAlignment(Pos.CENTER);
        gpCenterResultBP.setAlignment(Pos.CENTER);

        resultBP.setCenter(gpCenterResultBP);
        resultBP.setBottom(gpBottomResultBP);

        resultBP.setVisible(false);
    }

    // создание BarCart
    private void crBarChartResult() {
        // Создаем оси xy
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelRotation(45);
        xAxis.setLabel("Вид");
        yAxis.setLabel("Значение");

        // Создаем столбчатый график и добавляем в него оси
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(" ");
    }

    private void crMainBP() {
        mainBP = new BorderPane();
        mainBP.setPrefSize(600, 500);

        GridPane gpBottomMainMenu = new GridPane();
        GridPane gpCenterMeinMenu = new GridPane();

        btRun = new Button("Start");
        Platform.runLater(
                () -> btRun.requestFocus());
        btRun.setPrefSize(70, 30);

        textArea = new TextArea();
        textArea.setPromptText("Press or inset text");
        textArea.setPrefSize(500, 300);
        textArea.setWrapText(true);

        gpCenterMeinMenu.add(textArea, 0, 1);
        gpBottomMainMenu.add(btRun, 0, 1);

        gpBottomMainMenu.setAlignment(Pos.CENTER);
        gpCenterMeinMenu.setAlignment(Pos.CENTER);

        mainBP.setCenter(gpCenterMeinMenu);
        mainBP.setBottom(gpBottomMainMenu);
    }
}
