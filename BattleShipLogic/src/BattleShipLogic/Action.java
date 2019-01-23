package BattleShipLogic;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Action {
    private GameTime m_GameTime;
    private BattleShipLogic.Moves m_GameMove;
    private Point m_Point;
    private char m_hit;

    public Action(BattleShipLogic.Moves m_GameMove){

        this.m_GameMove = m_GameMove;
    }

    public void SetTime(GameTime gameTime){

        this.m_GameTime = gameTime;
    }

    public void SetPoint(Point point){

        this.m_Point = point;
    }

    public void LoadFromFile(ObjectInputStream load) throws IOException, GameException, ClassNotFoundException {
        m_Point = (Point) load.readObject();
        m_hit = load.readChar();
        m_GameTime = (GameTime) load.readObject();
    }

}
