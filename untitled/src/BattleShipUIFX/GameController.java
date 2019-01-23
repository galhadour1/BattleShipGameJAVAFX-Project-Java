package BattleShipUIFX;

import BattleShipLogic.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;


public class GameController {
    private static final String MAIN_MENU_FXML_PATH = "/BattleShipUIFX/Game.fxml";
    @FXML Label TextPlayerLabel;
    @FXML Pane DeatailsPane;
    @FXML Label label_score;
    @FXML Label label_TheScore;
    @FXML Label label_hits;
    @FXML Label label_misses;
    @FXML Label label_TheMisses;
    @FXML Label label_TheHits;
    @FXML Label label_TheTime;
    @FXML Label label_Time;
    @FXML Label label_MinesLeft;
    @FXML Label label_TheMines;
    @FXML GridPane shipsGridPane;
    @FXML Label label_currentPlayer;
    @FXML Label ErrorMineMSG;
    @FXML GridPane hitsGridPane;
    @FXML Pane Mine;

    @FXML Pane Missile;
    @FXML Pane MenuPane;
    @FXML Label label_AddMine1;
    @FXML Label label_AddMine2;
    @FXML Label label_StatMoves;
    @FXML Label label_StatTimePassed;
    @FXML Label label_StatAScore;
    @FXML Label label_StatBScore;
    @FXML Label label_StatAMiss;
    @FXML Label label_StatBMiss;
    @FXML Label label_StatAAvgTime;
    @FXML Label label_StatBAvgTime;
    @FXML Pane StatisticPane;
    private BattleShipLogic BattleShipGame = new BattleShipLogic();
    private Player currentPlayer;
    private int BOARD_SIZE;
    private Stage primaryStage;
   // private Game Game;


    @FXML
    public void SetGame(BattleShipLogic game){
        BattleShipGame = game;
        BattleShipGame.StartTheGame();
        BattleShipGame.StartMoveTime();
        BOARD_SIZE = BattleShipGame.getBoardSize();
        setBoards();

        URL url = GameController.class.getResource("Resources/sounds/letsGo.mp3");
        Media sound = new Media(url.toString());
        MediaPlayer player = new MediaPlayer(sound);

        player.play();

        newTurn();
    }

    private void newTurn(){
        FillBoards();
        updateDetails();
    }

    private void FillBoards(){
        char[][] shipboard = BattleShipGame.getAttackerPlayerShipBoard();
        char[][] hitsBoard = BattleShipGame.getAttackerPlayerTrackBoard();
        int x,y;
        char ch;
        for (Node node:shipsGridPane.getChildren()) {
            if (node instanceof PaneXY) {
                y = GridPane.getColumnIndex(node);
                x = GridPane.getRowIndex(node);
                ch = shipboard[x][y];
                if (ch == eItems.Ship.getChar())
                    setShip((PaneXY) (node));
                else if (ch == eItems.Trap.getChar())
                    setMine((PaneXY) (node));
                else if (ch == eItems.Hit.getChar())
                    setDamaged((PaneXY) (node));
                else if(ch == eItems.HitTrap.getChar()){
                    setHitMine((PaneXY)(node));
                }
                else
                    setEmpty((PaneXY)(node));
            }
        }

        for (Node node:hitsGridPane.getChildren()) {
            if (node instanceof PaneXY) {
                y = GridPane.getColumnIndex(node);
                x = GridPane.getRowIndex(node);
                ch = hitsBoard[x][y];
                if (ch == eItems.Hit.getChar())
                    setHit((PaneXY) (node));
                else if (ch == eItems.Miss.getChar())
                    setMiss((PaneXY) (node));
                else if(ch == eItems.HitTrap.getChar()){
                    setHitMine((PaneXY)(node));
                }
                else
                    setEmpty((PaneXY)(node));
            }
        }
    }

