package Game;


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
        if (info != 0) {
            simpleTurn(field, fieldSize);

                boolean smartTurn = smartTurn(field, fieldSize);

        } else randomOpen(field, fieldSize);

    }

    Boolean smartTurn(GameMines.Cell[][] field, int fieldSize) {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                field[j][i].setPossibility(0.0);
            }
        }
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (!field[j][i].isNotOpen()) {
                    double ver = (double) field[j][i].getCountBomb() / countNotOpenedNeighbours(i, j, field, fieldSize);
                    setNeighboursPossibility(i, j, field, fieldSize, ver);
                }
            }
        }
        for (int k = 0; k < 150; k++) {
            for (int i = 0; i < fieldSize; i++) {
                for (int j = 0; j < fieldSize; j++) {
                    if (!field[j][i].isNotOpen()) {
                        balance(i, j, field, fieldSize);
                    }
                }
            }
            k++;
        }
        double min = 1.0;
        int mi = -1;
        int mj = -1;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[j][i].isNotOpen()) {
                    if (field[j][i].getPossibility() < min && field[j][i].getPossibility() > 0.0) {
                        min = field[j][i].getPossibility();
                        mi = i;
                        mj = j;
                    }
                }
            }
        }
        if (check(mi, mj, fieldSize)) field[mj][mi].open();
        System.out.println(min);
        //info = 1;
        return false;
    }

    private boolean check(int i, int j, int fieldSize) {
        return !(i < 0 || j < 0 || i > fieldSize - 1 || j > fieldSize - 1);
    }

    void simpleTurn(GameMines.Cell[][] field, int fieldSize) {
        boolean success = false;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (!field[j][i].isNotOpen()) {
                    if (field[j][i].getCountBomb() == countFlaggedNeighbours(i, j, field, fieldSize)) {
                        //info = 2;
                        openUnflaggedNeighbours(i, j, field, fieldSize);
                        info = 1;
                        success = true;
                    }
                    if (countFlaggedNeighbours(i, j, field, fieldSize) == field[j][i].getCountBomb() -
                            countNotOpenedNeighbours(i, j, field, fieldSize)) {
                        //info = 2;
                        flagNotOpenedNeighbours(i, j, field, fieldSize);
                        info = 1;
                        success = true;
                    }
                }
            }
        }
        if (!success) info = 0;
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
        //info = 1;
        return new Coordinates(x, y);
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
                    //zeroOpen(nX, nY, field, fieldSize);
                field[nY][nX].open();
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

    private void balance(int i, int j, GameMines.Cell[][] field, int fieldSize) {
        double var = field[j][i].getCountBomb() / getNeighboursPossibility(i, j, field, fieldSize);
        if (var != 1.0) {
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    int nX = i + dx;
                    int nY = j + dy;
                    if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                        nX = i;
                        nY = j;
                    }
                    if (field[nY][nX].isNotOpen()) field[nY][nX].setPossibility(field[nY][nX].getPossibility() * var);

                }
            }
        }
    }

    private double getNeighboursPossibility(int i, int j, GameMines.Cell[][] field, int fieldSize) {
        double sum = 0.0;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = i + dx;
                int nY = j + dy;
                if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                    nX = i;
                    nY = j;
                }
                if (field[nY][nX].isNotOpen()) sum += field[nY][nX].getPossibility();

            }
        }
        return sum;
    }

    private void setNeighboursPossibility(int i, int j, GameMines.Cell[][] field, int fieldSize, double d) {
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = i + dx;
                int nY = j + dy;
                if (nX < 0 || nY < 0 || nX > fieldSize - 1 || nY > fieldSize - 1) {
                    nX = i;
                    nY = j;
                }
                if (field[nY][nX].isNotOpen()) field[nY][nX].setPossibility(field[nY][nX].getPossibility() + d);
                if (field[nY][nX].getPossibility() > 0.99) field[nY][nX].setPossibility(0.99);

            }
        }
    }
}