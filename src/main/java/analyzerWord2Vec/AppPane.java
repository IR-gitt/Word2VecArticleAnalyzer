package analyzerWord2Vec;

import analyzerWord2Vec.controllers.ControllerAppPane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import lombok.Getter;

// todo: сделать польз интерфейс и вывод аналитического круга или диаграммы
public class AppPane {
    @Getter
    BorderPane borderPane;
    GridPane gpAppPane;
    TextField textField;
    Text resultMsg;
    Button btRun;
    ControllerAppPane controllerAppPane;

    public Pane crAppPane() throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(600,600);
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(300,300);
        GridPane gpBottom = new GridPane();
        GridPane gpCenter = new GridPane();

        resultMsg = new Text();

        btRun = new Button();
        btRun.setPrefSize(70,30);

        textField = new TextField();
        textField.setPrefSize(300,100);

        gpCenter.add(textField,0,1);
        gpCenter.add(resultMsg, 0, 2);

        gpBottom.add(btRun,0,1);

        gpBottom.setAlignment(Pos.CENTER);
        gpCenter.setAlignment(Pos.CENTER);

//        gpCenter.setVgap(10);
//        gpBottom.setVgap(30);

        borderPane.setCenter(gpCenter);
        borderPane.setBottom(gpBottom);
        pane.setPrefSize(500,500);

        //pane.getChildren().add(borderPane);
        pane.getChildren().addAll(borderPane);
        controllerAppPane = new ControllerAppPane(borderPane, gpAppPane, textField, btRun);

        return pane;
    }
}
