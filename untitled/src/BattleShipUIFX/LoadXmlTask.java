package BattleShipUIFX;

import BattleShipLogic.BattleShipLogic;
import BattleShipLogic.eProblemsXML;
import javafx.concurrent.Task;
import BattleShipJAXB.BattleShipGame;

public class LoadXmlTask extends Task<eProblemsXML> {

    BattleShipLogic m_game;
    String m_path;
    LoadXmlTask(BattleShipLogic game,String path)
    {
        m_game= game;
        m_path=path;
    }
    @Override
    protected eProblemsXML call() throws Exception {
        eProblemsXML res = m_game.LoadItemsFromXML(m_path);
        sleepForAWhile(1900);
        return res;
    }

    public static void sleepForAWhile(long sleepTime) {
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }
}
