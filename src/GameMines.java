import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

class GameMines extends JFrame {

    final String TITLE_OF_PROGRAM = "Mines";
    final String SIGN_OF_FLAG = "F";
    final int BLOCK_SIZE = 30; // size of one block
    final int FIELD_SIZE = 12; // in blocks
    final int FIELD_DX = 6; // determined experimentally
    final int FIELD_DY = 28 + 25;
    final int START_LOCATION = 200;
    final int MOUSE_BUTTON_LEFT = 1; // for mouse listener
    final int MOUSE_BUTTON_RIGHT = 3;
    final int NUMBER_OF_MINES = 15; //MINES
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0};
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
    Random random = new Random();
    int countOpenedCells;
    int countFlags;
    boolean youWon, bangMine; // flags for win and bang/fail
    int bangX, bangY; // for fix the coordinates of the explosion
    boolean testMines = false;

    public static void main(String[] args) {
        new GameMines();
    }

    GameMines() {
        setTitle(TITLE_OF_PROGRAM);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
        setResizable(false);
        final TimerLabel timeLabel = new TimerLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        final Canvas canvas = new Canvas();
        Bot bot = new Bot(canvas, timeLabel);
        canvas.setBackground(Color.white);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX() / BLOCK_SIZE;
                int y = e.getY() / BLOCK_SIZE;
                if (!bangMine && !youWon) {
                    if (e.getButton() == MOUSE_BUTTON_LEFT) {// left button mouse
                        if (field[y][x].isNotOpen() && !field[y][x].isFlag) {
                            openCells(x, y);
                            youWon = (countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES) ||
                                    (countFlags == NUMBER_OF_MINES);// winning check
                            if (bangMine) {
                                bangX = x;
                                bangY = y;
                            }

                        }


                    }
                    if (e.getButton() == MOUSE_BUTTON_RIGHT) {// right button mouse
                        if (field[y][x].isNotFlagged()) {
                            field[y][x].inverseFlag();
                            countFlags++;
                        } else {
                            field[y][x].inverseFlag();
                            countFlags--;
                        }
                        System.out.println(field[x][y].countNotOpenedNeighbours(x, y) + " Not Opened Neighbours");
                        System.out.println(field[x][y].countFlaggedNeighbours(x, y) + " Flagged Neighbours");
                        System.out.println(field[x][y].countMinedNeighbours(x, y) + " Mined Neighbours");
                    }
                    if (bangMine || youWon){
                        timeLabel.stopTimer();
                        bot.stopTimer(); // game over
                    }
                    canvas.repaint();
                }
            }
        });
        add(BorderLayout.CENTER, canvas);
        add(BorderLayout.SOUTH, timeLabel);
        setVisible(true);
        initField();
    }

    void openCells(int x, int y) { // recursive procedure of opening the cells
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return; // wrong coordinates
        if (!field[y][x].isNotOpen()) return; // cell is already open
        field[y][x].open();
        if (field[y][x].getCountBomb() > 0 || bangMine) return; // the cell is not empty
        for (int dx = -1; dx < 2; dx++)
            for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
    }

    void initField() { // initialization of the playing field
        int x, y, countMines = 0;
        // create cells for the field
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                field[y][x] = new Cell();
        // to mine field
        if (!testMines) {
            while (countMines < NUMBER_OF_MINES) {
                do {
                    x = random.nextInt(FIELD_SIZE);
                    y = random.nextInt(FIELD_SIZE);
                } while (field[y][x].isMined());
                field[y][x].mine();
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

    class Bot {
        private Canvas canvas;
        private TimerLabel timerLabel;

        Timer timer = new Timer();

        Bot(Canvas canvas, TimerLabel timerLabel) {
            this.canvas = canvas;
            this.timerLabel = timerLabel;
            timer.schedule(timerTask, 1000, 1000);
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                botTurn();
                youWon = (countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES) ||
                        (countFlags == NUMBER_OF_MINES);
                if (youWon){
                    timerLabel.stopTimer();
                    stopTimer(); // game over
                }
                canvas.repaint();
            }
        };

        void stopTimer() {
            timer.cancel();
        }

    }

    private void botTurn() {

        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (!field[j][i].isNotOpen()) {
                    if (field[j][i].countMinedNeighbours(i, j) == field[j][i].countFlaggedNeighbours(i, j)) {
                        field[j][i].openUnflaggedNeighbours(i, j);
                    }
                    if (field[j][i].countNotOpenedNeighbours(i, j) == field[j][i].countMinedNeighbours(i, j) -
                            field[j][i].countFlaggedNeighbours(i, j)) {
                        field[j][i].flagNotOpenedNeighbours(i, j);
                    }
                }
            }
        }
        // youWon = countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES; // winning check
    }

    class Cell { // playing field cell
        private int countBombNear;
        private boolean isOpen, isMine, isFlag;

        void open() {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }

        //считает количество неоткрытых соседних клеток без флагов
        int countNotOpenedNeighbours(int i, int j) {
            int count = 0;
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    int nX = i + dx;
                    int nY = j + dy;
                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                        nX = i;
                        nY = j;
                    }
                    count += (field[nY][nX].isNotOpen() && field[nY][nX].isNotFlagged()) ? 1 : 0;
                }
            }
            return count;
        }

        //считает количество соседних ячеек с флагами
        int countFlaggedNeighbours(int i, int j) {
            int count = 0;
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    int nX = i + dx;
                    int nY = j + dy;
                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                        nX = i;
                        nY = j;
                    }
                    count += (!field[nY][nX].isNotFlagged()) ? 1 : 0;
                }
            }
            return count;
        }

        //устанавливает флаги на все неоткрытые соседние ячейки
        void flagNotOpenedNeighbours(int i, int j) {
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    int nX = i + dx;
                    int nY = j + dy;
                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                        nX = i;
                        nY = j;
                    }
                    if (field[nY][nX].isNotFlagged() && field[nY][nX].isNotOpen()) field[nY][nX].inverseFlag();
                }
            }
        }

        //открывает все соседние ячейки без флагов
        void openUnflaggedNeighbours(int i, int j) {
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    int nX = i + dx;
                    int nY = j + dy;
                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                        nX = i;
                        nY = j;
                    }
                    if (field[nY][nX].isNotFlagged() && field[nY][nX].isNotOpen()) openCells(nX, nY);
                }
            }
        }

        //считает количество заминированых клеток рядом
        int countMinedNeighbours(int i, int j) {
            int count = 0;
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    int nX = i + dx;
                    int nY = j + dy;
                    if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                        nX = i;
                        nY = j;
                    }
                    count += (field[nY][nX].isMined()) ? 1 : 0;
                }
            }
            return count;
        }

        void mine() {
            isMine = true;
        }

        void mine(Cell cell) {
            cell.mine();
            testMines = true;
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

        boolean isNotFlagged() {
            return !isFlag;
        }

        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * BLOCK_SIZE + 7, y * BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x * BLOCK_SIZE + 11, y * BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x * BLOCK_SIZE + 9, y * BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x * BLOCK_SIZE + 11, y * BLOCK_SIZE + 10, 4, 4);
        }

        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x * BLOCK_SIZE + 8, y * BLOCK_SIZE + 26);
        }

        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen) {
                if ((bangMine || youWon) && isMine) paintBomb(g, x, y, Color.black);
                else {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, SIGN_OF_FLAG, x, y, Color.red);
                }
            } else if (isMine) paintBomb(g, x, y, bangMine ? Color.red : Color.black);
            else if (countBombNear > 0)
                paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
        }
    }

    class TimerLabel extends JLabel { // label with stopwatch
        Timer timer = new Timer();

        TimerLabel() {
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        } // TimerTask task, long delay, long period

        TimerTask timerTask = new TimerTask() {
            volatile int time;
            Runnable refresher = new Runnable() {
                public void run() {
                    TimerLabel.this.setText(String.format("%02d:%02d", time / 60, time % 60));
                }
            };

            public void run() {
                time++;
                SwingUtilities.invokeLater(refresher);
            }
        };

        void stopTimer() {
            timer.cancel();
            if (youWon) TimerLabel.this.setText("You Win!");
            if (bangMine) TimerLabel.this.setText("You Lose!");
        }

    }

    class Canvas extends JPanel { // my canvas for painting
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++) field[y][x].paint(g, x, y);
        }
    }
}
