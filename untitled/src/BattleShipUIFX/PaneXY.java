package BattleShipUIFX;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.awt.*;

public class PaneXY extends Pane {
    private Point p;
    public boolean occupied=false;

    public PaneXY(int x, int y, Node... children) {
        super(children);
        p = new Point();
        p.setLocation(x,y);
    }

    public Point getP() {
        return p;
    }
}