    private void wrongMinePlace(){
        ErrorMineMSG.setVisible(true);
        new java.util.Timer().schedule(

                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        ErrorMineMSG.setVisible(false);
                    }
                },
                1000
        );

    }

    public void updateDetails(){
        currentPlayer = BattleShipGame.GetAttackerPlayerInstance();
        label_currentPlayer.setText(String.format("Player %c", BattleShipGame.getAttackerPlayer() + 'A'));
        label_TheHits.setText(String.format("%d",currentPlayer.GetHitsCount()));
        label_TheMisses.setText(String.format("%d",currentPlayer.GetMiss()));
        label_TheMines.setText(String.format("%d",currentPlayer.getTrapsNum()));
        label_TheScore.setText(String.format("%d",currentPlayer.GetPlayerScore()));
        label_TheTime.setText((currentPlayer.GetAverageAttacksTime()).toString());
        if(currentPlayer.getTrapsNum() == 1){
            label_AddMine1.setVisible(true);
            label_AddMine2.setVisible(true);
            Mine.setVisible(true);
            Mine.setDisable(false);
        }
        else if(currentPlayer.getTrapsNum() == 2){
            label_AddMine1.setVisible(true);
            label_AddMine2.setVisible(true);
            Mine.setVisible(true);
            Mine.setDisable(false);
        }
        else
        {
            label_AddMine1.setVisible(false);
            label_AddMine2.setVisible(false);
            Mine.setVisible(false);
            Mine.setDisable(true);
        }
    }

    private void setBoards()
    {
        for (int i=0;i<BOARD_SIZE-1;i++)
        {
            shipsGridPane.getColumnConstraints().add(new ColumnConstraints());
            shipsGridPane.getRowConstraints().add(new RowConstraints());
            hitsGridPane.getColumnConstraints().add(new ColumnConstraints());
            hitsGridPane.getRowConstraints().add(new RowConstraints());
        }
        for (int i=0;i<BOARD_SIZE;i++)
        {
            shipsGridPane.getColumnConstraints().get(i).setPercentWidth(100./BOARD_SIZE);
            shipsGridPane.getRowConstraints().get(i).setPercentHeight(100./BOARD_SIZE);
            hitsGridPane.getColumnConstraints().get(i).setPercentWidth(100./BOARD_SIZE);
            hitsGridPane.getRowConstraints().get(i).setPercentHeight(100./BOARD_SIZE);
        }
        for (int i=0;i<BOARD_SIZE;i++)
        {
            for (int j=0;j<BOARD_SIZE;j++) {

                shipsGridPane.add(createCell(i,j),i,j);
                hitsGridPane.add(createCell(i,j),i,j);
            }
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

    }


    @FXML
    private void putAMine()
    {
        WritableImage snapshot = Mine.snapshot(new SnapshotParameters(), null);
        Dragboard db = Mine.startDragAndDrop(TransferMode.ANY);
        db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
        ClipboardContent content = new ClipboardContent();
        content.putString( "Mine");
        db.setContent(content);
        db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
    }

    @FXML
    private void Attack()
    {
        WritableImage snapshot = Missile.snapshot(new SnapshotParameters(), null);
        Dragboard db = Missile.startDragAndDrop(TransferMode.ANY);
        db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
        ClipboardContent content = new ClipboardContent();
        content.putString( "Missile");
        db.setContent(content);
        db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
    }

    private Node createCell(int i, int j) {
        final PaneXY cell = new PaneXY(i,j);

        cell.setOnDragOver((event) -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        cell.setOnDragEntered((event) -> {
            if ((event.getDragboard().getString().equals("Mine") && cell.getParent().getId().equals("shipsGridPane") && !cell.occupied)
                    ||(event.getDragboard().getString().equals("Missile") && cell.getParent().getId().equals("hitsGridPane") && !cell.occupied)){
                cell.setStyle("-fx-background-color: #015267");
            }
            event.consume();
        });

        cell.setOnDragExited((event) -> {
            if (!cell.occupied) {
                cell.setStyle("-fx-background-color: none");
            }
            event.consume();
        });

        cell.setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.getString().equals("Mine") && cell.getParent().getId().equals("shipsGridPane") && !cell.occupied){
                if(BattleShipGame.PlaceMine(cell.getP()) == eAttackResults.PlacedTrap) {
                    setMine(cell);
                    updateDetails();
                    if(BattleShipGame.GetPlayerOnBreakInstance().getTrapsNum() == 0){
                        label_AddMine1.setVisible(false);
                        label_AddMine2.setVisible(false);
                        Mine.setVisible(false);
                        Mine.setDisable(true);
                    }
                    newTurn();
                }
                else{
                    wrongMinePlace();
                }

            }
            else if (db.getString().equals("Missile") && cell.getParent().getId().equals("hitsGridPane") && !cell.occupied){
                cell.occupied=true;
                onHit(cell);
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return cell;
    }

    private void setMine(PaneXY cell){
        String image = GameController.class.getResource("Resources/images/cartoon_sea_mine.png").toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");
        cell.occupied=true;
    }

    private void setShip(PaneXY cell){
        String image = GameController.class.getResource("Resources/images/ship.png").toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");

        cell.occupied = true;
    }

    private void setEmpty(PaneXY cell){
        cell.setStyle("-fx-background-color: none");
        cell.occupied=false;
    }

    private void setDamaged(PaneXY cell){
        String image = GameController.class.getResource("Resources/images/shipX.png").toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");
        cell.occupied = true;

    }

    private void setMiss(PaneXY cell){
        String image = GameController.class.getResource("Resources/images/water-splash.png").toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");
        cell.occupied=true;

        URL url = GameController.class.getResource("Resources/sounds/splash.wav");
        Media sound = new Media(url.toString());
        MediaPlayer player = new MediaPlayer(sound);
        player.play();

    }

    private void setHit(PaneXY cell){
        String image = GameController.class.getResource("Resources/images/damaged.PNG").toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");
        cell.occupied = true;

        URL url =  GameController.class.getResource("Resources/sounds/explosion.wav");
        Media sound = new Media(url.toString());
        MediaPlayer player = new MediaPlayer(sound);
        player.play();


    }

    private void setWin(){
            try {
                FXMLLoader loader = new FXMLLoader();
                URL loadFXML = Main.class.getResource("/BattleShipUIFX/Winner.fxml");
                loader.setLocation(loadFXML);
                Stage LoaderStage= new Stage();
                LoaderStage.initStyle(StageStyle.UNDECORATED);
                LoaderStage.setTitle("TheWinner");
                Pane root = loader.load();
                WinnerController winnerController = loader.getController();
                winnerController.setPrimaryStage(LoaderStage);
                LoaderStage.setScene(new Scene(root));
                if( BattleShipGame.FindWinners()==0) {
                    winnerController.setWinnerLabel(String.format("Player A!!!"));

                }
                else if( BattleShipGame.FindWinners()==1) {

                    winnerController.setWinnerLabel(String.format("Player B!!!"));
                }
                LoaderStage.show();
                //winnerController.sleep();
                try {
//                    winnerController.sleep();
                    //LoaderStage.wait(4000);
                    //winnerController.onTaskFinished();
                }
                catch (Exception ex){}
                //LoaderStage.close();
               //winnerController.onTaskFinished();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
//        if( BattleShipGame.FindWinners()==0)
//        {
//            TextPlayerLabel.setText(String.format("Player A Win!"));
//
//        }
//        else if( BattleShipGame.FindWinners()==1)
//        {
//
//            TextPlayerLabel.setText(String.format("Player B Win!"));
//        }
//        String image = GameController.class.getResource("../Resources/images/hit_mine.png").toExternalForm();
//        cell.setStyle("-fx-background-image: url('" + image + "'); " +
//                "-fx-background-position: center center; " +
//                "-fx-background-size: 100% 100%;");
//        cell.occupied=true;
    }

    private void setHitMine(PaneXY cell){
        String image = GameController.class.getResource("Resources/images/hit_mine.jpg").toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");
        cell.occupied=true;
    }

    public void checkHit(PaneXY cell){
        eAttackResults result= BattleShipGame.AttackOpponent(cell.getP());
        if (result==eAttackResults.AttackEndedWithHit)
            setHit(cell);
        else if (result== eAttackResults.AttackEndedWithMiss)
            setMiss(cell);
        else if (result== eAttackResults.AttackTrapWithObject || result== eAttackResults.AttackTrapWithOutObject)
            setHitMine(cell);
        if (result== eAttackResults.Win) {

            setWin();
        }
    }

    private void onHit(PaneXY cell){
        //missileAnim(cell);
        checkHit(cell);
        updateDetails();
        FillBoards();
    }

    private void missileAnim(PaneXY cell){

        String image = GameController.class.getResource("Resources/images/missileAnimation/1.gif").toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");

        animHelper(60, "Resources/images/missileAnimation/2.gif", cell);
        animHelper(120, "Resources/images/missileAnimation/3.gif", cell);
        animHelper(180, "Resources/images/missileAnimation/4.gif", cell);
        animHelper(240, "Resources/images/missileAnimation/5.gif", cell);
        animHelper(300, "Resources/images/missileAnimation/6.gif", cell);
        animHelper(360, "Resources/images/missileAnimation/7.gif", cell);
        animHelper(420, "Resources/images/missileAnimation/8.gif", cell);
        animHelper(480, "Resources/images/missileAnimation/9.gif", cell);
        animHelper(540, "Resources/images/missileAnimation/10.gif", cell);

    }

    private void animHelper(int delay, String resource, PaneXY cell){
        String image = GameController.class.getResource(resource).toExternalForm();
        cell.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-size: 100% 100%;");


    }

    public void OnClick_ShowStatistics(){
        MenuPane.setVisible(false);
        showStatistics();
        StatisticPane.setVisible(true);
    }



    private void showStatistics(){
        Object stats[] = BattleShipGame.GetStatisticsInfo();
        label_StatMoves.setText(String.format("%d",(int)stats[0]));
        label_StatTimePassed.setText(String.format("%s",(GameTime)stats[1]));
        label_StatAScore.setText(String.format("%d",(int)stats[2]));
        label_StatAMiss.setText(String.format("%d",(int)stats[3]));
        label_StatAAvgTime.setText(String.format("%s",(GameTime)stats[4]));
        label_StatBScore.setText(String.format("%d",(int)stats[5]));
        label_StatBMiss.setText(String.format("%d",(int)stats[6]));
        label_StatBAvgTime.setText(String.format("%s",(GameTime)stats[7]));
    }

    public void OnClick_backToMenu(){
        StatisticPane.setVisible(false);
        MenuPane.setVisible(true);
    }

    public void BackToMainMenu() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = Main.class.getResource("/BattleShipUIFX/Main.fxml");
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

    public void Exit()
    {
        primaryStage.close();
    }

//    public void start(Stage primaryStage) throws Exception{
//
//        FXMLLoader loader = new FXMLLoader();
//        URL mainFXML = Main.class.getResource(MAIN_MENU_FXML_PATH);
//        loader.setLocation(mainFXML);
//        primaryStage.setTitle("BattleShipGame");
//        Pane root = loader.load();
//        GameController gameController = loader.getController();
//        gameController.SetGame(BattleShipGame);
//        gameController.setPrimaryStage(primaryStage);
//        primaryStage.setScene(new Scene(root, 930, 600));
//        primaryStage.setMinHeight(600);
//        primaryStage.setMinWidth(930);
//
//        primaryStage.show();
//    }

}