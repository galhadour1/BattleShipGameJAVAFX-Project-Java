package BattleShipUIFX;

import BattleShipLogic.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainController {
    private static final String MAIN_MENU_FXML_PATH = "/BattleShipUIFX/Game.fxml";
    @FXML Button button_LoadXML;
    @FXML Button button_StartGame;
    @FXML Button button_ExitProgram;
    @FXML Button button_LoadFromFile;
    @FXML Button button_AgainstCom;
    @FXML Label label_LoadingErrorsXML;
    @FXML Label label_LoadingErrorsFile;

    private BattleShipLogic BattleShipGame = new BattleShipLogic();
    private Stage primaryStage;
    private GameController m_Game=new GameController();


//    @FXML
//   private void UpdateMenu()
//   {
//        button_LoadXML.visibleProperty().setValue(ActionType.LOAD_XML.getActionIsActive());
//        button_StartGame.visibleProperty().setValue(ActionType.START_GAME.getActionIsActive());
//        button_ExitProgram.visibleProperty().setValue(ActionType.END_PROGRAM.getActionIsActive());
//        button_LoadFromFile.visibleProperty().setValue(ActionType.LOAD_GAME_FROM_FILE.getActionIsActive());
//        button_AgainstCom.visibleProperty().setValue(ActionType.AGAINST_COM.getActionIsActive());
//    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void LoadGameFromXML()
    {
        eProblemsXML res;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        else{
            res = LoadFromTask(selectedFile.getPath());
        }

        if(res == eProblemsXML.XmlIsValid){
            button_StartGame.setDisable(false);
            label_LoadingErrorsXML.setVisible(false);
        }
        else{
            label_LoadingErrorsXML.setVisible(true);
            label_LoadingErrorsXML.setText(XMLErrorMsg(res));
        }

    }



    private eProblemsXML LoadFromTask(String path){
        try {

            FXMLLoader loader = new FXMLLoader();
            URL loadFXML = Main.class.getResource("/BattleShipUIFX/Loader.fxml");
            loader.setLocation(loadFXML);
            Stage LoaderStage= new Stage();
            LoaderStage.initStyle(StageStyle.UNDECORATED);
            LoaderStage.setTitle("XML-Loader");
            Pane root = loader.load();
            LoaderController loaderController = loader.getController();
            loaderController.setPrimaryStage(LoaderStage);
            LoaderStage.setScene(new Scene(root));
            loaderController.RunTask(BattleShipGame,path);
            LoaderStage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return eProblemsXML.XmlIsValid;
    }

    @FXML
    private void LoadGame()
    {
        eLoadFile res;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select m_Game file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        else{
            res = BattleShipGame.LoadGame(selectedFile.getPath());
        }

        if(res == eLoadFile.FileValid){
            button_StartGame.setDisable(false);
            label_LoadingErrorsFile.setVisible(false);
        }
        else{
            label_LoadingErrorsFile.setVisible(true);
            label_LoadingErrorsFile.setText("Invalid File");
        }
    }


    @FXML
    public void StartGame() throws Exception{
       // m_Game.SetGame(BattleShipGame);
        Game game=new Game(BattleShipGame);
        Stage newGame = new Stage();
        primaryStage.hide();
       game.start(newGame);
    }
    @FXML
    private void ExitProgram()
    {
        primaryStage.close();
    }

    private String XMLErrorMsg(eProblemsXML problem){
        String res = "";

        switch (problem){
            case XmlDoesNotExist:
                res = "Invalid File";
                break;
            case XmlBoardSizeInvalid:
                res = "Invalid board size";
                break;
            case XmlInvalidShipTypes:
                res = "Invalid ship type";
                break;
            case XmlInvalidMineAmount:
                res = "Invalid Mine Amount";
                break;
            case XmlLengthOfShipsIllegal:
                res = "Ships length is illegal";
                break;
            case XmlShipPositionInvalid:
                res = "Ships location is illegal";
                break;
            case XmlNumberOfShhipsIsNegative:
                res = "Ships amount is illegal";
                break;
            case XmlShipItemsNumberInvalid:
                res = "Invalid ships instances number";
                break;
        }

        return res;
    }
}
