package edu.uoc.nertia.controller;

import edu.uoc.nertia.model.cells.Cell;
import edu.uoc.nertia.model.cells.Element;
import edu.uoc.nertia.model.exceptions.LevelException;
import edu.uoc.nertia.model.exceptions.PositionException;
import edu.uoc.nertia.model.leaderboard.LeaderBoard;
import edu.uoc.nertia.model.levels.Level;
import edu.uoc.nertia.model.levels.LevelDifficulty;
import edu.uoc.nertia.model.stack.StackItem;
import edu.uoc.nertia.model.utils.Direction;
import edu.uoc.nertia.model.utils.MoveResult;
import edu.uoc.nertia.model.utils.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;


/**
 * Controller class of the game. It is the middleware (or bridge) between the model and view classes.
 * <br/>
 * This class is called from the view classes in order to access/modify the model data.
 *
 *  @author David García-Solórzano
 *  @version 1.0
*/
public class Game {

    /**
     * Name of the folder in which level files are
     */
    private String fileFolder;

    /**
     * Number of the current level.
     */
    private int currentLevel;

    /**
     * Maximum quantity of levels that the game has.
     */
    private final int maxLevels;

    /**
     * Total score of the game, i.e. the sum of the levels' scores.
     */
    private int score;

    /**
     * Level object that contains the information of the current level.
     */
    private Level level;

    /**
     * LeaderBoard object that manages the leaderboard of the game.
     */
    private LeaderBoard leaderBoard;

