package BattleShipLogic;

public enum eItems {
    Ship('$'),
    Trap('@'),
    Hit('X'),
    HitTrap('M'),
    Miss('*'),;

    private final char m_Item;

    private eItems(char sign){
        m_Item =sign;
    }
    public char getChar(){return m_Item;}
}
