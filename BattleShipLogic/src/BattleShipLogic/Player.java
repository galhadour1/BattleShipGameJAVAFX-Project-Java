package BattleShipLogic;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Player {
    private static final char k_HitOnBoard ='X';
    private BoardGame m_ShipsBoard;
    private BoardGame m_TrackBoard;
    private GameTime m_PlayingTime;
    private int m_PlayerIndex;
    private int m_PlayerScore;
    private int m_AttacksCounter;
    private int m_NumberOfTraps;
    private int m_HitsCounter;
    private int m_MissCounter;
    private List<BoardItems> m_ShipsList;
    private Stack<Action> m_AttacksStack;
    protected int m_boardSize;

    public Player(int boardSize,int numberOfTraps) throws GameException {
        this.m_ShipsList = new LinkedList<BoardItems>();
        this.m_PlayingTime = new GameTime();
        m_AttacksStack = new Stack<Action>();
        this.m_ShipsBoard = new BoardGame(boardSize);
        this.m_TrackBoard = new BoardGame(boardSize);
        this.m_NumberOfTraps = numberOfTraps;
        this.m_PlayerIndex = m_PlayerIndex;
        this.m_PlayerScore = 0;
        this.m_AttacksCounter = 0;
        this.m_HitsCounter = 0;
        this.m_MissCounter = 0;
    }

    public BoardGame GetShipsBoard(){return m_ShipsBoard;}

    public BoardGame GetTrackBoard(){return m_TrackBoard;}

    public int GetMissCounter(){
        return m_MissCounter;
    }

    public int GetPlayerScore(){
        return m_PlayerScore;
    }

    public int GetAttacksCounter(){
        return m_AttacksCounter;
    }

    public GameTime GetAverageAttacksTime(){
        GameTime averageTime = new GameTime();

        averageTime.CalcAverageTime(m_PlayingTime, m_AttacksCounter);

        return averageTime;
    }


    public void SetShipsList(List<BoardItems> shipsListToSet){
        this.m_ShipsList = shipsListToSet;
    }

    public boolean SetTrapOnAttackerBoard(Trap trap) throws GameException {
        boolean isSetSucceed=false;

        isSetSucceed= m_ShipsBoard.AddItemToBoardGame(trap);

        return isSetSucceed;
    }

    public char GetSignFromShipBoard(Point pointOnBoard) throws GameException{
        return m_ShipsBoard.GetSignOnBoardByPoint(pointOnBoard);
    }

    public void IncreaseAttacksCounter(){ m_AttacksCounter++;}

    public void IncreaseHitsCounter(){
        m_HitsCounter++;
        m_PlayerScore++;}///////////////////////////change in excersie2

    public void IncreaseMissCounter(){
        m_MissCounter++;}

    public void AddTime(GameTime timeToAdd){
        this.m_PlayingTime.AddTime(timeToAdd);
    }

    public int GetHitsCount(){
        return m_HitsCounter;
    }

    public int GetMiss(){
        return m_MissCounter;
    }

    public int getTrapsNum(){
        return m_NumberOfTraps;
    }

    public boolean AddTraps(Point pointOnBoard) throws GameException{
        boolean isTrapAdded =true;

        if(m_NumberOfTraps > 0) {
            m_NumberOfTraps--;
            Trap trapToAdd = new Trap(pointOnBoard);
           // trapToAdd.SetTrapOnBoard(pointOnBoard);
            if (!(m_ShipsBoard.AddItemToBoardGame(trapToAdd))) {
                throw new GameException("Disable to add the mine to the board");
            }
        }
        else
            isTrapAdded=false;

        return isTrapAdded;
    }


    public void LoadFromFile(ObjectInputStream load) throws IOException, GameException, ClassNotFoundException{
        m_PlayerScore = load.readInt();
        m_AttacksCounter = load.readInt();
        m_HitsCounter = load.readInt();
        m_MissCounter = load.readInt();
        m_NumberOfTraps = load.readInt();
        m_boardSize = load.readInt();
        m_PlayingTime = (GameTime) load.readObject();
        m_ShipsBoard.LoadFromFile(load);
        m_TrackBoard.LoadFromFile(load);

        //load ships
        int shipsNum = load.readInt();
        for (int i = 0; i < shipsNum; i++){
            char type = load.readChar();
            if(type == 'R'){
                int stringLenght = load.readInt();
                String shipId = new String(new char[stringLenght]);
                shipId = (String) load.readObject();
                int length = load.readInt();
                int score = load.readInt();

                BattleShip ship = new BattleShip(shipId, length, score);
                ship.LoadFromFile(load);
                m_ShipsList.add(ship);
            }
            else{   //if type == 'L'
                //TODO:
            }
        }

        //load moves
        int movesCount = load.readInt();
        Stack<Action> tmp = new Stack<>();
        for (int i = 0; i < movesCount; i++){
            BattleShipLogic.Moves move = (BattleShipLogic.Moves)load.readObject();
            Action toAdd = new Action(move);
            toAdd.LoadFromFile(load);
            tmp.add(toAdd);
        }
        for(int i = 0; i < movesCount; i++) {
            m_AttacksStack.add(tmp.pop());
        }
    }

    public void DecreaseTrapsNum(){
        m_NumberOfTraps--;
    }

    public void AddShipsToGameBoard() throws GameException{
        boolean isAdditionSucceeded = true;

        for (BoardItems ship: this.m_ShipsList) {
            isAdditionSucceeded = m_ShipsBoard.AddItemToBoardGame(ship);
            if(!isAdditionSucceeded){
                m_ShipsBoard.ClearBoard();
                throw new GameException("Disable to add the ship to the board");
            }
        }
    }

    public void AddAttack(Action attackToAdd){
        m_AttacksStack.add(attackToAdd);
    }

    public void SaveAttack(Action attack){
        m_AttacksStack.add(attack);
    }

    public boolean HitBefore(Point pointOnBoard) {
        return m_TrackBoard.CheckIfHitBefore(pointOnBoard);
    }

    public boolean PlaceHitOnBoard(Point pointOnBoard){
        for (BoardItems ship: m_ShipsList){
            if (ship instanceof BattleShip){
                if (((BattleShip) ship).CheckHitOnBoard(pointOnBoard)){
                    ((BattleShip) ship).PlaceHitAndCheckSink(pointOnBoard);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean CheckLMissOnBoard(){
        for (BoardItems ship: m_ShipsList){
            if (ship instanceof BattleShip){
                if (!((BattleShip) ship).CheckShipSank()){
                    return false;
                }
            }
        }

        return true;
    }

    public char CheckHitOnBoard(Point pointOnBoard) throws GameException{
        char signOnBoard= m_ShipsBoard.GetSignOnBoardByPoint(pointOnBoard);

        if (signOnBoard!= k_HitOnBoard && signOnBoard!='~' && signOnBoard!=eItems.Trap.getChar()) {
            m_ShipsBoard.UpdateHitOnBoard(pointOnBoard);
        PlaceHitOnBoard(pointOnBoard);
    }

        return signOnBoard;
    }
}
