package BattleShipLogic;

import BattleShipJAXB.BattleShipGame;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.*;
import java.util.List;

public class BattleShipLogic {
    public enum Moves{Trap, Hit}
    private int m_PlayerOnBreak =1;
    private int m_AttackerPlayer =0;
    private int m_BoardSize;
    private List<Player> m_PlayersList;
    private final static int k_NoWinners =-1;
    private final static int k_NumOfTraps =2;
    private final static int k_NumOfPlayers =2;
    private final static String k_JAXBXmlPackageName = "BattleShipJAXB";
    private eStatusGame m_e_StatusGame = eStatusGame.BeforeLoadXml;
    private Date m_StartTime;
    private Date m_StartMoveTime;
    private ArrayList<eProblemsXML> errorList;
    private Stack<Action> m_PlayersTurns;
    private GameTime m_TimePassed;
    private GameTime m_TimeToAdd;
    
    public BattleShipLogic(){
        m_PlayersList =new ArrayList<>(k_NumOfPlayers);
    }

    public int getAttackerPlayer() {
        return m_AttackerPlayer;
    }

    public Player GetAttackerPlayerInstance(){
        return m_PlayersList.get(getAttackerPlayer());
    }

    public Player GetPlayerOnBreakInstance(){
        return m_PlayersList.get(getPlayerOnBreak());
    }

    public List<Player> getPlayersList(){return m_PlayersList;}

    public int getPlayerOnBreak() { return m_PlayerOnBreak;}

    public int getAttackerPlayerScore(){return m_PlayersList.get(m_AttackerPlayer).GetPlayerScore();}

    public char[][] getAttackerPlayerShipBoard(){
        return m_PlayersList.get(m_AttackerPlayer).GetShipsBoard().GetBoardGame();
    }

    public char[][] getPlayerOnBreakShipBoard(){
        return m_PlayersList.get(m_PlayerOnBreak).GetShipsBoard().GetBoardGame();
    }

    public char[][] getAttackerPlayerTrackBoard(){
        return m_PlayersList.get(m_AttackerPlayer).GetTrackBoard().GetBoardGame();
    }

    public int getBoardSize(){return m_BoardSize;}

    public eStatusGame getStatusGame() {
        return m_e_StatusGame;
    }

    public boolean SetTrapOnBoard(Point point)
    {
        Trap trap=new Trap(point);
        boolean isSetSucceed=false;

        try {
            trap.SetTrapOnBoard(point);
            isSetSucceed= m_PlayersList.get(m_AttackerPlayer).SetTrapOnAttackerBoard(trap);
            ChangePlayers();
        }
        catch (GameException e) {}

        return isSetSucceed;
    }

    public String getJaxbErrorsInString() {
        StringBuilder xmlErrors = new StringBuilder();
        Iterator var2 = this.errorList.iterator();

        while(var2.hasNext()) {
            eProblemsXML error = (eProblemsXML)var2.next();
            xmlErrors.append(error);
            xmlErrors.append(System.getProperty("line.separator"));
        }

        return xmlErrors.toString();
    }

    public void StartMoveTime(){
        m_StartMoveTime =new Date();
    }

    public void StartTheGame(){
        m_e_StatusGame = eStatusGame.GameStarted;
        eMenuType.LoadXmlFile.setIsMenuTypeActive(false);
        eMenuType.StartTheGame.setIsMenuTypeActive(false);
        eMenuType.GameStatus.setIsMenuTypeActive(true);
        eMenuType.AttackOpponent.setIsMenuTypeActive(true);
        eMenuType.GameStatistics.setIsMenuTypeActive(true);
        eMenuType.EndCurrentGame.setIsMenuTypeActive(true);
        eMenuType.Trap.setIsMenuTypeActive(true);
        m_StartTime =new Date();
    }


    public eInputProblems CheckUserChoice(eMenuType action) {
        if (m_e_StatusGame.equals(eStatusGame.BeforeLoadXml)) {
            if (!action.getIsMenuTypeActive())
                return eInputProblems.OperateOtherCommandBeforeLoading;
            else {
                eMenuType.StartTheGame.setIsMenuTypeActive(true);
            }
        }
        else if (m_e_StatusGame.equals(eStatusGame.AfterLoadXml)) {
            if (!action.getIsMenuTypeActive())
                return eInputProblems.OperateGameActionBeforeStarted;
        }
        else if (m_e_StatusGame.equals(eStatusGame.GameStarted)) {
            if (!action.getIsMenuTypeActive())
                return eInputProblems.OperateSettingsCommandWhenGameIsOn;

        }

        return eInputProblems.IsChoiceValid;
    }

