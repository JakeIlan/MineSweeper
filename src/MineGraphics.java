package Game;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MineGraphics extends JFrame {

    private final int BLOCK_SIZE = 50; // size of one block
    private int fieldSize; // in blocks
    private final int FIELD_DX = 6 + 12; // determined experimentally
    private final int FIELD_DY = 28 + 25 + 10;
    private final int START_LOCATION = 100;
    private final int MOUSE_BUTTON_LEFT = 1; // for mouse listener
    private final int MOUSE_BUTTON_RIGHT = 3;
    private int NUMBER_OF_MINES; //MINES
    private boolean botActivity = true;
    private GameMines game;


    public static void main(String[] args) {
        new MineGraphics();
    }


    private MineGraphics() {

        JFrame settings = new JFrame("Game Settings");
        settings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settings.setBounds(100, 100, 100, 100);

        Box box = Box.createVerticalBox();

        Box box1 = Box.createHorizontalBox();
        JTextField fieldSizeInput = new JTextField("10", 4);

        box1.add(fieldSizeInput);
        box1.setBorder(new TitledBorder("Field Size"));

        Box box2 = Box.createHorizontalBox();
        JTextField minesInput = new JTextField("10", 4);

        box2.add(minesInput);
        box2.setBorder(new TitledBorder("Number of mines"));

        Box box3 = Box.createHorizontalBox();

        box3.setBorder(new TitledBorder("Bot"));


        box.add(box1);
        box.add(box2);
        box.add(box3);
        settings.setContentPane(box);
        JFrame gameFrame = new JFrame("MineSweeper");
        JButton button = new JButton("Start game");
        button.addActionListener((ActionEvent e) -> {
            fieldSize = Integer.parseInt(fieldSizeInput.getText());
            NUMBER_OF_MINES = Integer.parseInt(minesInput.getText());

            initGame(fieldSize, NUMBER_OF_MINES, gameFrame);
        });

//        JButton button2 = new JButton("Start game");
//        button2.addActionListener((ActionEvent e) -> {
//            fieldSize = Integer.parseInt(fieldSizeInput.getText());
//            NUMBER_OF_MINES = Integer.parseInt(minesInput.getText());
//
//            initGame(fieldSize, NUMBER_OF_MINES, gameFrame);
//        });
//
//        settings.add(button2);

        settings.add(button);
        settings.setVisible(true);


        Box bbox1 = Box.createVerticalBox();
        MineBot bot = new MineBot();
        JTextField btext = new JTextField(10);

        JButton bLaunch = new JButton("Запуск бота");
        bLaunch.addActionListener((ActionEvent e) -> {
            if (bot.getInfo() == 0) {
                if (!game.bangMine) game.openCells(bot.randomOpen(game.field, fieldSize));
                btext.setText("Random");
                game.getCanvas().repaint();
                bot.setInfo(1);
            }
            if (bot.getInfo() == 1) {
                if (!game.bangMine) bot.simpleTurn(game.field, fieldSize);


                game.youWon = (game.countOpenedCells == game.FIELD_SIZE * game.FIELD_SIZE - NUMBER_OF_MINES) ||
                        (game.countFlags == NUMBER_OF_MINES);

                if (game.bangMine) JOptionPane.showMessageDialog(MineGraphics.this, "You lose!");
                if (game.youWon && !game.bangMine) JOptionPane.showMessageDialog(MineGraphics.this, "You win!");
                btext.setText("Common turn");
                game.getCanvas().repaint();
                bot.setInfo(2);
            }
            if (bot.getInfo() == 2) {
                if (!game.bangMine) bot.smartTurn(game.field, fieldSize);
                game.getCanvas().repaint();
                bot.setInfo(1);
            }
        });
        Box bbox2 = Box.createVerticalBox();
        bbox2.setBorder(new TitledBorder("Testing"));
        JButton simTurn = new JButton("Simple turn");
        JButton ranTurn = new JButton("Random turn");
        simTurn.addActionListener((ActionEvent e) -> {
            if (!game.bangMine) bot.simpleTurn(game.field, fieldSize);


            game.youWon = (game.countOpenedCells == game.FIELD_SIZE * game.FIELD_SIZE - NUMBER_OF_MINES) ||
                    (game.countFlags == NUMBER_OF_MINES);

            if (game.bangMine) JOptionPane.showMessageDialog(MineGraphics.this, "You lose!");
            if (game.youWon && !game.bangMine) JOptionPane.showMessageDialog(MineGraphics.this, "You win!");

            game.getCanvas().repaint();
        });

        ranTurn.addActionListener((ActionEvent e) -> {
            if (!game.bangMine) game.openCells(bot.randomOpen(game.field, fieldSize));

            game.getCanvas().repaint();
        });


        bbox1.add(btext);
        bbox1.add(bLaunch);
        bbox2.add(simTurn);
        bbox2.add(ranTurn);
        bbox1.setBorder(new TitledBorder("Бот"));
        box3.add(bbox1);
        box3.add(bbox2);
        settings.pack();
    }


    private void initGame(int fieldSize, int numberOfMines, JFrame gameField) {
        this.game = new GameMines(fieldSize, NUMBER_OF_MINES);
        game.initField();
        //int[] mines = {2, 15, 18, 45, 51, 73, 78, 85, 94, 95};
        //int[] mines = {0, 2};

       //game.initFieldTest(mines);
        //gameField.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameField.setBounds(430, 100, fieldSize * BLOCK_SIZE + FIELD_DX + 2, fieldSize * BLOCK_SIZE + FIELD_DY - 14);
        setResizable(false);
        //timer.setHorizontalAlignment(SwingConstants.CENTER);
        final Canvas canvas = new Canvas();
        game.setCanvas(canvas);
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX() / BLOCK_SIZE;
                int y = e.getY() / BLOCK_SIZE;
                if (!game.bangMine && !game.youWon) {
                    if (e.getButton() == MOUSE_BUTTON_LEFT) {// left button mouse
                        if (game.field[y][x].isNotOpen() && !game.field[y][x].isFlag) {
                            game.openCells(x, y);
                            game.youWon = (game.countOpenedCells == game.FIELD_SIZE * game.FIELD_SIZE - NUMBER_OF_MINES) ||
                                    (game.countFlags == NUMBER_OF_MINES);// winning check
                            if (game.bangMine) {
                                game.bangX = x;
                                game.bangY = y;
                            }

                        }


                    }
                    if (e.getButton() == MOUSE_BUTTON_RIGHT) {// right button mouse
                        if (game.field[y][x].isNotFlagged()) {
                            game.field[y][x].inverseFlag();
                            game.countFlags++;
                        } else {
                            game.field[y][x].inverseFlag();
                            game.countFlags--;
                        }
                    }
                    game.youWon = (game.countFlags == game.NUMBER_OF_MINES) ||
                            (game.countOpenedCells == fieldSize * fieldSize - game.NUMBER_OF_MINES);

                    canvas.repaint();
                    if (game.bangMine || game.youWon) {
                        if (game.bangMine) JOptionPane.showMessageDialog(MineGraphics.this, "You lose!");
                        if (game.youWon) JOptionPane.showMessageDialog(MineGraphics.this, "You win!");
                        // game over
                    }

                }
            }
        });
        gameField.add(BorderLayout.CENTER, canvas);
        gameField.setVisible(true);
    }

    class Canvas extends JPanel { // my canvas for painting
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < fieldSize; x++)
                for (int y = 0; y < fieldSize; y++) (game.field[y][x]).paint(g, x, y);

        }
    }


    private void endGameAlarm() {
        if (game.bangMine) JOptionPane.showMessageDialog(MineGraphics.this, "You lose!");
        if (game.youWon) JOptionPane.showMessageDialog(MineGraphics.this, "You win!");
    }

    public GameMines getGame() {
        return game;
    }
}
