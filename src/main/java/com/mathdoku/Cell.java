package com.mathdoku;

import java.util.ArrayList;

public class Cell {

    private int xCoordinate;
    private int yCoordinate;

    private int id;

    private int value;

    private Cage respondingCage;

    private boolean[] neighbours;
    private boolean isHighlightedRed;

    private boolean isHinted;

    private ArrayList<Integer> undoValues;
    private ArrayList<Integer> redoValues;

    public Cell(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.value = 0;

        id = (xCoordinate) + 1 + (yCoordinate * GameGrid.gridSize);

        neighbours = new boolean[4];
        undoValues = new ArrayList<>();
        redoValues = new ArrayList<>();
        isHighlightedRed = false;
        isHinted = false;
    }

    public void setHighlightedRed(boolean isHighlighted) {
        this.isHighlightedRed = isHighlighted;
    }

    public boolean isHighlightedRed() {
        return isHighlightedRed;
    }

    public void clearRedoValues() {
        redoValues.clear();
    }

    public void clearUndoValues() {
        undoValues.clear();
    }

    public Cage getRespondingCage() {
        return respondingCage;
    }

    public boolean isHinted() {
        return isHinted;
    }

    public void setHinted(boolean isHinted) {
        this.isHinted = isHinted;
    }

    public void checkNeighbours() throws Exception {

        // System.out.println("Coordinate: " + Arrays.toString(coordinate));
        boolean top = false;
        boolean bot = false;
        boolean left = false;
        boolean right = false;

        for (Cell neighbourCoordinate : respondingCage.getCells()) {
            // System.out.println("Neighbour: " + Arrays.toString(neighbourCoordinate));

            if (neighbourCoordinate.getXCoordinate() != xCoordinate || neighbourCoordinate.yCoordinate != yCoordinate) {
                if (xCoordinate == (neighbourCoordinate.getXCoordinate() + 1)
                        && (yCoordinate == neighbourCoordinate.getYCoordinate())) {
                    left = true;
                    // System.out.println("Neighbour on the left");
                } else if (xCoordinate == (neighbourCoordinate.getXCoordinate() - 1)
                        && (yCoordinate == neighbourCoordinate.getYCoordinate())) {
                    right = true;
                    // System.out.println("Neighbour on the right");
                } else if (yCoordinate == (neighbourCoordinate.getYCoordinate() + 1)
                        && (xCoordinate == neighbourCoordinate.getXCoordinate())) {
                    top = true;
                    // System.out.println("Neighbour on the top");
                } else if (yCoordinate == (neighbourCoordinate.getYCoordinate() - 1)
                        && (xCoordinate == neighbourCoordinate.getXCoordinate())) {
                    bot = true;
                    // System.out.println("Neighbour on the bottom");
                }
            }

        }

        neighbours[0] = left;
        neighbours[1] = top;
        neighbours[2] = right;
        neighbours[3] = bot;

        if (this.getRespondingCage().getCells().size() != 1) {
            if (neighbours[0] == neighbours[1] && neighbours[1] == neighbours[2] && neighbours[2] == neighbours[3]
                    && !neighbours[0]) {
                throw new Exception("One of the cells in the cage with label " + this.getRespondingCage().getLabel()
                        + " is not adjacent to others");
            }
        }

    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    // Instabilities possible due to && value != 0
    public boolean setValue(int value) {
        if (value <= GameGrid.gridSize && ((undoValues.size() == 0 && value != 0)
                || (undoValues.size() != 0 && value != undoValues.get(undoValues.size() - 1)))) {

            undoValues.add(value);
            this.value = value;

            isHinted = false;

            return true;

        }
        return false;
        // this.clearRedoValues();
    }

    public int getUndoValue() {
        if (undoValues.size() - 1 != 0) {
            redoValues.add(undoValues.remove(undoValues.size() - 1));
            value = undoValues.get(undoValues.size() - 1);
            isHinted = false;
            return this.getValue();
        } else {
            redoValues.add(undoValues.remove(undoValues.size() - 1));
        }
        isHinted = false;
        value = 0;
        return 0;
    }

    public void clearCell() {
        value = 0;
        undoValues.clear();
        isHinted = false;
    }

    public int getRedoValue() {
        if (redoValues.size() != 0) {
            int value = redoValues.remove(redoValues.size() - 1);
            undoValues.add(value);
            this.value = value;
            return this.getValue();
        }
        return -1;
    }

    public boolean[] getNeighbours() {
        return (neighbours);
    }

    public void setRespondingCage(Cage cage) {
        respondingCage = cage;
    }
}
