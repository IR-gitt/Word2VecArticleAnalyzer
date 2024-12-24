package analyzerWord2Vec;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

// todo: сделать польз интерфейс и вывод аналитического круга или диаграммы
public class AppPane {
    public Pane crAppPane(){
        Pane pane = new Pane();
        GridPane gpAppPane =  new GridPane();
        Text text = new Text();
        Button btChosePath = new Button();
        Button btRun = new Button();
        gpAppPane.add(btRun,3,3);
        gpAppPane.add(text,1,1);
        gpAppPane.add(btChosePath,1,3);
        pane.getChildren().add(gpAppPane);
        return pane;
    }
}
