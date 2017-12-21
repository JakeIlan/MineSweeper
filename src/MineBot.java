package Game;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class MineBot {
    private Random random = new Random();
    private int info = 0;
    private String massage;


    MineBot() {
        this.info = 0;
    }

    void setInfo(int info) {
        this.info = info;
    }

    int getInfo() {
        return info;
    }

    void logic(GameMines.Cell[][] field, int fieldSize) {
        if (info == 0) randomOpen(field, fieldSize);
        if (info == 1) simpleTurn(field, fieldSize);
        //if (info == 2) smartTurn(field, fieldSize);
    }

    private void smartTurn(GameMines.Cell[][] field, int fieldSize) {
        boolean success = false;
        Map<GameMines.Cell, Double> cells = new HashMap<>();
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[j][i].isNotOpen() && countNotOpenedNeighbours(i, j, field, fieldSize) > 0) {

                }

            }
        }
        if (!success) info = 0;
    }

    void simpleTurn(GameMines.Cell[][] field, int fieldSize) {
        boolean success = false;
        // ArrayList<Coordinates> list = new ArrayList<Coordinates>();
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (!field[j][i].isNotOpen()) {
                    if (field[j][i].getCountBomb() == countFlaggedNeighbours(i, j, field, fieldSize)) {
                        info = 1;
                        success = true;
                        openUnflaggedNeighbours(i, j, field, fieldSize);
                    }
                    if (countFlaggedNeighbours(i, j, field, fieldSize) == field[j][i].getCountBomb() -
                            countNotOpenedNeighbours(i, j, field, fieldSize)) {
                        info = 1;
                        success = true;
                        flagNotOpenedNeighbours(i, j, field, fieldSize);
                    }
//                    if (field[j][i].getCountBomb() == 0) zeroOpen(i, j, field, fieldSize);
                }
            }
        }
        if (!success) info = 0;
        // return list;
    }

    private void flagNotOpenedNeighbours(int i, int j, GameMines.Cell[][] field, int fieldSize) {
        // ArrayList<Coordinates> list = new ArrayList<Coordinates>();
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = i + dx;
                int nY = j + dy;
                if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                    nX = i;
                    nY = j;
                }
                if (field[nY][nX].isNotFlagged() && field[nY][nX].isNotOpen()) field[nY][nX].inverseFlag();
            }
        }
        // return list;
        //return new Coordinates(-1, -1, true);
    }

    Coordinates randomOpen(GameMines.Cell[][] field, int fieldSize) {
        int x, y;
        do {
            x = random.nextInt(fieldSize);
            y = random.nextInt(fieldSize);
        } while (!field[y][x].isNotOpen());
        info = 1;
        return new Coordinates(x, y, true);
        //field[y][x].open();
    }

    //считает количество соседних ячеек с флагами
    private int countFlaggedNeighbours(int i, int j, GameMines.Cell[][] field, int fieldSize) {
        int count = 0;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = i + dx;
                int nY = j + dy;
                if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                    nX = i;
                    nY = j;
                }
                count += (!field[nY][nX].isNotFlagged()) ? 1 : 0;
            }
        }
        return count;
    }

    //открывает все соседние ячейки без флагов
    private void openUnflaggedNeighbours(int i, int j, GameMines.Cell[][] field, int fieldSize) {
        //ArrayList<Coordinates> list = new ArrayList<Coordinates>();
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = i + dx;
                int nY = j + dy;
                if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                    nX = i;
                    nY = j;
                }
                if (field[nY][nX].isNotFlagged() && field[nY][nX].isNotOpen())
                    zeroOpen(nX, nY, field, fieldSize); //field[nY][nX].open();
            }
        }
        // return list;

    }

    private int countNotOpenedNeighbours(int i, int j, GameMines.Cell[][] field, int fieldSize) {
        int count = 0;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = i + dx;
                int nY = j + dy;
                if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                    nX = i;
                    nY = j;
                }
                count += (field[nY][nX].isNotOpen() && field[nY][nX].isNotFlagged()) ? 1 : 0;
            }
        }
        return count;
    }

    private void zeroOpen(int i, int j, GameMines.Cell[][] field, int fieldSize) {

        if (!field[j][i].isNotOpen()) return; // cell is already open
        field[j][i].open();
        if (field[j][i].getCountBomb() > 0) return; // the cell is not empty
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = i + dx;
                int nY = j + dy;
                if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                    nX = i;
                    nY = j;
                }
                zeroOpen(nX, nY, field, fieldSize);
            }


        }
    }
}