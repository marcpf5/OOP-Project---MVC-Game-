package edu.uoc.nertia.model.stack;

import edu.uoc.nertia.model.cells.Element;
import edu.uoc.nertia.model.utils.Position;

import java.util.List;

import static java.util.Objects.hash;

public record StackItem(Position originPosition, Element originElement, List<Position> collectedLives, List<Position> collectedGems) {

    public String toString(){
        return "StackItem - Origin Position: " + originPosition + ", Origin Element: "+ originElement + ", Collected Lives: "+ collectedLives+", Collected Gems: "+ collectedGems;
    }

    public int hashCode(){
        return hash(originPosition, originElement, collectedLives, collectedGems);
    }

    public boolean equals(Object o){
        return (this.getClass() == o.getClass()) && (this.originPosition() == ((StackItem) o).originPosition()) && (this.originElement() == ((StackItem) o).originElement()) && (this.collectedLives() == ((StackItem) o).collectedLives()) && (this.collectedGems() == ((StackItem) o).collectedGems());
    }
}
