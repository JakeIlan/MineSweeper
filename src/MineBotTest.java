package Game;

import org.junit.jupiter.api.Test;

public class MineBotTest {
    private GameMines game;
    private MineBot bot;

    @Test
    public void easyWinRate() {

        game = new GameMines(9, 10);
        bot = new MineBot();
        game.initField();
        while (!game.youWon && !game.bangMine) {
            if (bot.getInfo() == 0) {
                if (!game.bangMine) game.openCells(bot.randomOpen(game.field, 9));
                bot.setInfo(1);
            }
            if (bot.getInfo() == 1) {
                if (!game.bangMine) bot.simpleTurn(game.field, 9);
                game.youWon = (game.countOpenedCells == game.FIELD_SIZE * game.FIELD_SIZE - 10) ||
                        (game.countFlags == 10);

                bot.setInfo(2);
            }
            if (bot.getInfo() == 2) {
                if (!game.bangMine) bot.smartTurn(game.field, 9);
                bot.setInfo(1);
            }
        }
        if (game.youWon) System.out.println("win");
        if (game.bangMine) System.out.println("lose");
    }






}