    /**
     * Constructor
     *
     * @param fileFolder Folder name where the configuration/level files are.
     * @throws IOException When there is a problem while retrieving number of levels
     */
    public Game(String fileFolder) throws IOException {
        int num;

        setFileFolder(fileFolder);

        //Get the number of files that are in the fileFolder, i.e. the number of levels.
        URL url = getClass().getClassLoader().getResource(getFileFolder());

        URLConnection urlConnection = Objects.requireNonNull(url).openConnection();

        if (urlConnection instanceof JarURLConnection) {
            //run in jar
            String path = null;
            try {
                path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            } catch (URISyntaxException e) {
                System.out.println("ERROR: Game Constructor");
                e.printStackTrace();
                System.exit(-1);
            }

            URI uri = URI.create("jar:file:" + path);

            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                num = (int) Files.walk(fs.getPath(getFileFolder()))
                        .filter(Files::isRegularFile).count();
            }
        } else {
            //run in ide
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream;
            inputStream = Objects.requireNonNull(classLoader.getResourceAsStream(getFileFolder()));

            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                num = (int) reader.lines().count();
            }
        }

        //We load the leaderboard
        leaderBoard = new LeaderBoard(5);

        setScore(0);

        maxLevels = num;
        currentLevel = 1;
        try {
            loadLevel();
        } catch (LevelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter of the attribute {@code fileFolder}.
     *
     * @param fileFolder Folder name where the configuration/level files are.
     */
    private void setFileFolder(String fileFolder) {
        //TODO
        this.fileFolder = fileFolder;
    }

    /**
     * Getter of the attribute {@code fileFolder}.
     *
     * @return Value of the attribute {@code fileFolder}.
     */
    private String getFileFolder() {
        //TODO
        return fileFolder;
    }


    /**
     * Returns the size of the board. The board is NxN.
     *
     * @return Value of the board's size.
     */
    public int getBoardSize() {
        //TODO
        return level.getSize();
    }

    public int getScore() {
        //TODO
        return score;
    }

    private void setScore(int score) {
        //TODO
        this.score = score;
    }

    /**
     * Returns the {@link Cell} object which is in the position {@code (row,column)}.
     *
     * @param row    Row in which the cell we want to retrieve is
     * @param column Column in which the cell we want to retrieve is
     * @return The cell that is in the position {@code (row,column)}.
     * @throws LevelException When either the row or the column is wrong.
     */
    public Cell getCell(int row, int column) throws LevelException {
        //TODO
        return level.getCell(row, column);
    }

    /**
     * Returns the difficulty of the current level.
     *
     * @return The difficulty of the current level.
     */
    public LevelDifficulty getDifficulty() {
        //TODO
        if (level.getDifficulty() == LevelDifficulty.EASY) {
            return LevelDifficulty.EASY;
        } else if (level.getDifficulty() == LevelDifficulty.MEDIUM) {
            return LevelDifficulty.MEDIUM;
        } else if (level.getDifficulty() == LevelDifficulty.HARD) {
            return LevelDifficulty.HARD;
        } else {
            return null;
        }
    }

    /**
     * Returns the number of moves that have been done in the current level so far.
     *
     * @return Number of moves that the player has done so far. If level is null, then returns 0.
     */
    public int getNumMoves() {
        //TODO
        return level.getNumMoves();
    }

    /**
     * Returns the number of lives that the player has.
     *
     * @return Number of lives.
     */
    public int getNumLives() {
        //TODO
        return level.getNumLives();
    }

    /**
     * Indicates if the game is finished ({@code true}) or not ({@code false}).
     * <p>The game is finished when the attribute {@code currentLevel} is equal to attribute {@code maxLevels}.
     * </p>
     *
     * @return True if there are no more levels and therefore the game is finished. Otherwise, false.
     */
    public boolean isFinished() {
        //TODO
        return (getCurrentLevel() == maxLevels);
    }

    /**
     * Getter of the attribute {@code currentLevel}.
     *
     * @return Value of the attribute {@code currentLevel} that indicates which level the player is playing.
     */
    public int getCurrentLevel() {
        //TODO
        return currentLevel;
    }

    /**
     * Checks if there is a new level to play and loads it.<br/>
     * If the game is finished, it returns {@code false}. Otherwise, it returns {@code true}.
     * The game score must be updated when a level is finished.
     * Thus, when the player is playing the first level, game's score is zero.
     *
     * @return True if there is a next level, and it has been loaded correctly. Otherwise, it returns false.
     * @throws LevelException When there is a level exception/problem loading the new level.
     */
    public boolean nextLevel() throws LevelException {
        //TODO
        int a = this.level.getScore() + this.getScore();
        if (this.isFinished()) {
            if (isLevelCompleted()) {
                setScore(a);
            }
            return false;
        } else {
            if (isLevelCompleted()) {
                setScore(a);
                currentLevel += 1;
                loadLevel();
            }
            return true;
        }
    }

    /**
     * Loads a new level by using the value of the attribute {@code currentLevel}.
     * <p>
     * The pattern of the filename is: fileFolder+"level" + numberLevel + ".txt".
     * </p>
     *
     * @throws LevelException When there is a level exception/problem.
     */
    private void loadLevel() throws LevelException {
        //TODO
        level = new Level(fileFolder + "level" + currentLevel + ".txt");
    }

    /**
     * Checks if the level is completed, i.e. the player has collected all the gems of the board.
     *
     * @return {@code true} if this level is beaten, otherwise {@code false}.
     */
    public boolean isLevelCompleted() {
        //TODO
        return level.getNumGemsGot() == level.getNumGemsInit();
    }

    /**
     * Checks if the player has lost, i.e. the number of lives is zero.
     *
     * @return {@code true} if this the player has lost, otherwise {@code false}.
     */
    public boolean hasLost() {
        //TODO
        return level.hasLost();
    }

    /**
     * Undo one move from the level's stack.
     *
     * @return {@code true} if one move has been undone, otherwise {@code false} (e.g. the stack is empty).
     * @throws LevelException When either the row or the column is wrong.
     */
    public boolean undo() throws LevelException {
        //TODO
        return level.undo();
    }

    /**
     * Reloads the current level, i.e. load the level again.
     *
     * @throws LevelException When there is a level exception/problem.
     */
    public void reload() throws LevelException {
        //TODO
        this.level = new Level(fileFolder + "level" + currentLevel + ".txt");

    }

    /**
     * Moves the player in the given direction. If the move ends in:
     * <ul>
     * <li>a mine, then it returns {@link MoveResult#DIE}</li>
     * <li>another kind of cell, then it returns {@link MoveResult#OK}</li>
     * </ul>
     * If the first cell is out of bounds, then it returns {@link MoveResult#KO} (i.e. INVALID move)
     *
     * @param direction Direction to move the player in.
     * @return MoveResult object the move is done.
     * @throws LevelException    If there are any problems with increaseNumGemsGot.
     * @throws PositionException If there are any problems while managing positions.
     */
    public MoveResult movePlayer(Direction direction) throws LevelException {
        //TODO


        if (direction == Direction.RIGHT) {
            int collectedGemsMove = 0;
            int[] gemColumn = new int[level.getNumGemsInit()];
            Arrays.fill(gemColumn, 0);
            Position playerPos = level.getPlayerPosition();
            int i = 1;
            if (playerPos.getColumn() == level.getSize() - 1 || getCell(playerPos.getRow(), playerPos.getColumn() + 1).getElement() == Element.WALL) {
                return MoveResult.KO;
            } else {
                while (i < level.getSize()) {
                    if (getCell(playerPos.getRow(), playerPos.getColumn() + i).getElement() == Element.MINE) {
                        level.decreaseNumLives();

                        int j = 0;
                        while (j < collectedGemsMove) {
                            level.decreaseNumGemsGot();
                            j += 1;
                        }
                        int k = 0;
                        while (k < collectedGemsMove) {
                            level.setCell(playerPos.offsetBy(0, gemColumn[k]), Element.GEM);
                            k += 1;
                        }
                        level.increaseNumMoves();

                        return MoveResult.DIE;
                    } else if (getCell(playerPos.getRow(), playerPos.getColumn() + i).getElement() == Element.STOP) {
                        level.setCell(playerPos.offsetBy(0, i), Element.PLAYER_STOP);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();



                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));
                        return MoveResult.OK;

                    } else if (getCell(playerPos.getRow(), playerPos.getColumn() + i).getElement() == Element.WALL) {
                        level.setCell(playerPos.offsetBy(0, i - 1), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER_STOP) {
                            level.setCell(playerPos, Element.STOP);
                        } else {
                            level.setCell(playerPos, Element.EMPTY);
                        }
                        level.increaseNumMoves();



                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));
                        return MoveResult.OK;

                    } else if (playerPos.getColumn() + i == level.getSize() - 1 && getCell(playerPos.getRow(), playerPos.getColumn() + i).getElement() == Element.EMPTY) {
                        level.setCell(playerPos.offsetBy(0, i), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER_STOP) {
                            level.setCell(playerPos, Element.STOP);
                        } else {
                            level.setCell(playerPos, Element.EMPTY);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));
                        return MoveResult.OK;
                    } else if (getCell(playerPos.getRow(), playerPos.getColumn() + i).getElement() == Element.GEM) {
                        level.increaseNumGemsGot(1);
                        if ((playerPos.getColumn() + i == level.getSize() - 1)) {
                            level.setCell(playerPos.offsetBy(0, i), Element.PLAYER);
                            if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER_STOP) {
                                level.setCell(playerPos, Element.STOP);
                            } else {
                                level.setCell(playerPos, Element.EMPTY);
                            }
                            level.increaseNumMoves();

                            List<Position> gems = new ArrayList<>();
                            Position gemPos = null;
                            try {
                                gemPos = new Position(playerPos.getRow(), playerPos.getColumn()+i);
                            } catch (PositionException e) {
                                e.printStackTrace();
                            }
                            gems.add(gemPos);

                            level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), gems));
                            return MoveResult.OK;
                        } else {
                            gemColumn[collectedGemsMove] += i;
                            collectedGemsMove += 1;
                            level.setCell(playerPos.offsetBy(0, i), Element.EMPTY);
                            i = i + 1;
                        }
                    } else {
                        i = i + 1;
                    }
                }
            }
        } else if (direction == Direction.LEFT) {
            int collectedGemsMove = 0;
            int[] gemColumn = new int[level.getNumGemsInit()];
            Arrays.fill(gemColumn, 0);
            Position playerPos = level.getPlayerPosition();
            if (playerPos.getColumn() == 0 || getCell(playerPos.getRow(), playerPos.getColumn() - 1).getElement() == Element.WALL) {
                return MoveResult.KO;
            } else {
                int i = 1;
                while (i < level.getSize()) {
                    if (getCell(playerPos.getRow(), playerPos.getColumn() - i).getElement() == Element.MINE) {
                        level.decreaseNumLives();

                        int k = 0;
                        while (k < collectedGemsMove) {
                            level.setCell(playerPos.offsetBy(0, -gemColumn[k]), Element.GEM);
                            k += 1;
                        }

                        int j = 0;
                        while (j < collectedGemsMove) {
                            level.decreaseNumGemsGot();
                            j += 1;
                        }
                        level.increaseNumMoves();

                        return MoveResult.DIE;
                    } else if (getCell(playerPos.getRow(), playerPos.getColumn() - i).getElement() == Element.STOP) {
                        level.setCell(playerPos.offsetBy(0, -i), Element.PLAYER_STOP);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));
                        return MoveResult.OK;
                    } else if (getCell(playerPos.getRow(), playerPos.getColumn() - i).getElement() == Element.WALL) {
                        level.setCell(playerPos.offsetBy(0, -i + 1), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));
                        return MoveResult.OK;
                    } else if (playerPos.getColumn() - i == 0 && getCell(playerPos.getRow(), playerPos.getColumn() - i).getElement() == Element.EMPTY) {
                        level.setCell(playerPos.offsetBy(0, -i), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));
                        return MoveResult.OK;
                    } else if (getCell(playerPos.getRow(), playerPos.getColumn() - i).getElement() == Element.GEM) {
                        if ((playerPos.getColumn() - i == 0)) {
                            level.setCell(playerPos.offsetBy(0, -i), Element.PLAYER);
                            level.increaseNumGemsGot(1);
                            if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER_STOP) {
                                level.setCell(playerPos, Element.STOP);
                            } else {
                                level.setCell(playerPos, Element.EMPTY);
                            }
                            level.increaseNumMoves();



                            List<Position> gems = new ArrayList<>();
                            Position gemPos = null;
                            try {
                                gemPos = new Position(playerPos.getRow(), playerPos.getColumn()+i);
                            } catch (PositionException e) {
                                e.printStackTrace();
                            }
                            gems.add(gemPos);

                            level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), gems));
                            return MoveResult.OK;
                        } else {
                            level.increaseNumGemsGot(1);
                            level.setCell(playerPos.offsetBy(0, -i), Element.EMPTY);
                            collectedGemsMove += 1;
                            gemColumn[collectedGemsMove] += i;
                            i = i + 1;
                        }
                    } else {

                        i = i + 1;
                    }
                }
            }
        } else if (direction == Direction.UP) {
            int collectedGemsMove = 0;
            int[] getRow = new int[level.getNumGemsInit()];
            Arrays.fill(getRow, 0);
            Position playerPos = level.getPlayerPosition();
            if (playerPos.getRow() == 0 || getCell(playerPos.getRow() - 1, playerPos.getColumn()).getElement() == Element.WALL) {
                return MoveResult.KO;
            } else {
                int i = 1;
                while (i < level.getSize()) {
                    if (getCell(playerPos.getRow() - i, playerPos.getColumn()).getElement() == Element.MINE) {
                        level.decreaseNumLives();

                        int k = 0;
                        while (k < collectedGemsMove) {
                            level.setCell(playerPos.offsetBy(-getRow[k], 0), Element.GEM);
                            k += 1;
                        }

                        int j = 0;
                        while (j < collectedGemsMove) {
                            level.decreaseNumGemsGot();
                            j += 1;
                        }
                        level.increaseNumMoves();

                        return MoveResult.DIE;
                    } else if (getCell(playerPos.getRow() - i, playerPos.getColumn()).getElement() == Element.STOP) {
                        level.setCell(playerPos.offsetBy(-i, 0), Element.PLAYER_STOP);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));

                        return MoveResult.OK;
                    } else if (getCell(playerPos.getRow() - i, playerPos.getColumn()).getElement() == Element.WALL) {
                        level.setCell(playerPos.offsetBy(-i + 1, 0), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));

                        return MoveResult.OK;
                    } else if (playerPos.getRow() - i == 0 && getCell(playerPos.getRow() - i, playerPos.getColumn()).getElement() == Element.EMPTY) {
                        level.setCell(playerPos.offsetBy(-i, 0), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));

                        return MoveResult.OK;
                    } else if (getCell(playerPos.getRow() - i, playerPos.getColumn()).getElement() == Element.GEM) {
                        if ((playerPos.getRow() - i == 0)) {
                            level.setCell(playerPos.offsetBy(-i, 0), Element.PLAYER);
                            level.increaseNumGemsGot(1);
                            if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER_STOP) {
                                level.setCell(playerPos, Element.STOP);
                            } else {
                                level.setCell(playerPos, Element.EMPTY);
                            }
                            level.increaseNumMoves();



                            List<Position> gems = new ArrayList<>();
                            Position gemPos = null;
                            try {
                                gemPos = new Position(playerPos.getRow(), playerPos.getColumn()+i);
                            } catch (PositionException e) {
                                e.printStackTrace();
                            }
                            gems.add(gemPos);

                            level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), gems));
                            return MoveResult.OK;
                        } else {
                            level.increaseNumGemsGot(1);
                            level.setCell(playerPos.offsetBy(-i, 0), Element.EMPTY);
                            collectedGemsMove += 1;
                            getRow[collectedGemsMove] += i;
                            i = i + 1;
                        }
                    } else {
                        i = i + 1;
                    }
                }
            }
        } else if (direction == Direction.DOWN) {
            int collectedGemsMove = 0;
            int[] getRow = new int[level.getNumGemsInit()];
            Arrays.fill(getRow, 0);
            Position playerPos = level.getPlayerPosition();
            if (playerPos.getRow() == level.getSize() - 1 || getCell(playerPos.getRow() + 1, playerPos.getColumn()).getElement() == Element.WALL) {
                return MoveResult.KO;
            } else {
                int i = 1;
                while (i < level.getSize()) {
                    if (getCell(playerPos.getRow() + i, playerPos.getColumn()).getElement() == Element.MINE) {
                        level.decreaseNumLives();

                        int k = 0;
                        while (k < collectedGemsMove) {
                            level.setCell(playerPos.offsetBy(getRow[k], 0), Element.GEM);
                            k += 1;
                        }

                        int j = 0;
                        while (j < collectedGemsMove) {
                            level.decreaseNumGemsGot();
                            j += 1;
                        }

                        level.increaseNumMoves();
                        return MoveResult.DIE;
                    } else if (getCell(playerPos.getRow() + i, playerPos.getColumn()).getElement() == Element.STOP) {
                        level.setCell(playerPos.offsetBy(i, 0), Element.PLAYER_STOP);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));

                        return MoveResult.OK;
                    } else if (getCell(playerPos.getRow() + i, playerPos.getColumn()).getElement() == Element.WALL) {
                        level.setCell(playerPos.offsetBy(i - 1, 0), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));

                        return MoveResult.OK;
                    } else if (playerPos.getRow() + i == level.getSize() - 1 && getCell(playerPos.getRow() + i, playerPos.getColumn()).getElement() == Element.EMPTY) {
                        level.setCell(playerPos.offsetBy(i, 0), Element.PLAYER);
                        if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER) {
                            level.setCell(playerPos, Element.EMPTY);
                        } else {
                            level.setCell(playerPos, Element.STOP);
                        }
                        level.increaseNumMoves();




                        level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), new ArrayList<>()));

                        return MoveResult.OK;
                    } else if (getCell(playerPos.getRow() + i, playerPos.getColumn()).getElement() == Element.GEM) {
                        if ((playerPos.getRow() + i == level.getSize() - 1)) {
                            level.setCell(playerPos.offsetBy(i, 0), Element.PLAYER);
                            level.increaseNumGemsGot(1);
                            if (getCell(playerPos.getRow(), playerPos.getColumn()).getElement() == Element.PLAYER_STOP) {
                                level.setCell(playerPos, Element.STOP);
                            } else {
                                level.setCell(playerPos, Element.EMPTY);
                            }
                            level.increaseNumMoves();



                            List<Position> gems = new ArrayList<>();
                            Position gemPos = null;
                            try {
                                gemPos = new Position(playerPos.getRow(), playerPos.getColumn()+i);
                            } catch (PositionException e) {
                                e.printStackTrace();
                            }
                            gems.add(gemPos);

                            level.push(new StackItem(playerPos, Element.PLAYER, new ArrayList<>(), gems));
                            return MoveResult.OK;
                        } else {
                            level.increaseNumGemsGot(1);
                            level.setCell(playerPos.offsetBy(i, 0), Element.EMPTY);
                            collectedGemsMove += 1;
                            getRow[collectedGemsMove] += i;
                            i = i + 1;
                        }
                    } else {
                        i = i + 1;
                    }
                }
            }
        }
        return MoveResult.OK;
    }

    /**
     * Checks if the score gotten by the player deserves to be stored in the leaderboard.
     *
     * @return {@code true} if the score can be stored in the leaderboard. Otherwise, {@code false}.
     */
    public boolean isInLeaderBoard() {
        //TODO
        return leaderBoard.isInTheTop(score);
    }

    /**
     * Add the score in the leaderboard.
     *
     * @param name Player's name.
     */
    public void addToLeaderBoard(String name) {
        //TODO
        leaderBoard.add(name, score);
    }

    /**
     * Prints the leaderboard.
     */
    public void displayLeaderBoard() {
        //TODO
        System.out.print(leaderBoard);
    }

    /**
     * Returns the status of the game at that moment.
     *
     * @return Textual version of the game. This includes the board and level's status.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        //TODO: Here you have to put your code so that the board is displayed.
        int i = 0;
        while (i < this.level.getSize()) {
            int j = 0;
            while (j < this.level.getSize()) {
                try {
                    str.append(level.getCell(i, j).getElement().getSymbol());
                } catch (LevelException e) {
                    e.printStackTrace();
                }
                j += 1;
            }
            i += 1;
            str.append(System.lineSeparator());
        }
        str.append("#Lives: ")
                .append(level.getNumLives())
                .append(" | #Moves: ")
                .append(level.getNumMoves())
                .append(" | #Gems: ")
                .append(level.getNumGemsGot())
                .append(" | Level Score: ")
                .append(level.getScore())
                .append(" pts")
                .append(" | Game Score: ")
                .append(getScore())
                .append(" pts")
                .append(System.lineSeparator())
                .append("Enter Your Move (UP/DOWN/LEFT/RIGHT/UNDO/QUIT): ");

        return str.toString();
    }


}
