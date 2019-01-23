package BattleShipUIFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
//java -jar BattleShipGame.jar

    private static final String MAIN_MENU_FXML_PATH = "/BattleShipUIFX/Main.fxml";
    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = Main.class.getResource(MAIN_MENU_FXML_PATH);
        loader.setLocation(mainFXML);
        primaryStage.setTitle("BattleShipGame");
        Pane root = loader.load();
        MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);
        primaryStage.setScene(new Scene(root, 560, 400));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(560);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
