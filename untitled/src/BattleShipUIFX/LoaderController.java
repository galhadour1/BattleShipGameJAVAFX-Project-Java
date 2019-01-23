package BattleShipUIFX;

import BattleShipLogic.BattleShipLogic;
import BattleShipLogic.eProblemsXML;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;

public class LoaderController {
    @FXML
    ProgressBar progressBar;
    @FXML
    Label LoadingLabel;
    private Stage primaryStage;

    public static void sleepForAWhile(long sleepTime) {

        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void bindTaskToUIComponents(Task<eProblemsXML> aTask) {
        // task progress bar
        progressBar.progressProperty().bind(aTask.progressProperty());
        aTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onTaskFinished();
        });

    }

    public void onTaskFinished() {
        this.progressBar.progressProperty().unbind();
        primaryStage.close();
    }

    public void RunTask(BattleShipLogic game, String path){
        Task<eProblemsXML> currentRunningTask = new LoadXmlTask(game,path);
        bindTaskToUIComponents(currentRunningTask);
        new Thread(currentRunningTask).start();
    }
}
