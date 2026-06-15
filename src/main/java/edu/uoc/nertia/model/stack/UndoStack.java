package edu.uoc.nertia.model.stack;

import java.util.Stack;

public class UndoStack extends Stack<StackItem> {
    //Attributes
    private int numPops=0;

    //Methods
    public UndoStack(){
    }

    @Override
    public StackItem pop(){
        StackItem a;
        if (!this.isEmpty()){
            a= this.peek();
            super.pop();
            incrementNumPops();
        }else {
            return null;
        }
        return a;
    }

    public int getNumPops(){
        return numPops;
    }

    private void incrementNumPops(){
        numPops += 1;
    }

}
