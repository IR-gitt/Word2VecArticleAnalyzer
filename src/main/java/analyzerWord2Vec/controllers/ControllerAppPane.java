package analyzerWord2Vec.controllers;

import analyzerWord2Vec.ArticleAnalyzer;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class ControllerAppPane {
    Pane pane;
    GridPane gridPane;
    TextField textField;
    Button btRun;

    public ControllerAppPane(Pane pane, GridPane gridPane, TextField textField, Button btRun) throws Exception {
        this.pane = pane;
        this.gridPane = gridPane;
        this.textField = textField;
        this.btRun = btRun;
        initialize();
    }

    private void initialize() throws Exception {
        btStart();
    }


    public void btStart() throws Exception{
        btRun.setOnAction(event -> {
            ArticleAnalyzer articleAnalyzer = new ArticleAnalyzer();

            try {
                articleAnalyzer.startAnalyzer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

}