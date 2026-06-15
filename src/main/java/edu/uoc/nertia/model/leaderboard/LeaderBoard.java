package edu.uoc.nertia.model.leaderboard;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author David García Solórzano
 * @version 1.0
 */
public class LeaderBoard {

    /**
     * Maximum scores that will be stored.
     */
    private final int maxScores;

    /**
     * List with the top highest scores.
     */

    private List<Score> scores;

    /**
     * Name of the file where the data will be stored.
     */
    private static final String FILE_LEADERBOARD = "leaderboard.ser";


    private List<Score> getScores(){
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(FILE_LEADERBOARD))){
            return (List<Score>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>(this.getMaxScores());
        }
    }

    public void add(String name, int points){

        //TODO
        /*
        Before the block with the tag "DONE", you must write your code so that:
        * Checks if the the points that are passed as a parameter can be included in the leaderboard.
        * Add the new score (i.e. player's name and points) in the scores list.
        * The scores list must be sorted in descendant order, i.e. being the first score the one that has the highest points.
         */
        Score a = new Score(name, points);
        if (this.isInTheTop(points)){
            scores.add(a);
        }
        Collections.sort(scores);

        //DONE: write to the file. DON'T MODIFY
        try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(FILE_LEADERBOARD,false))){
            output.writeObject(scores);
        } catch (NotSerializableException e) {
            System.out.println("||1|"+e.getMessage());
        } catch (InvalidClassException  e) {
            System.out.println("||2|"+e.getMessage());
        } catch (IOException  e) {
            System.out.println("||3|"+e.getMessage());
        }
    }

    /**
     * Returns if the given points deserve to be in the top list.
     * @param points Score to compare
     * @return {@code true} if the given points deserve to be in the leaderboard. Otherwise, {@code false}.
     */
    public boolean isInTheTop(int points){
        //TODO
        int i=0;
        if (getScores().size()<getMaxScores()){
            return true;
        }else {
            while (i<getScores().size()) {
                if (points > getScores().get(i).points()) {
                    return true;
                }else{
                    i = i+1;
                }
            }
        }
        return false;
    }

    /** Constructor for LeaderBoard
     * @param maxScores Number of scores that must be stored. If {@code maxsScores}
     *                  is negative or zero, then the default value is 5.
     */
    public LeaderBoard(int maxScores) {
        //TODO
        if (maxScores<=0){
            this.maxScores = 5;
        }else{
            this.maxScores = maxScores;
        }
        scores = new ArrayList<>();
    }

    /**
     * Returns the value of the attribute {@code MAX_SCORES}.
     * @return The value of {@code MAX_SCORES}.
     */
    public int getMaxScores(){
        //TODO
        return maxScores;
    }

    /** Returns the top five high scores, in order from best to worst
     * @return String with the top five high scores.
     */
    @Override
    public String toString() {
        //TODO
        /* Example of String:
        1) DAVID 100 pts
        2) MARINA 90 pts
        3) Pau 85 pts
        4) Elena 80 pts
         */
        StringBuilder a = new StringBuilder();
        int i=0;
        if (getScores().size()>4) {
            while (i < 5) {
                a.append(i + 1).append(") ").append(scores.get(i).name().toUpperCase()).append(" : ").append(scores.get(i).points()).append(" pts").append(System.lineSeparator());
                i = i + 1;
            }
        }else{
            while (i<scores.size()){
                a.append(i+1).append(") ").append(scores.get(i).name().toUpperCase()).append(" : ").append(scores.get(i).points()).append(" pts").append(System.lineSeparator());
                i = i+1;
            }
        }

        return a.toString();
    }

}
