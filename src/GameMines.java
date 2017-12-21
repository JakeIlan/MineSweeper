package Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

class GameMines {

    private final int BLOCK_SIZE = 50; // size of one block
    int FIELD_SIZE = 1; // in blocks
    int NUMBER_OF_MINES; //MINES
    private final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0};
    Cell[][] field;
    private Random random = new Random();
    int countOpenedCells;
    int countFlags;
    private MineGraphics.Canvas canvas;
    boolean youWon, bangMine; // flags for win and bang/fail
    int bangX, bangY; // for fix the coordinates of the explosion


    GameMines(int fieldSize, int numberOfMines) {
        bangMine = false;
        youWon = false;
        FIELD_SIZE = fieldSize;
        NUMBER_OF_MINES = numberOfMines;
        this.field = new Cell[fieldSize][fieldSize];
    }


    void openCells(int x, int y) { // recursive procedure of opening the cells
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return; // wrong coordinates
        if (!field[y][x].isNotOpen()) return; // cell is already open
        field[y][x].open();
        if (field[y][x].getCountBomb() > 0 || bangMine) return; // the cell is not empty
        for (int dx = -1; dx < 2; dx++)
            for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
    }

    void reverseFlag(Coordinates c) {
        field[c.getY()][c.getX()].inverseFlag();
    }

    void openCells(Coordinates c) {
        if (c.getX() < 0 || c.getY() < 0) return;
        openCells(c.getX(), c.getY());
    }

    void openCells(ArrayList<Coordinates> c) {
        for (Coordinates crd:c) {
            openCells(crd);
            c.remove(crd);
        }
    }


    void initField() { // initialization of the playing field
        int x, y, countMines = 0;
        // create cells for the field
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                field[y][x] = new Cell();
        // to mine field
        while (countMines < NUMBER_OF_MINES) {
            do {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (field[y][x].isMined());
            field[y][x].mine();
            countMines++;
        }

        // to count dangerous neighbors
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!field[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) {
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (field[nY][nX].isMined()) ? 1 : 0;
                        }
                    field[y][x].setCountBomb(count);
                }
    }

    void initFieldTest(int[] mines) { // initialization of the playing field
        int x, y, countMines = 0;
        // create cells for the field
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                field[y][x] = new Cell();
        // to mine field
        int i;
        while (countMines < NUMBER_OF_MINES) {
            for (i = 0; i < NUMBER_OF_MINES; i++) {
                x = mines[i] / FIELD_SIZE;
                y = mines[i] % FIELD_SIZE;
                field[x][y].mine();
                countMines++;
            }
        }
        // to count dangerous neighbors
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!field[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) {
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (field[nY][nX].isMined()) ? 1 : 0;
                        }
                    field[y][x].setCountBomb(count);
                }
    }


    class Cell { // playing field cell
        Double possibility;
        int countBombNear;
        boolean isOpen, isMine, isFlag;

        public Double getPossibility() {
            return possibility;
        }

        public void setPossibility(Double possibility) {
            this.possibility = possibility;
        }

        void open() {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }



//        //считает количество неоткрытых соседних клеток без флагов
//        int countNotOpenedNeighbours(int i, int j) {
//            int count = 0;
//            for (int dx = -1; dx < 2; dx++) {
//                for (int dy = -1; dy < 2; dy++) {
//                    int nX = i + dx;
//                    int nY = j + dy;
//                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
//                        nX = i;
//                        nY = j;
//                    }
//                    count += (field[nY][nX].isNotOpen() && field[nY][nX].isNotFlagged()) ? 1 : 0;
//                }
//            }
//            return count;
//        }

//        //считает количество соседних ячеек с флагами
//        int countFlaggedNeighbours(int i, int j) {
//            int count = 0;
//            for (int dx = -1; dx < 2; dx++) {
//                for (int dy = -1; dy < 2; dy++) {
//                    int nX = i + dx;
//                    int nY = j + dy;
//                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
//                        nX = i;
//                        nY = j;
//                    }
//                    count += (!field[nY][nX].isNotFlagged()) ? 1 : 0;
//                }
//            }
//            return count;
//        }

        //устанавливает флаги на все неоткрытые соседние ячейки
//        void flagNotOpenedNeighbours(int i, int j) {
//            for (int dx = -1; dx < 2; dx++) {
//                for (int dy = -1; dy < 2; dy++) {
//                    int nX = i + dx;
//                    int nY = j + dy;
//                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
//                        nX = i;
//                        nY = j;
//                    }
//                    if (field[nY][nX].isNotFlagged() && field[nY][nX].isNotOpen()) field[nY][nX].inverseFlag();
//                }
//            }
//        }

//        //открывает все соседние ячейки без флагов
//        void openUnflaggedNeighbours(int i, int j) {
//            for (int dx = -1; dx < 2; dx++) {
//                for (int dy = -1; dy < 2; dy++) {
//                    int nX = i + dx;
//                    int nY = j + dy;
//                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
//                        nX = i;
//                        nY = j;
//                    }
//                    if (field[nY][nX].isNotFlagged() && field[nY][nX].isNotOpen()) openCells(nX, nY);
//                }
//            }
//        }

//        //считает количество заминированых клеток рядом
//        private int countMinedNeighbours(int i, int j) {
//            int count = 0;
//            for (int dx = -1; dx < 2; dx++) {
//                for (int dy = -1; dy < 2; dy++) {
//                    int nX = i + dx;
//                    int nY = j + dy;
//                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
//                        nX = i;
//                        nY = j;
//                    }
//                    count += (field[nY][nX].isMined()) ? 1 : 0;
//                }
//            }
//            return count;
//        }

        void mine() {
            isMine = true;
        }

        void setCountBomb(int count) {
            countBombNear = count;
        }

        int getCountBomb() {
            return countBombNear;
        }

        boolean isNotOpen() {
            return !isOpen;
        }

        boolean isMined() {
            return isMine;
        }

        void inverseFlag() {
            if (this.isNotOpen())
                isFlag = !isFlag;
        }

        void inverseFlag(Coordinates c) {
            if (field[c.getY()][c.getX()].isNotOpen() && c.getX() >= 0 && c.getY() > 0)
                field[c.getY()][c.getX()].isFlag = !isFlag;
            else return;
        }

        boolean isNotFlagged() {
            return !isFlag;
        }

        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * BLOCK_SIZE + 7 + 8, y * BLOCK_SIZE + 10 + 9, 22, 12);
            g.fillRect(x * BLOCK_SIZE + 11 + 9, y * BLOCK_SIZE + 6 + 8, 12, 22);
            g.fillRect(x * BLOCK_SIZE + 9 + 8, y * BLOCK_SIZE + 8 + 8, 18, 18);
            g.setColor(Color.white);
            g.fillRect(x * BLOCK_SIZE + 11 + 9, y * BLOCK_SIZE + 10 + 9, 5, 5);
        }

        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE - 5));
            g.drawString(str, x * BLOCK_SIZE + 13, y * BLOCK_SIZE + 40);
        }

        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen) {
                if ((bangMine || youWon) && isMine) paintBomb(g, x, y, Color.black);
                else {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) {
                        g.setColor(new Color(0xFF3E5E));
                        g.fill3DRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                        //  paintString(g, SIGN_OF_FLAG, x, y, Color.red);
                    }
                }
            } else if (isMine) paintBomb(g, x, y, bangMine ? Color.red : Color.black);
            else if (countBombNear > 0)
                paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
        }
    }

//    class TimerLabel extends JLabel { // label with stopwatch
//        Timer timer = new Timer();
//
//        TimerLabel() {
//            timer.scheduleAtFixedRate(timerTask, 0, 1000);
//        } // TimerTask task, long delay, long period
//
//        TimerTask timerTask = new TimerTask() {
//            volatile int time;
//            Runnable refresher = new Runnable() {
//                public void run() {
//                    TimerLabel.this.setText(String.format("%02d:%02d", time / 60, time % 60));
//                }
//            };
//
//            public void run() {
//                time++;
//                SwingUtilities.invokeLater(refresher);
//            }
//        };
//
//        void stopTimer() {
//            timer.cancel();
//            if (youWon) TimerLabel.this.setText("You Win!");
//            if (bangMine) TimerLabel.this.setText("You Lose!");
//        }
//
//    }


    void setCanvas(MineGraphics.Canvas canvas) {
        this.canvas = canvas;
    }

    MineGraphics.Canvas getCanvas() {
        return canvas;
    }
}
