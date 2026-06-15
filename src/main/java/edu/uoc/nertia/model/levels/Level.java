package edu.uoc.nertia.model.levels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;


import edu.uoc.nertia.model.exceptions.PositionException;
import edu.uoc.nertia.model.utils.Position;
import edu.uoc.nertia.model.cells.Cell;
import edu.uoc.nertia.model.cells.CellFactory;
import edu.uoc.nertia.model.cells.Element;
import edu.uoc.nertia.model.exceptions.LevelException;
import edu.uoc.nertia.model.stack.StackItem;
import edu.uoc.nertia.model.stack.UndoStack;


/**
 * Level class.
 * @author David García Solórzano
 * @version 1.0
 */
public class Level {

    /**
     * Minimum size of the board in one direction, the board will be sizexsize
     */
    private static final int MIN_SIZE = 3;

    /**
     * Number representing unlimited number of lives for a player.
     */
    private static final int UNLIMITED_LIVES = -1;

    /**
     * Number of rows and columns in the game board. A board is a square of size x size.
     */
    private final int size;

    /**
     * Difficulty of the level
     */
    private LevelDifficulty difficulty;

    /**
     * 2D array representing each cell in the game board.
     */
    private Cell[][] board;

    /**
     * The number of moves performed by the player (excluding invalid moves).
     */
    private int numMoves = 0;

    /**
     * The number of lives the player has.
     */
    private int numLives;

    /**
     * The number of gems the player has got.
     */
    private int numGemsGot = 0;

    /**
     * The number of gems initially on the game board when a {@link Level} instance was created.
     */
    private final int numGemsInit;

    /**
     * Data structure that allows us to undo moves and manage its information.
     */
    private final UndoStack undoStack;

    /**
     * Constructor
     *
     * @param fileName Name of the file that contains level's data.
     * @throws LevelException When there is any error while parsing the file.
     */
    public Level(String fileName) throws LevelException{
        size = parse(fileName);

        numGemsInit = (int)
                Arrays.stream(getBoard()).flatMap(Arrays::stream)
                        .filter(cell -> cell.getElement() == Element.GEM)
                        .count();

        //Uncomment when you create UndoStack class.
        undoStack = new UndoStack();
    }

    /**
     * Parses/Reads level's data from the given file.<br/>
     * It also checks which the board's requirements are met.
     *
     * @param fileName Name of the file that contains level's data.
     * @return The size of the board in one direction (i.e. row or column). The board is {@code size x size}.
     * @throws LevelException When there is any error while parsing the file
     * or some board's requirement is not satisfied.     *
     */
    private int parse(String fileName) throws LevelException{
        String line;
        int size = 0;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream(fileName));

