package com.mathdoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Cage {

    private ArrayList<Cell> cells;
    private String label;
    private boolean isCorrect;
    private char operation;
    private int cageAnswer;
    private ArrayList<Cage> correctCage;

    public Cage(String label) throws Exception {
        this.label = label;
        isCorrect = true;

        cells = new ArrayList<>();
        correctCage = new ArrayList<>();

        operation = this.getOperation();
        cageAnswer = this.getCageAnswer();
    }

    public Cage() {
        isCorrect = true;

        cells = new ArrayList<>();
        correctCage = new ArrayList<>();
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public void setOperation(char operation) {
        this.operation = operation;
    }

    public char getOperation() throws Exception {
        char operation;
        if (label.length() > 1) {
            operation = label.charAt(label.length() - 1);
        } else {
            operation = 'N';
        }

        checkValidity(operation);
        return operation;
    }

    public void checkValidity(char operation) throws Exception {
        switch (operation) {
            case '+':
            case '-':
            case '*':
            case 'x':
            case '/':
            case '÷':
            case 'N':
                break;
            default:
                throw new Exception("Invalid Operation used at a cage with label " + this.getLabel());
        }
    }

    public void setCageAnswer(int cageAnswer) {
        this.cageAnswer = cageAnswer;
    }

    public int getCageAnswer() {
        if (label.length() > 1) {
            return (Integer.parseInt(label.substring(0, label.length() - 1)));
        }
        return (Integer.parseInt(label));
    }

    public void addCell(Cell cell) throws Exception {
        if (cell.getRespondingCage() != null) {
            throw new Exception("Error, the cell with ID " + cell.getId() + " is already used in a cage with label "
                    + cell.getRespondingCage().getLabel());
        }
        cells.add(cell);
        cell.setRespondingCage(this);
    }

    public void checkNeighbours() throws Exception {
        switch (operation) {
            case 'N':
                if (cells.size() != 1) {
                    throw new Exception(
                            "No Operation can only happen if the size of the cage is 1 cell, for cage with label "
                                    + this.getLabel());
                }

        }
        for (Cell cell : cells) {
            cell.checkNeighbours();
        }
    }

    public boolean checkCageFilled() {
        for (Cell cell : cells) {
            if (cell.getValue() == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkCage(boolean autofill) {
        switch (operation) {
            case '+':
                isCorrect = this.checkAdditionCage(autofill);
                break;
            case '-':
                isCorrect = this.checkSubtractionCage(autofill);
                break;
            case '*':
            case 'x':
                isCorrect = this.checkMultiplicationCage(autofill);
                break;
            case '/':
            case '÷':
                isCorrect = this.checkDivisionCage(autofill);
                break;
            case 'N':
                isCorrect = (cells.get(0).getValue() == this.cageAnswer);
                break;
        }

        return (isCorrect);
    }

    public boolean checkAdditionCage(boolean autofill) {
        int sum = 0;
        for (Cell cell : cells) {

            if (cell.getValue() == 0) {
                return false;
            }

            sum += cell.getValue();
        }

        if (autofill) {
            return sum <= cageAnswer;
        }

        return (sum == cageAnswer);
    }

    public boolean checkSubtractionCage(boolean autofill) {

        ArrayList<Integer> values = new ArrayList<>();

        for (Cell cell : cells) {
            /*
             * if (cell.getValue() == 0){ return false; }
             */
            values.add(cell.getValue());
        }

        Collections.sort(values);
        Collections.reverse(values);

        int total = values.get(0);

        for (int i = 1; i < values.size(); i++) {
            total -= values.get(i);
        }

        if (autofill) {
            return total >= cageAnswer;
        }

        return total == cageAnswer;
    }

    public boolean checkMultiplicationCage(boolean autofill) {
        int total = 1;

        for (Cell cell : cells) {

            int value = cell.getValue();

            if (value == 0) {
                if (!autofill) {
                    return false;
                }
                value = 1;
            }

            total *= value;
        }

        if (autofill) {
            return total <= cageAnswer;
        }

        return (total == cageAnswer);
    }

    public boolean checkDivisionCage(boolean autofill) {
        double total;

        ArrayList<Integer> values = new ArrayList<>();

        for (Cell cell : cells) {

            int value = cell.getValue();

            if (value == 0) {
                if (!autofill) {
                    return false;
                }
                value = 1;
            }
            values.add(value);
        }

        Collections.sort(values);
        Collections.reverse(values);

        total = values.get(0);

        for (int i = 1; i < cells.size(); i++) {
            total = total / (double) (values.get(i));
        }

        int intTotal = 0;

        if ((total == Math.floor(total)) && !Double.isInfinite(total)) {
            intTotal = (int) total;
        }

        if (autofill) {
            return intTotal >= cageAnswer;
        }

        return intTotal == cageAnswer;
    }

    public Cell getFirstElement() {
        return (cells.get(0));
    }

    public void setLabel() {
        StringBuilder label = new StringBuilder();
        try {
            label.append(cageAnswer);
            if (operation != 'N') {
                label.append(operation);
            }
        } catch (Exception ignore) {
        }

        this.label = label.toString();
    }

    public String getLabel() {
        return label;
    }

    public Cell leftmostCell() {
        // Cell cell = cells.get(0);
        cells.sort(Comparator.comparingInt(Cell::getYCoordinate).thenComparing(Cell::getXCoordinate));
        return cells.get(0);
    }

    public boolean isCorrect() {
        return (isCorrect);
    }
}
