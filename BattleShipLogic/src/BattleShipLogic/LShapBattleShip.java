package BattleShipLogic;

import java.awt.*;

public class LShapBattleShip extends BattleShip {

    public LShapBattleShip(String shipID,int length, int score)throws GameException
    {
        super(shipID, length, score);
        m_SpreadingItemPoints = new Point[length*2-1];
        for (int i=0;i<length*2-1;i++)
            m_SpreadingItemPoints[i]=new Point();
        SetPointsLeft(length*2-1);
    }
    @Override
    public void SetStartPositionPoint(Point startPoint){
        this.m_StartPositionPoint = startPoint;
        this.BuildItem();
    }

    @Override
    public void BuildItem(){
        int x = m_StartPositionPoint.x;
        int y = m_StartPositionPoint.y;
        m_SpreadingItemPoints[0].x=x;
        m_SpreadingItemPoints[0].y=y;
        for (int i=1;i<m_Length;i++)
        {
            if((m_Direction == eDirection.RightUp)||(m_Direction == eDirection.DownRight)){
                y--;
            }
            else if ((m_Direction == eDirection.UpRight)||(m_Direction == eDirection.RightDowm)) {
                y++;
            }
            m_SpreadingItemPoints[i].x = x;
            m_SpreadingItemPoints[i].y = y;
        }

        y=m_StartPositionPoint.y;
        for (int i=m_Length;i<2*m_Length-1;i++)
        {
            if((m_Direction == eDirection.RightUp)||(m_Direction == eDirection.RightDowm)){
                x--;
            }
            else if ((m_Direction == eDirection.UpRight)||(m_Direction == eDirection.DownRight)){
                x++;
            }
            m_SpreadingItemPoints[i].x = x;
            m_SpreadingItemPoints[i].y = y;
        }
    }
}