        try(InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader)){

            line = getFirstNonEmptyLine(reader);

            if (line != null) {
                setNumLives(Integer.parseInt(line));
            }

            line = getFirstNonEmptyLine(reader);

            if (line  != null) {
                size = Integer.parseInt(line);
                if(size < MIN_SIZE){
                    throw new LevelException(LevelException.SIZE_ERROR);
                }
            }

            line = getFirstNonEmptyLine(reader);

            if (line != null) {
                setDifficulty(LevelDifficulty.valueOf(line));
            }

            board = new Cell[size][size];

            for (int row = 0; row < size; row++) {
                char[] rowChar = Objects.requireNonNull(getFirstNonEmptyLine(reader)).toCharArray();
                for (int column = 0; column < size; column++) {
                    board[row][column] = CellFactory.getCellInstance(row, column,rowChar[column]);
                }
            }

            //Checks if there are more than one finish cell
            if(Stream.of(board).flatMap(Arrays::stream).filter(x -> x.getElement() == Element.PLAYER).count()!=1){
                throw new LevelException(LevelException.PLAYER_LEVEL_FILE_ERROR);
            }

            //Checks if there are one gem at least.
            if(Stream.of(board).flatMap(Arrays::stream).filter(x -> x.getElement() == Element.GEM).count()<1){
                throw new LevelException(LevelException.MIN_GEMS_ERROR);
            }

        }catch (IllegalArgumentException | IOException | PositionException e){
            throw new LevelException(LevelException.PARSING_LEVEL_FILE_ERROR);
        }

        return size;
    }

    /**
     * This is a helper method for {@link #parse(String fileName)} which returns
     * the first non-empty and non-comment line from the reader.
     *
     * @param br BufferedReader object to read from.
     * @return First line that is a parsable line, or {@code null} there are no lines to read.
     * @throws IOException if the reader fails to read a line.
     */
    private String getFirstNonEmptyLine(final BufferedReader br) throws IOException {
        do {

            String s = br.readLine();

            if (s == null) {
                return null;
            }
            if (s.isBlank() || s.startsWith("/")) {
                continue;
            }

            return s;
        } while (true);
    }

    public int getSize(){
        return this.size;
    }

    public LevelDifficulty getDifficulty(){
        return this.difficulty;
    }

    private void setDifficulty(LevelDifficulty difficulty){
        this.difficulty = difficulty;
    }

    private boolean hasUnlimitedLives(){
        return this.getNumLives() == UNLIMITED_LIVES;
    }

    public int getNumLives(){
        if (this.numLives==UNLIMITED_LIVES){
            return 2147483647;
        }else{
            return this.numLives;
        }
    }

    private void setNumLives(int numLives){
        if (numLives<=0){
            this.numLives = UNLIMITED_LIVES;
        }else {
            this.numLives = numLives;
        }
    }

    public void increaseNumLives(int num) throws LevelException{
        if (num<0){
            throw new LevelException(LevelException.INCREASE_NUM_LIVES_ERROR);
        }else if (this.getNumLives()==UNLIMITED_LIVES){
            setNumLives(UNLIMITED_LIVES);
        }else{
            setNumLives(this.getNumLives()+num);
        }
    }

    public void decreaseNumLives(){
        if (!(this.getNumLives()==UNLIMITED_LIVES || this.getNumLives()==0)){
            this.numLives = this.numLives-1;
        }
    }

    public void increaseNumGemsGot(int numGemsGot) throws LevelException{
        if (numGemsGot<0){
            throw new LevelException(LevelException.INCREASE_NUM_GEMS_GOT_ERROR);
        }else{
            this.numGemsGot += numGemsGot;
        }
    }

    public void decreaseNumGemsGot(){
        if (this.getNumGemsGot()>0){
            this.numGemsGot -= 1;
        }else{
            this.numGemsGot = 0;
        }
    }

    public int getNumGemsGot(){
        return this.numGemsGot;
    }

    public int getNumMoves(){
        return this.numMoves;
    }

    public int getNumGemsInit(){
        return this.numGemsInit;
    }

    public void increaseNumMoves(){
        this.numMoves += 1;
    }

    public boolean hasWon(){
        return getNumGemsInit()==getNumGemsGot();
    }

    public boolean hasLost(){
        return getNumLives()==0;
    }

    private Cell[][] getBoard(){
        return this.board;
    }

    public Cell getCell(int row, int column) throws LevelException{
        if (row>getSize()-1 || column>getSize()-1) {
            throw new LevelException(LevelException.INCORRECT_CELL_POSITION);
        }else if (row<0 || column<0){
            throw new LevelException(LevelException.INCORRECT_CELL_POSITION);
        }else{
            return this.getBoard()[row][column];
        }
    }

    public Cell getCell(Position position) throws LevelException{
        if (position.getRow()>getSize()-1 || position.getColumn()>getSize()-1){
            throw new LevelException(LevelException.INCORRECT_CELL_POSITION);
        }else{
            return this.board[position.getRow()][position.getColumn()];
        }
    }



    public void setCell(Position position, Element element) throws LevelException{
        if (position.getColumn()>=size || position.getRow()>=size) {
            throw new LevelException(LevelException.INCORRECT_CELL_POSITION);
        }else{
            this.board[position.getRow()][position.getColumn()].setElement(element);
        }
    }

    public Position getPlayerPosition(){
        int i=0;
        while (i<getSize()){
            int j =0;
            while (j<getSize()){
                if (board[i][j].getElement()==Element.PLAYER || board[i][j].getElement()==Element.PLAYER_STOP){
                    return getBoard()[i][j].getPosition();
                }
                j += 1;
            }
            i += 1;
        }
        return null;
    }

    public int getScore(){
        return (getSize()*getSize()) + (10*getNumGemsGot()) - getNumMoves() - (2*undoStack.getNumPops());
    }

    public StackItem push(StackItem item){
        undoStack.push(item);
        return item;
    }



    public boolean undo() throws LevelException{
        List<Position> gemsList = undoStack.peek().collectedGems();
        List<Position> livesList = undoStack.peek().collectedLives();
        Position playerPos = getPlayerPosition();
        if (undoStack.isEmpty()){
            return false;
        }else{
            if (getCell(playerPos.getRow(),playerPos.getColumn()).getElement()==Element.PLAYER_STOP) {
                board[playerPos.getRow()][playerPos.getColumn()].setElement(Element.STOP);
            }else{
                board[playerPos.getRow()][playerPos.getColumn()].setElement(Element.EMPTY);
            }
            if (board[undoStack.peek().originPosition().getRow()][undoStack.peek().originPosition().getColumn()].getElement()==Element.STOP) {
                board[undoStack.peek().originPosition().getRow()][undoStack.peek().originPosition().getColumn()].setElement(Element.PLAYER_STOP);
            }else{
                board[undoStack.peek().originPosition().getRow()][undoStack.peek().originPosition().getColumn()].setElement(Element.PLAYER);
            }

            if (!gemsList.isEmpty()){
                int size = gemsList.size();
                setCell((undoStack.peek().collectedGems().get(size-1)), Element.GEM);
                decreaseNumGemsGot();
            }
            if (!livesList.isEmpty()){
                int size = gemsList.size();
                board[undoStack.peek().collectedLives().get(size-1).getRow()][undoStack.peek().collectedLives().get(size-1).getColumn()].setElement(Element.EXTRA_LIFE);
                decreaseNumLives();
            }
            undoStack.pop();

        }
        return true;
    }

    @Override
    public String toString(){
        int i=0;
        StringBuilder tauler= new StringBuilder();
        while (i<this.getSize()){
            int j = 0;
            while (j<this.getSize()){
                tauler.append(getBoard()[i][j]);
                j += 1;
            }
            i += 1;
            tauler.append(System.lineSeparator());
        }
        return String.valueOf(tauler);
    }


}
