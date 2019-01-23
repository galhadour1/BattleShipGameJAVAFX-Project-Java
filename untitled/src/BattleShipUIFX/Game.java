package BattleShipUIFX;

import BattleShipLogic.BattleShipLogic;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;

public class Game extends Application {
    private static final String MAIN_MENU_FXML_PATH = "/BattleShipUIFX/Game.fxml";
    private BattleShipLogic BattleShipGame;
    public Game(BattleShipLogic game){
        BattleShipGame =game;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = Main.class.getResource(MAIN_MENU_FXML_PATH);
        loader.setLocation(mainFXML);
        primaryStage.setTitle("BattleShipGame");
        Pane root = loader.load();
        GameController gameController = loader.getController();
        gameController.SetGame(BattleShipGame);
        gameController.setPrimaryStage(primaryStage);
        primaryStage.setScene(new Scene(root, 930, 600));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(930);

        primaryStage.show();
    }


// public static void main(String[] args) {
//        launch(args);
//    }
}

