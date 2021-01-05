import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.*;

public class MinesweeperGame extends Game {
    private static final int SIDE = 15;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    /*
    1. В методе openTile(int, int), если элемент не является миной и количество соседей-мин равно нулю, для каждого не открытого соседа должен рекурсивно вызываться метод openTile(int, int).
    2. Метод openTile(int, int) должен вызывать метод getNeighbors(GameObject), если элемент не является миной и количество соседей-мин равно нулю.
    3. В методе openTile(int, int), если элемент не является миной и количество соседей мин не равняется нулю, на игровое поле должно выводиться количество заминированных соседей.
    Используй метод setCellNumber(int, int, int).
    4. Метод openTile(int, int) не должен ничего выводить, если элемент не является миной и количество соседей мин равно нулю. Используй пустую строку.
     */
    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if (!isGameStopped && !gameObject.isFlag && !gameObject.isOpen) {
            gameObject.isOpen = true;
            countClosedTiles--;
            setCellColor(x, y, Color.GREEN);

            if (countClosedTiles == countMinesOnField && !gameObject.isMine) {
                score += 5;
                setScore(score);
                win();
            }
            if (!gameObject.isMine && gameObject.countMineNeighbors == 0) {
                setCellValue(x, y, "");
                for (GameObject gObj : getNeighbors(gameObject)) {
                    if (!gObj.isOpen) {
                        score += 5;
                        setScore(score);
                        openTile(gObj.getX(), gObj.getY());
                    }
                }
            } else if (!gameObject.isMine) {
                score += 5;
                setScore(score);
                setCellNumber(x, y, gameObject.countMineNeighbors);
            } else {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }
        }
    }

    private void markTile(int x, int y) {
        if (!isGameStopped) {
            GameObject gameObject = gameField[y][x];
            if (!gameObject.isOpen) {
                if (countFlags > 0) {
                    if (!gameObject.isFlag) {
                        gameObject.isFlag = true;
                        countFlags--;
                        setCellValue(x, y, FLAG);
                        setCellColor(x, y, Color.RED);
                    } else {
                        gameObject.isFlag = false;
                        countFlags++;
                        setCellValue(x, y, "");
                        setCellColor(x, y, Color.ORANGE);
                    }
                }
            }
        }
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 2;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.CYAN, "GAME OVER!!!", Color.BLACK, 45);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.CYAN, "YOU WON!!!", Color.BLACK, 45);
    }

    private void restart() {
        isGameStopped = false;
        countMinesOnField = 0;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        createGame();
    }

    private void countMineNeighbors() {
        int count = 0;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (!gameField[y][x].isMine) {
                    for (GameObject gObj : getNeighbors(gameField[y][x])) {
                        if (gObj.isMine) {
                            count++;
                        }
                    }
                    gameField[y][x].countMineNeighbors = count;
                    count = 0;

                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}