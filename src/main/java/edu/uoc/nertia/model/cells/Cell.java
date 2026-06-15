package edu.uoc.nertia.model.cells;

import edu.uoc.nertia.model.utils.Position;

public class Cell {
    //Attributes
    private Element element;
    private Position position;

    //Methods
    public Cell(Position position, Element element){
        setPosition(position);
        setElement(element);
    }

    public final Element getElement(){
        return element;
    }

    public final Position getPosition(){
        return position;
    }

    private void setPosition(Position position){
        this.position = position;
    }

    public void setElement(Element element){
        this.element = element;
    }

    @Override
    public String toString(){
        if (this.getElement()==null){
            return "";
        }else{
            return String.valueOf(this.getElement());
        }
    }
}