    //checks if the ships locations are valid and creating m_PlayersList
    private boolean CheckShipLocationsValidation(BattleShipGame xmlClasses){
        boolean isShipLocationsValid=true;
        int boardSize=xmlClasses.getBoardSize();

        try {
            m_PlayersList.add(new Player(boardSize, k_NumOfTraps));
            m_PlayersList.add(new Player(boardSize, k_NumOfTraps));
        }
        catch (GameException e){}
        List<List<BoardItems>> shipLists = new ArrayList<>(2);
        List<BoardItems> list1 = new ArrayList<>();
        List<BoardItems> list2 = new ArrayList<>();
        shipLists.add(list1);
        shipLists.add(list2);
        for (BattleShipGame.ShipTypes.ShipType shipType: xmlClasses.getShipTypes().getShipType())
        {
            try {
                shipLists.get(0).add((BattleShip) BattleShipFactory.PullOutObject(shipType));
                shipLists.get(1).add((BattleShip) BattleShipFactory.PullOutObject(shipType));
            }
            catch (GameException e) {
                isShipLocationsValid=false;
            }
        }

        for (int i = 0; i < k_NumOfPlayers; i++) {
            for (BattleShipGame.Boards.Board.Ship ship: xmlClasses.getBoards().getBoard().get(i).getShip()){
                for (BoardItems myObj: shipLists.get(i)) {
                    if (myObj instanceof BattleShip)
                    {
                        BattleShip myBattleShip =(BattleShip)(myObj);
                        if (ship.getShipTypeId().equals(myBattleShip.GetShipID()) && !myBattleShip.CheckPositionInitialized()){
                            myBattleShip.SetDirection(eDirection.StringToDirection(ship.getDirection()));
                            myBattleShip.SetStartPositionPoint(new Point(ship.getPosition().getY()-1, ship.getPosition().getX()-1));
                        }
                    }
                    else if (myObj instanceof LShapBattleShip)
                    {
                        LShapBattleShip myShip=(LShapBattleShip)(myObj);
                        if (ship.getShipTypeId().equals(myShip.GetShipID()) && !myShip.CheckPositionInitialized()){
                            myShip.SetDirection(eDirection.valueOf(ship.getDirection()));
                            myShip.SetStartPositionPoint(new Point(ship.getPosition().getY()-1, ship.getPosition().getX()-1));
                        }
                    }
                }
            }

            m_PlayersList.get(i).SetShipsList(shipLists.get(i));
            try {
                m_PlayersList.get(i).AddShipsToGameBoard();
            } catch (GameException e) {
                isShipLocationsValid=false;
            }
        }

        return isShipLocationsValid;
    }

    //checks if the number of ships in xml file is valid
    private boolean CheckShipInstancesNumberValidation(BattleShipGame xmlClasses) {
        boolean isInstancesNumberValid=true;
        Map<String, Integer> shipsMap = new HashMap<>();
        
        for (int i = 0; i < k_NumOfPlayers; i++) {
            for (BattleShipGame.ShipTypes.ShipType shipType : xmlClasses.getShipTypes().getShipType()) {
                shipsMap.put(shipType.getId(), shipType.getAmount());
            }
            
            for (BattleShipGame.Boards.Board.Ship ship: xmlClasses.getBoards().getBoard().get(i).getShip()){
                int newAmount = shipsMap.get(ship.getShipTypeId())-1;
                shipsMap.put((ship.getShipTypeId()),newAmount);
            }
            
            for (Map.Entry<String,Integer> entry:shipsMap.entrySet()) {
                if (entry.getValue() != 0) {
                    return false;
                }
            }
            
            shipsMap.clear();
        }
        
        return isInstancesNumberValid;
    }

    public void ClearAllBoards()
    {
        for (Player player:m_PlayersList) {
            player.GetShipsBoard().ClearBoard();
            player.GetTrackBoard().ClearBoard();
        }
    }

    public eAttackResults AttackOpponent(Point point)
    {
        Action attack = new Action(Moves.Hit);
        attack.SetPoint(point);
        boolean isChangePlayers = false;
        boolean isHitShip=false;
        eAttackResults attackResult=eAttackResults.AttackEndedWithMiss;

        if (!m_PlayersList.get(m_AttackerPlayer).GetShipsBoard().CheckPointInRange(point))
            return eAttackResults.OutOfRange;
        if (m_PlayersList.get(m_AttackerPlayer).HitBefore(point))
            return eAttackResults.HitBefore;
        try {
            char ch= m_PlayersList.get(m_PlayerOnBreak).CheckHitOnBoard(point);

            if (ch==eItems.Ship.getChar()){//attacking ship
                AttackShip(point);
                isHitShip=true;
                attackResult=eAttackResults.AttackEndedWithHit;
            }
            else if (ch==eItems.Trap.getChar()){ //if attacking trap and there is an object in the same place
                if (m_PlayersList.get(m_AttackerPlayer).GetShipsBoard().GetSignOnBoardByPoint(point)!='~'
                        && m_PlayersList.get(m_AttackerPlayer).GetShipsBoard().GetSignOnBoardByPoint(point)!= eItems.Miss.getChar()){
                   isChangePlayers= AttackTrapWithObject(point, isChangePlayers);
                   attackResult=eAttackResults.AttackTrapWithObject;
                }
                else { //attacking trap and there is no object at the same place
                    isChangePlayers=AttackTrapWithoutObject(point, isChangePlayers);
                    attackResult=eAttackResults.AttackTrapWithOutObject;
                }
            }
            else {//miss attack
                 isChangePlayers=AttackMiss(isChangePlayers,point);
                 attackResult=eAttackResults.AttackEndedWithMiss;
            }
        }
        catch (GameException e1){}
        endAttack(attack);
        if(isChangePlayers){
            ChangePlayers();
        }
        if (FindWinners() != k_NoWinners)
        {
            attackResult= eAttackResults.Win;

        }

        return attackResult;
    }

