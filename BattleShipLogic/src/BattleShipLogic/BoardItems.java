package BattleShipLogic;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract  class BoardItems implements Serializable {
    protected Point[] m_SpreadingItemPoints;
    protected Point m_StartPositionPoint = new Point();
    protected char m_Sign;

    public abstract void BuildItem();

    public BoardItems(char sign) {
        m_StartPositionPoint.x = -1;
        m_StartPositionPoint.y = -1;
        this.m_Sign = sign;
    }


    public abstract void LoadFromFile(ObjectInputStream load) throws IOException, ClassNotFoundException;
}

