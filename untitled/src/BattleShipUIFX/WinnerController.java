package BattleShipUIFX;

import BattleShipLogic.BattleShipLogic;
import BattleShipLogic.eProblemsXML;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class WinnerController {
    @FXML
    Label WinnerLabel;
    @FXML
    Label TheWinnerIsLabel;
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

    public void setWinnerLabel(String winner)
    {
        WinnerLabel.setText(winner);
    }

    public void sleep()
    {
        sleepForAWhile(3000);
    }

    public void show()
    {
        primaryStage.show();

    }

    public void onTaskFinished() {
     primaryStage.close();

    }
}
