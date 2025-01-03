package analyzerWord2Vec.controllers;

import analyzerWord2Vec.ArticleAnalyzer;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class ControllerAppPane {
    Pane pane;
    GridPane gridPane;
    TextArea textArea;
    Button btRun;
    Text resultMsg;

    public ControllerAppPane(Pane pane, GridPane gridPane, TextArea textArea, Button btRun, Text resultMsg) throws Exception {
        this.pane = pane;
        this.gridPane = gridPane;
        this.textArea = textArea;
        this.btRun = btRun;
        this.resultMsg = resultMsg;
        initialize();
    }

    private void initialize() throws Exception {
        btStart();
    }


    public void btStart() throws Exception {
        btRun.setOnAction(event -> {
            ArticleAnalyzer articleAnalyzer = new ArticleAnalyzer();
            String textForAnalyzer = textArea.getText();
            textForAnalyzer = "трудового договора о дистанционной работе в отечественном трудовом праве";
            try {
                String resultAnalyze =
                        articleAnalyzer.startAnalyzer(textForAnalyzer);
                resultMsg.setText(resultAnalyze);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

}