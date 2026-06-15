package edu.uoc.nertia.model.cells;

public enum Element {
    EMPTY('-',"empty.png"), EXTRA_LIFE('L', "life.png"), GEM('*', "gem.png"), MINE('X', "mine.png"), PLAYER('@', "player.png"), STOP('S', "stop.png"), PLAYER_STOP('$', "player_stop.png"), WALL('#', "wall.png");
    private char symbol;
    private String imageSrc;

    Element(char symbol, String imageSrc){
        setSymbol(symbol);
        setImageSrc(imageSrc);
    }

    private void setSymbol(char symbol){
        this.symbol = symbol;
    }

    private void setImageSrc(String imageSrc){
        this.imageSrc = imageSrc;
    }

    public static Element symbol2Element (char symbol){
        for (var e: Element.values()){
            if (e.getSymbol()==symbol){
                return e;
            }
        }
        return null;
    }

    public char getSymbol(){
        return this.symbol;
    }

    public String getImageSrc(){
        return this.imageSrc;
    }

    @Override
    public String toString(){
        return String.valueOf(this.getSymbol());
    }


}