    public eAttackResults PlaceMine(Point point){

        eAttackResults res;

        if(m_PlayersList.get(m_AttackerPlayer).getTrapsNum() == 0){
            res = eAttackResults.ZeroTraps;
        }
        else {
            Trap toAdd = new Trap(point);
            toAdd.SetTrapOnBoard(point);
            if(m_PlayersList.get(m_AttackerPlayer).GetShipsBoard().AddItemToBoardGame(toAdd)){
                res = eAttackResults.PlacedTrap;
                m_PlayersList.get(m_AttackerPlayer).DecreaseTrapsNum();
                Action action = new Action(Moves.Trap);
                action.SetPoint(point);
                m_PlayersList.get(m_AttackerPlayer).AddAttack(action);
                endAttack(action);
                ChangePlayers();
            }
            else{
                res = eAttackResults.InvalidPoint;
            }
        }
        return res;
    }

    private void AttackShip(Point point)
    {
        m_PlayersList.get(m_AttackerPlayer).IncreaseHitsCounter();
        m_PlayersList.get(m_AttackerPlayer).GetTrackBoard().UpdateHitOnBoard(point);
    }

    private boolean AttackTrapWithObject(Point point, boolean isChangePlayers) throws GameException
    {
            m_PlayersList.get(m_AttackerPlayer).IncreaseHitsCounter();
            m_PlayersList.get(m_AttackerPlayer).GetTrackBoard().UpdateMissOnBoard(point);
            m_PlayersList.get(m_PlayerOnBreak).IncreaseHitsCounter();
            if(m_PlayersList.get(m_AttackerPlayer).GetSignFromShipBoard(point) == eItems.Ship.getChar())
            {
                m_PlayersList.get(m_PlayerOnBreak).GetTrackBoard().UpdateHitOnBoard(point);
                m_PlayersList.get(m_PlayerOnBreak).GetShipsBoard().UpdateHitOnBoard(point);
            }
            else //hit  a trap
            {
                m_PlayersList.get(m_PlayerOnBreak).GetTrackBoard().UpdateMissOnBoard(point);
                m_PlayersList.get(m_PlayerOnBreak).GetShipsBoard().UpdateMissOnBoard(point);
            }

            try {
            m_PlayersList.get(m_PlayerOnBreak).GetShipsBoard().UpdateBoard(point,eItems.HitTrap.getChar());
            }
            catch (Exception ex){}

            isChangePlayers = true;

            return isChangePlayers;
    }

    public boolean AttackMiss(boolean isChangePlayers, Point point)
    {
        m_PlayersList.get(m_AttackerPlayer).GetTrackBoard().UpdateMissOnBoard(point);
        m_PlayersList.get(m_PlayerOnBreak).GetShipsBoard().UpdateMissOnBoard(point);
        m_PlayersList.get(m_AttackerPlayer).IncreaseMissCounter();
        isChangePlayers = true;

        return  isChangePlayers;
    }

    public boolean AttackTrapWithoutObject(Point point, boolean isChangePlayers)
    {
        m_PlayersList.get(m_AttackerPlayer).IncreaseHitsCounter();
        m_PlayersList.get(m_AttackerPlayer).GetTrackBoard().UpdateMissOnBoard(point);
        m_PlayersList.get(m_PlayerOnBreak).GetShipsBoard().UpdateMissOnBoard(point);//////
        m_PlayersList.get(m_PlayerOnBreak).GetTrackBoard().UpdateMissOnBoard(point);
        m_PlayersList.get(m_PlayerOnBreak).IncreaseMissCounter();
        try {
            m_PlayersList.get(m_PlayerOnBreak).GetShipsBoard().UpdateBoard(point,eItems.HitTrap.getChar());
            m_PlayersList.get(m_AttackerPlayer).GetTrackBoard().UpdateBoard(point,eItems.HitTrap.getChar());
        }
        catch (Exception ex){}
        isChangePlayers=true;

        return isChangePlayers;
    }

