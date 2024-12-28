package analyzerWord2Vec;

import analyzerWord2Vec.controllers.ControllerAppPane;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.Getter;

// todo: сделать польз интерфейс и вывод аналитического круга или диаграммы
public class AppPane {
    @Getter
    Pane pane;
    GridPane gpAppPane;
    Text text;
    Button btRun;
    ControllerAppPane controllerAppPane;

    public Pane crAppPane() throws Exception {
        pane = new Pane();
        gpAppPane =  new GridPane();
        text = new Text();
        btRun = new Button();
        gpAppPane.add(btRun,3,3);
        gpAppPane.add(text,1,1);

        pane.getChildren().add(gpAppPane);
        controllerAppPane = new ControllerAppPane(pane, gpAppPane, text, btRun);
        controllerAppPane.btStart();

        return pane;
    }
}
