package edu.uoc.nertia.model.utils;

import edu.uoc.nertia.model.exceptions.PositionException;

import java.util.Objects;


public class Position {
    //Attributes
    private int row;
    private int column;

    //Methods
    public Position(int row, int column) throws PositionException {
        setRow(row);
        setColumn(column);
    }

    private void setColumn(int column) throws PositionException{
        if (column<0){
            throw new PositionException(PositionException.POSITION_COLUMN_ERROR);
        }else{
            this.column = column;
        }
    }

    private void setRow(int row) throws PositionException{
        if (row<0){
            throw new PositionException(PositionException.POSITION_ROW_ERROR);
        }else{
            this.row = row;
        }
    }

    public int getRow(){
        return this.row;
    }

    public int getColumn(){
        return this.column;
    }

    public Position offsetBy(int drow, int dcolumn) {
        int sumarow = this.getRow()+drow;
        int sumacol= this.getColumn()+dcolumn;
        if ((sumarow)>=0 && (sumacol)>=0){
            try {
                return new Position(sumarow, sumacol);
            } catch (PositionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Position offsetBy(int drow, int dcolumn, int size){
        int sumaRow = this.getRow()+drow;
        int sumaCol = this.getColumn()+dcolumn;
        if ((sumaRow) >= 0 && (sumaCol) >= 0 && sumaRow < size && sumaCol < size) {
            try {
                return new Position(sumaRow,sumaCol);
            } catch (PositionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o){
        if (o!= null) {
            if (o.getClass() == this.getClass()) {
                return ((Position) o).getRow() == this.getRow() && ((Position) o).getColumn() == this.getColumn();
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(row, column);
    }



}