    private void endAttack(Action attack){
        m_PlayersList.get(m_AttackerPlayer).IncreaseAttacksCounter();
        Date endAttack = new Date();
        GameTime attackGameTime = new GameTime(m_StartMoveTime, endAttack);
        m_PlayersList.get(m_AttackerPlayer).AddTime(attackGameTime);
        attack.SetTime(attackGameTime);
        m_PlayersList.get(m_AttackerPlayer).AddAttack(attack);
    }

    public Object[] GetStatisticsInfo(){
        Date currentTime = new Date();
        Object[] statisticsInfo = new Object[8];

        statisticsInfo[0] = m_PlayersList.get(1).GetAttacksCounter() + m_PlayersList.get(0).GetAttacksCounter();
        statisticsInfo[1] = new GameTime(m_StartTime, currentTime);
        statisticsInfo[2] = m_PlayersList.get(0).GetPlayerScore();
        statisticsInfo[3] = m_PlayersList.get(0).GetMissCounter();
        statisticsInfo[4] = m_PlayersList.get(0).GetAverageAttacksTime();
        statisticsInfo[5] = m_PlayersList.get(1).GetPlayerScore();
        statisticsInfo[6] = m_PlayersList.get(1).GetMissCounter();
        statisticsInfo[7] = m_PlayersList.get(1).GetAverageAttacksTime();

        return statisticsInfo;
    }

    public eLoadFile LoadGame(String path){
        eLoadFile res = eLoadFile.FileValid;
        try{
            FileInputStream file = new FileInputStream(path);
            ObjectInputStream load = new ObjectInputStream(file);
            LoadFromFile(load);
            load.close();
            file.close();
        }
        catch (Exception ex){
            SetGameState(eStatusGame.BeforeLoadXml);
            res = eLoadFile.FileInvalid;
        }
        finally {
            return res;
        }
    }

    public void SetGameState(eStatusGame state){
        m_e_StatusGame = state;
    }

    public void LoadFromFile(ObjectInputStream load) throws IOException, ClassNotFoundException, GameException{
        m_StartTime = new Date();
        m_TimeToAdd = (GameTime) load.readObject();
        m_AttackerPlayer = load.readInt();
        m_PlayerOnBreak = load.readInt();
        m_e_StatusGame = eStatusGame.GetEnumByID(load.readInt());
        m_BoardSize = load.readInt();
        m_PlayersList.clear();
        m_PlayersList.add(new Player(m_BoardSize,0));
        m_PlayersList.add(new Player(m_BoardSize,0));
        for (Player p: m_PlayersList) {
            p.LoadFromFile(load);
        }
        //Loads the turns stack
        int turns = load.readInt();
        Stack<Action> tmp = new Stack<>();
        for(int i = 0; i < turns; i++){
            Moves move = (Moves)load.readObject();
            Action toAdd = new Action(move);
            toAdd.LoadFromFile(load);
            tmp.add(toAdd);
        }
        for(int i = 0; i < turns; i++) {
            m_PlayersTurns.add(tmp.pop());
        }

        m_e_StatusGame=eStatusGame.AfterLoadXml;
        eMenuType.AgainstCom.setIsMenuTypeActive(true);
        eMenuType.StartTheGame.setIsMenuTypeActive(true);
    }


    public int FindWinners()
    {
        for(int i = 0; i< k_NumOfPlayers; i++){
            if (m_PlayersList.get(i).CheckLMissOnBoard())
                return i^=1;
        }

        return k_NoWinners;
    }

    public void ChangePlayers(){
        int tempPlayer;

        tempPlayer= m_PlayerOnBreak;
        m_PlayerOnBreak=m_AttackerPlayer;
        m_AttackerPlayer =tempPlayer;
    }

    public BattleShipGame FromXmlFileToObject(String xmlLocation) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(k_JAXBXmlPackageName);
        Unmarshaller u = jc.createUnmarshaller();

        return (BattleShipGame) u.unmarshal(new File(xmlLocation));
    }

    public eProblemsXML LoadItemsFromXML(String path){
        BattleShipGame xmlData = null ;

        try {
            xmlData = FromXmlFileToObject(path);
        }catch (JAXBException e) {
            return eProblemsXML.XmlDoesNotExist;
        }

        m_BoardSize = xmlData.getBoardSize();
        if(!BoardGame.CheckBoardSizeValidation(m_BoardSize))
            return eProblemsXML.XmlBoardSizeInvalid;
        if (!CheckShipInstancesNumberValidation(xmlData))
            return eProblemsXML.XmlShipItemsNumberInvalid;
        if (!CheckShipLocationsValidation(xmlData))
            return eProblemsXML.XmlShipPositionInvalid;

        return eProblemsXML.XmlIsValid;
    }
}
