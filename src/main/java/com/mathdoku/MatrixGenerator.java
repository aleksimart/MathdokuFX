package com.mathdoku;

import java.util.*;

public class MatrixGenerator {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    };

    private Difficulty difficulty;
    private int gridsize;
    private GameGrid gameGrid;
    private Random random;
    private int maxIndex;
    private Cell startCell;
    private ArrayList<Cell> unusedCells;

    private ArrayList<Cell> cells;

    private ArrayList<Integer> cageSizes;

    private int solutionNumber;

    public MatrixGenerator(Difficulty difficulty, int gridsize) {

        this.solutionNumber = 0;
        this.difficulty = difficulty;
        this.gridsize = gridsize;
        this.gameGrid = new GameGrid(gridsize);
        cageSizes = new ArrayList<>();

        random = new Random();
        cells = new ArrayList<>();
        unusedCells = new ArrayList<>();
        cloneCells();
        this.startCell = startPoint();

        maxIndex = gridsize * gridsize - 1;

        generateAnswersU();

        try {
            randomCages();
            gameGrid.storeAnswers(false);
            checkUniqueness();
        } catch (Exception ignore) {
        }
    }

    public void checkUniqueness() {
        solutionNumber = 0;
        solutionNumber(0);
        if (solutionNumber > 1) {
            gameGrid.getCages().clear();
            for (Cell cell : cells) {
                cell.setRespondingCage(null);
            }

            for (int i = 0; i < cells.size(); i++) {
                cells.get(i).setValue(gameGrid.getAnswers().get(i));
            }

            unusedCells.addAll(gameGrid.getCells());

            try {
                randomCages();
                checkUniqueness();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    // Testing Purposes only
    public MatrixGenerator() {
        this.solutionNumber = 0;
        this.difficulty = Difficulty.EASY;
        this.gridsize = 2;
        this.gameGrid = new GameGrid(gridsize);
        cageSizes = new ArrayList<>();

        random = new Random();
        cells = new ArrayList<>();
        unusedCells = new ArrayList<>();
    }

    public GameGrid getGameGrid() {
        return gameGrid;
    }

    /*
     * public static void main(String[] args){
     * 
     * // Test if the uniqueness check works, should output 2
     * 
     * MatrixGenerator a = new MatrixGenerator(); GameGrid test = new GameGrid(2);
     * try { Cage cage1 = new Cage("2*"); cage1.addCell(GameGrid.findCell(1));
     * GameGrid.findCell(1).setValue(1); cage1.addCell(GameGrid.findCell(2));
     * GameGrid.findCell(2).setValue(2); cage1.checkNeighbours(); Cage cage2 = new
     * Cage("2*"); cage2.addCell(GameGrid.findCell(3));
     * GameGrid.findCell(3).setValue(2); cage2.addCell(GameGrid.findCell(4));
     * GameGrid.findCell(4).setValue(1); cage2.checkNeighbours();
     * test.addCage(cage1); test.addCage(cage2); a.gameGrid = test; a.cloneCells();
     * //a.solutionNumber(0); //System.out.println(a.solutionNumber);
     * a.gameGrid.storeAnswers(false); a.checkUniqueness(); } catch (Exception e) {
     * e.printStackTrace(); }
     * 
     * }
     * 
     */

    public void cloneCells() {
        cells = gameGrid.getCells();
        unusedCells.addAll(gameGrid.getCells());
    }

    public Cell startPoint() {
        int randomIndex;
        randomIndex = random.nextInt(cells.size());
        cells.get(randomIndex).setValue(random.nextInt(GameGrid.gridSize) + 1);
        return cells.get(randomIndex);
    }

    public boolean generateAnswersU() {
        Cell emptyCell = nextEmpty();

        if (emptyCell != null) {

            for (int i = 1; i <= GameGrid.gridSize; i++) {
                if (isValid(emptyCell, i)) {
                    emptyCell.setValue(i);
                    if (generateAnswersU()) {
                        return true;
                    }
                    emptyCell.clearCell();
                }
            }
            return false;
        }
        return true;
    }

    public Cell nextEmpty() {

        for (int i = startCell.getId() - 1; i < cells.size() + startCell.getId() - 1; i++) {
            int j = i;

            if (i >= cells.size()) {
                j = i % cells.size();
            }

            if (cells.get(j).getValue() == 0) {
                return cells.get(j);
            }

        }

        return null;
    }

    public boolean isValid(Cell cell, int value) {
        cell.setValue(value);
        if (gameGrid.checkColumn(cell) && gameGrid.checkRow(cell)) {
            cell.clearCell();
            return true;
        } else {
            cell.clearCell();
            return false;
        }
    }

    // For now, easy and 6 x 6
    public void numOfCages() {
        int cellNum = gridsize * gridsize;
        int cellSize;
        switch (difficulty) {
            case EASY:
                if (gridsize == 6) {

                    while (cellNum != 0) {
                        cellSize = random.nextInt(3) + 1;

                        if (cellSize >= cellNum) {
                            cellSize = cellNum;
                            cellNum = 0;
                        }

                        cageSizes.add(cellSize);
                    }
                }
                break;
            case MEDIUM:
            case HARD:
        }
    }

    public Cell getRandomUnusedCell() {

        if (unusedCells.size() != 0) {
            int index = random.nextInt(unusedCells.size());
            return unusedCells.remove(index);
        }

        return null;
    }

    public void randomCages() throws Exception {
        while (unusedCells.size() != 0) {
            Cage cage = generateRandomCage();
            generateCageOperation(cage);
            cage.setLabel();
            cage.checkNeighbours();
            gameGrid.addCage(cage);
        }
    }

    public Cage generateRandomCage() throws Exception {

        int cageSize = random.nextInt(4) + 1;
        Cell startCell = getRandomUnusedCell();
        Cage cage = new Cage();

        cage.addCell(startCell);

        ArrayList<Cell> potentialNeighbours = null;
        if (cageSize > 1) {
            potentialNeighbours = potentialNeighbours(startCell);
        }

        while (cage.getCells().size() != cageSize && potentialNeighbours.size() != 0) {
            try {
                Cell cell = potentialNeighbours.remove(random.nextInt(potentialNeighbours.size()));
                potentialNeighbours.addAll(potentialNeighbours(cell));
                cage.addCell(cell);
                unusedCells.remove(cell);
            } catch (Exception ignore) {
            }
        }

        return cage;
    }

    public ArrayList<Cell> potentialNeighbours(Cell initialCell) {
        ArrayList<Cell> potentialNeighbours = new ArrayList<>();

        int initialCellId = initialCell.getId();
        int leftId = initialCellId - 1;
        int rightId = initialCellId + 1;
        int topId = initialCellId - GameGrid.gridSize;
        int botId = initialCellId + GameGrid.gridSize;

        if (rightId <= GameGrid.gridSize * GameGrid.gridSize && rightId % GameGrid.gridSize != 1
                && GameGrid.findCell(rightId).getRespondingCage() == null) {
            potentialNeighbours.add(GameGrid.findCell(rightId));
        }

        if (leftId > 0 && leftId % GameGrid.gridSize != 0 && GameGrid.findCell(leftId).getRespondingCage() == null) {
            potentialNeighbours.add(GameGrid.findCell(leftId));
        }

        if (botId <= GameGrid.gridSize * GameGrid.gridSize && GameGrid.findCell(botId).getRespondingCage() == null) {
            potentialNeighbours.add(GameGrid.findCell(botId));
        }

        if (topId > 0 && GameGrid.findCell(topId).getRespondingCage() == null) {
            potentialNeighbours.add(GameGrid.findCell(topId));
        }

        return potentialNeighbours;
    }

    public void generateCageOperation(Cage cage) {
        if (cage.getCells().size() == 1) {
            cage.setOperation('N');
            cage.setCageAnswer(cage.getCells().get(0).getValue());
        } else {
            double prob = random.nextDouble();

            if (prob <= 0.15) {
                cage.setOperation('+');
                additionCalculation(cage);
            } else if (prob > 0.15 && prob <= 0.5) {
                cage.setOperation('-');
                if (!subtractionCalculation(cage)) {
                    generateCageOperation(cage);
                }
            } else if (prob > 0.5 && prob <= 0.65) {
                cage.setOperation('*');
                multiplicationCalculation(cage);
            } else {
                cage.setOperation('/');
                if (!divisionCalculation(cage)) {
                    generateCageOperation(cage);
                }
            }
        }
    }

    public void additionCalculation(Cage cage) {
        int sum = 0;

        for (Cell cell : cage.getCells()) {
            sum += cell.getValue();
        }

        cage.setCageAnswer(sum);
    }

    public boolean subtractionCalculation(Cage cage) {

        ArrayList<Cell> cells = cage.getCells();
        cells.sort(Comparator.comparingInt(Cell::getValue).reversed());

        int total = cells.get(0).getValue();
        for (int i = 1; i < cells.size(); i++) {
            total -= cells.get(i).getValue();

            if (total < 0) {
                return false;
            }
        }

        cage.setCageAnswer(total);
        return true;
    }

    public void multiplicationCalculation(Cage cage) {
        int total = 1;

        for (Cell cell : cage.getCells()) {
            total *= cell.getValue();
        }

        cage.setCageAnswer(total);
    }

    public boolean divisionCalculation(Cage cage) {

        ArrayList<Cell> cells = cage.getCells();
        cells.sort(Comparator.comparingInt(Cell::getValue).reversed());

        double total = cells.get(0).getValue(); // TODO issue here somewhere

        for (int i = 1; i < cells.size(); i++) {
            total = total / (double) cells.get(i).getValue();
        }

        int intTotal;
        if ((total == Math.floor(total)) && !Double.isInfinite(total)) {
            intTotal = (int) total;
            cage.setCageAnswer(intTotal);
            return true;
        }

        return false;
    }

    public void solutionNumber(int n) {
        int N = GameGrid.gridSize;
        if (n == N * N)
            solutionNumber++;
        else {
            for (int i = 1; i <= N; i++) {
                if (gameGrid.isValid(cells.get(n), i)) {
                    cells.get(n).setValue(i);
                    solutionNumber(n + 1);
                    cells.get(n).clearCell();
                }
            }
        }
    }
}
