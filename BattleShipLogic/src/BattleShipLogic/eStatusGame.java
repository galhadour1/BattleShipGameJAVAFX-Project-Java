package BattleShipLogic;

public enum eStatusGame {
        WinnerFound,
        GameStarted,
        BeforeLoadXml,
        AfterLoadXml;

        public static eStatusGame GetEnumByID(int i){
                eStatusGame[] val = eStatusGame.values();
                return val[i];
        }
}
