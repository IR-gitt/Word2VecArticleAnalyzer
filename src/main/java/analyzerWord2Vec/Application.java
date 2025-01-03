package analyzerWord2Vec;

import javafx.scene.Scene;
import javafx.stage.Stage;



public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {

        Scene scene = new Scene(new AppPane().crAppPane(), 600, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}