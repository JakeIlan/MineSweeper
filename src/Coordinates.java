package Game;


public class Coordinates {
    private int x;
    private int y;
    boolean swOpenOrFlag;

    Coordinates(int x, int y, Boolean b) {
        this.x = x;
        this.y = y;
        this.swOpenOrFlag = b;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSwOpenOrFlag() {
        return swOpenOrFlag;
    }

    public void setSwOpenOrFlag(boolean swOpenOrFlag) {
        this.swOpenOrFlag = swOpenOrFlag;
    }
}
