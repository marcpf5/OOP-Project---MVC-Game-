package edu.uoc.nertia.model.leaderboard;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

public record Score(String name, int points) implements Serializable, Comparable<Score>{

    @Serial
    private static final long serialVersionUID = 13L;

    @Override
    public String toString(){
        return name.toUpperCase(Locale.ROOT) + " : "+ points + " pts";
    }

    @Override
    public int compareTo(Score o){
        return Integer.compare(o.points, this.points);
    }



}
