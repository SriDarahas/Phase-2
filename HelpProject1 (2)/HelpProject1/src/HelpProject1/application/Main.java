package HelpProject1.application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        HelpSystemUI ui = new HelpSystemUI(primaryStage);
        ui.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
