package com.mathdoku;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

class GameGrid {

    // Size of the greed (e.g: 3x3 or 6x6), can only be a square grid
    static int gridSize;

    // Pane to attach the canvas to
    private final Pane canvasPane;

    private final Canvas mathDoku;
    private final GraphicsContext gc;

    // All cells and cages belonging to particular board
    private ArrayList<Cage> cages;
    private static ArrayList<Cell> cells;

    // Stores the reference to the last cell clicked (moved to) by the user
    private Cell lastCellPressed;

    private ArrayList<Cell> undoCellPressed;
    private ArrayList<Cell> redoCellPressed;

    private ArrayList<Integer> cellAnswers;

    // Sizes of the single cell
    private double cellWidth;
    private double cellHeight;

    // Enables/Disables showing mistakes
    private boolean showMistakes;

    // Stores whenever the puzzle has been solved or not
    private boolean correct;

    // private int sumOfCells;

    private Color fontColor;
    private double fontSizeInput;
    private double fontSizeLabel;

    /**
     * Constructor of the class Sets the size for the grid Creates a pane, canvas,
     * and the graphicsContext of canvas Declares arrayLists for list of undo cells
     * and redo cells and for lists of cages and cells Sets the initial value of
     * showMistakes to false
     *
     * Calls a few methods to set up cells, add sample data and setup resizable
     * canvas
     *
     */
    GameGrid(int gridSize) {

        GameGrid.gridSize = gridSize;
        /*
         * this.sumOfCells = this.getSumOfCells();
         */
        canvasPane = new Pane();
        mathDoku = new Canvas();
        gc = mathDoku.getGraphicsContext2D();

        cells = new ArrayList<>();
        cages = new ArrayList<>();

        undoCellPressed = new ArrayList<>();
        redoCellPressed = new ArrayList<>();

        cellAnswers = new ArrayList<>();

        fontColor = Color.WHITE;

        showMistakes = false;

        this.setUpCells();
        this.setupResizableCanvas();
    }

    void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        draw();
    }

    Color getFontColor() {
        return fontColor;
    }

    void setFontSize(double fontSize) {
        this.fontSizeInput = fontSize * cellWidth / 90;
        this.fontSizeLabel = (fontSize - 5) * cellWidth / 90;
        draw();
    }

    double getFontSize() {
        return fontSizeInput * 90 / cellWidth;
    }

    /**
     * Add an event handler to store the coordinates of the cell clicked on Add an
     * event handler to handle the keys pressed Ensure that the canvas is resizable
     */
    private void setupResizableCanvas() {

        // Handler to get the cell chosen by the user
        mathDoku.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            // Coordinates of last cell pressed
            int lastCellPressedX = (int) (event.getX() / (canvasPane.getWidth() / gridSize));
            int lastCellPressedY = (int) (event.getY() / (canvasPane.getHeight() / gridSize));

            lastCellPressed = findCell(lastCellPressedX, lastCellPressedY);

            // Redraw the canvas with appropriate highlighted cell
            draw();
        });

        mathDoku.addEventFilter(MouseEvent.ANY, event -> mathDoku.requestFocus());
        mathDoku.setFocusTraversable(true);

        /*
         * digit 1-9: set the value and add the cell chosen to the undo cells, also
         * clear the redo cells ESC: undo any cell that is chosen Backspace: clear the
         * value of the cell, add the cell chosen to the undo cells and clear the redo
         * cells wasd/arrows: movement along the grid Additionally, if any button will
         * be clicked during the time when the mistakes are shown, it will be undone
         */
        mathDoku.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            Main.ShowMistakesEnabler.disableShowMistakesEnabled();
            setShowMistakes(false);

            if (lastCellPressed != null) {
                try {
                    this.inputCellValue(Integer.parseInt(event.getText()));
                } catch (Exception e) {
                    switch (event.getCode()) {
                        case ENTER:
                            Main.ShowMistakesEnabler.toggleShowMistakesEnabled();
                            setShowMistakes(true);
                            break;
                        case ESCAPE:
                            lastCellPressed = null;
                            break;
                        case BACK_SPACE:
                            this.inputCellValue(0);
                            break;
                        case DOWN:
                        case S:
                            if (lastCellPressed.getYCoordinate() % (gridSize - 1) != 0
                                    || lastCellPressed.getYCoordinate() == 0) {
                                lastCellPressed = findCell(lastCellPressed.getXCoordinate(),
                                        lastCellPressed.getYCoordinate() + 1);
                            } else {
                                lastCellPressed = findCell(lastCellPressed.getXCoordinate(), 0);
                            }
                            break;
                        case UP:
                        case W:
                            if (lastCellPressed.getYCoordinate() == 0) {
                                lastCellPressed = findCell(lastCellPressed.getXCoordinate(), gridSize - 1);
                            } else {
                                lastCellPressed = findCell(lastCellPressed.getXCoordinate(),
                                        lastCellPressed.getYCoordinate() - 1);
                            }
                            break;
                        case LEFT:
                        case A:
                            if (lastCellPressed.getXCoordinate() == 0) {
                                lastCellPressed = findCell(gridSize - 1, lastCellPressed.getYCoordinate());
                            } else {
                                lastCellPressed = findCell(lastCellPressed.getXCoordinate() - 1,
                                        lastCellPressed.getYCoordinate());
                            }
                            break;
                        case RIGHT:
                        case D:
                            if (lastCellPressed.getXCoordinate() % (gridSize - 1) != 0
                                    || lastCellPressed.getXCoordinate() == 0) {
                                lastCellPressed = findCell(lastCellPressed.getXCoordinate() + 1,
                                        lastCellPressed.getYCoordinate());
                            } else {
                                lastCellPressed = findCell(0, lastCellPressed.getYCoordinate());
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else {
                switch (event.getCode()) {
                    case ENTER:
                        Main.ShowMistakesEnabler.toggleShowMistakesEnabled();
                        setShowMistakes(true);
                        break;
                    case DOWN:
                    case S:
                    case UP:
                    case W:
                    case LEFT:
                    case A:
                    case RIGHT:
                    case D:
                        lastCellPressed = findCell(0, 0);
                }
            }

            draw();
            event.consume();
        });

        // Add the canvas to the Pane
        canvasPane.getChildren().add(mathDoku);

        /*
         * Bind the width and height of the canvas to the pane Makes it resizable
         */
        mathDoku.widthProperty().bind(canvasPane.widthProperty());
        mathDoku.heightProperty().bind(canvasPane.heightProperty());

        /*
         * Listeners to redraw the canvas when it is resized
         */
        mathDoku.widthProperty().addListener(observable -> {
            for (Cell cell : cells) {
                if (cell.isHighlightedRed()) {
                    cell.setHighlightedRed(false);
                }
            }
            draw();
        });
        mathDoku.heightProperty().addListener(observable -> {
            for (Cell cell : cells) {
                if (cell.isHighlightedRed()) {
                    cell.setHighlightedRed(false);
                }
            }
            draw();
        });

        // Design
        canvasPane.setId("canvas");
        canvasPane.getStylesheets().add("CanvasDesign.css");
    }

    private static Cell findCell(int xCoordinate, int yCoordinate) {
        return (cells.get(xCoordinate + gridSize * yCoordinate));
    }

    // Combined enter number and this method into one
    void inputCellValue(int value) {

        Main.ShowMistakesEnabler.disableShowMistakesEnabled();
        setShowMistakes(false);

        if (lastCellPressed != null && lastCellPressed.setValue(value)) {
            this.addUndoCellPressed(lastCellPressed);
            this.clearRedoCells();
        }

        draw();
    }

    /**
     * @param cell cell that where the value was previously entered
     */
    private void addUndoCellPressed(Cell cell) {
        undoCellPressed.add(cell);
    }

    /**
     * Method to revert to the previous cell where the value was entered and revert
     * to its previous value Also store the current cell in the redo Cells in order
     * to redo the changes
     */
    void undoCellPressed() {

        // If there are any undoCells
        if (undoCellPressed.size() != 0) {
            Cell cellPressed = undoCellPressed.remove(undoCellPressed.size() - 1);

            redoCellPressed.add(cellPressed);

            lastCellPressed = cellPressed;
            lastCellPressed.getUndoValue();

            this.draw();
        }
    }

    boolean undoIsEmpty() {
        return undoCellPressed.isEmpty();
    }

    boolean redoIsEmpty() {
        return redoCellPressed.isEmpty();
    }

    /**
     * Method to redo the revert of the previous cell with the value that was
     * originally undone Also store the redone cell back to undone
     */
    void redoCellPressed() {

        // If there are any redoCells
        if (redoCellPressed.size() != 0) {
            lastCellPressed = redoCellPressed.remove(redoCellPressed.size() - 1);
            undoCellPressed.add(lastCellPressed);
            lastCellPressed.getRedoValue();
            this.draw();
        }
    }

    /**
     * Clears out the lists containing the positions of undone cells to be redone as
     * well as the redo values of all cells Used when clearing the whole board or
     * when the new value is entered
     */
    void clearRedoCells() {

        for (Cell cell : cells) {
            cell.clearRedoValues();
        }
        redoCellPressed.clear();
    }

    /**
     * Clears out the lists containing the last position of the cell selected as
     * well as all undo values of all cells Used when clearing the whole board
     */
    void clearUndoCells() {

        for (Cell cell : cells) {
            cell.clearUndoValues();
        }
        undoCellPressed.clear();
    }

    /**
     * @return arrayList of cells
     */
    ArrayList<Cell> getCells() {
        return cells;
    }

    /**
     * Create the appropriate number of cells based on the grid size
     */
    private void setUpCells() {
        cells.clear();

        for (int i = 0; i < gridSize; i++) {

            for (int j = 0; j < gridSize; j++) {
                Cell cell = new Cell(j, i);
                cells.add(cell);
            }
        }
    }

    /**
     * Sets the value of the showMistakes boolean to the value given If the value of
     * the boolean is false, then all the cells highlighted value is set to false
     * 
     * @param showMistakes
     */
    void setShowMistakes(Boolean showMistakes) {

        this.showMistakes = showMistakes;

        if (!showMistakes) {
            for (Cell cell : cells) {
                cell.setHighlightedRed(false);
            }
        }
    }

    boolean isCorrect() {
        return correct;
    }

    /**
     * @return the pane with the canvas on it
     */
    Pane getCanvasPane() {
        return canvasPane;
    }

    /**
     * Get the current width and height of the canvas Define the size of each cell
     * based on the height/width properties of the canvas Clear out fully the canvas
     * from any previous drawings Draw a square which is the edges of the grid Draw
     * the lines which separate the square into equal cells Highlight the chosen
     * cell (if any is chosen) Add inputs, set up the cages and, if needed,
     * highlight the cells with wrong answers
     */
    void draw() {

        // Find the current width and height of the canvas
        double width = mathDoku.getWidth();
        double height = mathDoku.getHeight();

        cellWidth = width / gridSize;
        cellHeight = height / gridSize;

        if (fontSizeInput == 0) {
            setFontSize(15);
        }

        // Clear the canvas of anything previously drawn
        gc.clearRect(0, 0, width, height);

        // Set the drawing colour to Black
        resetColor();
        gc.setLineWidth(3);

        // Draw the outer rectangle
        gc.strokeRect(0, 0, width, height);
        gc.setLineWidth(2);

        // Draw the lines inside to make the grid
        for (int i = 1; i < gridSize; i++) {
            gc.strokeLine(width * i / gridSize, 0, width * i / gridSize, height);
            gc.strokeLine(0, height * i / gridSize, width, height * i / gridSize);
        }

        this.addInputs();
        this.cageSetup();

        if (lastCellPressed != null) {
            this.highlightCell();
        }

        if (showMistakes) {
            this.checkAnswers(true);
        }
    }

    private void resetColor() {
        gc.setStroke(fontColor);
        gc.setFill(fontColor);
    }

    /**
     * Highlights the edges of cell that user has clicked/moved on
     */
    private void highlightCell() {

        if (lastCellPressed != null) {
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(6);
            gc.strokeRect(lastCellPressed.getXCoordinate() * cellWidth + 4,
                    lastCellPressed.getYCoordinate() * cellHeight + 4, cellWidth - 8, cellHeight - 8);
        }

        resetColor();
    }

    /**
     * Enters the values for each cell on the canvas (if there is any input)
     */
    private void addInputs() {

        for (Cell cell : cells) {

            if (cell.getValue() != 0) {

                gc.setFont(Font.font(fontSizeInput));
                // gc.setFont(Font.font(getFontSize()));

                double textX = cell.getXCoordinate() * cellWidth + (double) 33 / 70 * cellWidth;
                double textY = cell.getYCoordinate() * cellHeight + (double) 2 / 3 * cellHeight;

                gc.fillText(Integer.toString(cell.getValue()), textX, textY);
            }
        }
    }

    /**
     * Gets the cages, adds the label in the first cell of the cage and adds the
     * appropriate cage lines
     */
    private void cageSetup() {
        int labelX;
        int labelY;

        Iterator<Cage> cageIterator = cages.iterator();

        while (cageIterator.hasNext()) {

            Cage cage = cageIterator.next();
            Cell firstCell = cage.leftmostCell();

            labelX = firstCell.getXCoordinate();
            labelY = firstCell.getYCoordinate();

            gc.setLineWidth(1);
            gc.setFont(Font.font(fontSizeLabel));

            gc.fillText(cage.getLabel(), labelX * cellWidth + cellWidth / 9, labelY * cellHeight + cellHeight / 5);
            gc.setLineWidth(6);

            for (Cell cell : cage.getCells()) {

                boolean[] neighbours = cell.getNeighbours();
                if (!neighbours[1]) {
                    gc.strokeLine(cell.getXCoordinate() * cellWidth, cell.getYCoordinate() * cellHeight,
                            (cell.getXCoordinate() + 1) * cellWidth, cell.getYCoordinate() * cellHeight);
                }
                if (!neighbours[3]) {
                    gc.strokeLine(cell.getXCoordinate() * cellWidth, (cell.getYCoordinate() + 1) * cellHeight,
                            (cell.getXCoordinate() + 1) * cellWidth, (cell.getYCoordinate() + 1) * cellHeight);
                }
                if (!neighbours[0]) {
                    gc.strokeLine(cell.getXCoordinate() * cellWidth, cell.getYCoordinate() * cellHeight,
                            cell.getXCoordinate() * cellWidth, (cell.getYCoordinate() + 1) * cellHeight);
                }
                if (!neighbours[2]) {
                    gc.strokeLine((cell.getXCoordinate() + 1) * cellWidth, cell.getYCoordinate() * cellHeight,
                            (cell.getXCoordinate() + 1) * cellWidth, (cell.getYCoordinate() + 1) * cellHeight);
                }
            }
        }
    }

    static Cell findCell(int id) {
        for (Cell cell : cells) {
            if (cell.getId() == id) {
                return cell;
            }
        }
        return null;
    }

    /**
     * @param cage cage to add to the current board
     */
    public void addCage(Cage cage) {
        cages.add(cage);
    }

    public ArrayList<Cage> getCages() {
        return cages;
    }

    boolean checkAnswers(boolean highlight) {
        correct = true;
        gc.setFill(Color.rgb(255, 0, 0, 0.3));

        for (int level = 0; level < gridSize; level++) {
            this.checkRow(level, highlight);
            this.checkColumn(level, highlight);
        }

        for (Cage cage : cages) {
            this.checkCage(cage, highlight);
        }

        // System.out.println("Congrats you won");
        return correct;
        // resetColor();
    }

    private boolean checkRow(int level, boolean highlight) {

        ArrayList<Cell> cellRow = new ArrayList<>();
        Set<Integer> values = new HashSet<>();

        int size = gridSize;
        Cell cell;

        boolean hasZeroes = false;

        for (int i = 0; i < gridSize; i++) {

            cell = findCell(i, level);
            cellRow.add(cell);

            if (cell.getValue() == 0) {
                size--;
                hasZeroes = true;
            } else {
                values.add(cell.getValue());
            }
        }

        return CheckRowColumnValues(highlight, cellRow, values, size, hasZeroes);
    }

    public boolean checkRow(Cell cell) {
        int row = cell.getYCoordinate();
        int value = cell.getValue();

        for (int i = 0; i < GameGrid.gridSize; i++) {
            if (value == findCell(i, row).getValue() && findCell(i, row) != cell) {
                return false;
            }
        }

        return true;
    }

    public boolean checkColumn(Cell cell) {
        int column = cell.getXCoordinate();
        int value = cell.getValue();

        for (int i = 0; i < GameGrid.gridSize; i++) {
            if (value == findCell(column, i).getValue() && findCell(column, i) != cell) {
                return false;
            }
        }

        return true;
    }

    public boolean checkColumn(int level, boolean highlight) {
        ArrayList<Cell> cellColumn = new ArrayList<>();
        Set<Integer> values = new HashSet<>();
        int size = gridSize;
        Cell cell;

        boolean hasZeroes = false;

        for (int i = 0; i < gridSize; i++) {
            cell = findCell(level, i);
            cellColumn.add(cell);

            if (cell.getValue() == 0) {
                size--;
                hasZeroes = true;
            } else {
                values.add(cell.getValue());
            }
        }

        return CheckRowColumnValues(highlight, cellColumn, values, size, hasZeroes);
    }

    private boolean CheckRowColumnValues(boolean highlight, ArrayList<Cell> cellColumn, Set<Integer> values, int size,
            boolean hasZeroes) {
        if (values.size() != size) {
            if (highlight) {
                this.highlightCellsRed(cellColumn, false);
            }

            correct = false;

            return false;
        }
        return true;
    }

    public boolean checkCage(Cage cage, boolean highlight) {
        if (cage.checkCageFilled() && !cage.checkCage(false)) {
            if (highlight) {
                this.highlightCellsRed(cage.getCells(), true);
            }
            return false;
        }

        else if (!cage.checkCageFilled()) {

            if (highlight) {
                this.highlightCageYellow(cage);
            }
            correct = false;
        }

        return true;
    }

    public void highlightCageYellow(Cage cage) {
        gc.setFill(Color.rgb(255, 250, 205, 0.3));

        for (Cell cell : cage.getCells()) {
            if (!cell.isHighlightedRed()) {
                gc.fillRect(cell.getXCoordinate() * cellWidth, cell.getYCoordinate() * cellHeight, cellWidth,
                        cellHeight);
            }
        }

        // resetColor();
    }

    public void highlightCellsRed(ArrayList<Cell> cells, boolean isCage) {
        gc.setFill(Color.rgb(255, 0, 0, 0.3));

        for (Cell cell : cells) {

            if (!cell.isHighlightedRed()) {
                gc.fillRect(cell.getXCoordinate() * cellWidth, cell.getYCoordinate() * cellHeight, cellWidth,
                        cellHeight);
                cell.setHighlightedRed(true);
                correct = false;
            }

            if (!isCage) {
                if (cell.getRespondingCage().checkCageFilled()) {
                    ArrayList<Cell> cageCells = cell.getRespondingCage().getCells();

                    for (Cell cageCell : cageCells) {
                        if (cageCell == cell) {
                            continue;
                        }
                        if (!cageCell.isHighlightedRed()) {
                            gc.fillRect(cageCell.getXCoordinate() * cellWidth, cageCell.getYCoordinate() * cellHeight,
                                    cellWidth, cellHeight);
                            cageCell.setHighlightedRed(true);
                        }
                    }
                }
            }
        }

        // resetColor();
    }

    public Cell findEmpty() {

        for (Cell cell : cells) {
            if (cell.getValue() == 0) {
                return cell;
            }
        }

        return null;
    }

    public boolean isValid(Cell cell, int value) {
        cell.setValue(value);

        if (checkCage(cell.getRespondingCage(), false) && checkColumn(cell) && checkRow(cell)) {
            cell.clearCell();
            return true;
        } else {
            cell.clearCell();
            return false;
        }
    }

    public boolean solve() {
        Cell empty = findEmpty();

        if (empty != null) {

            for (int i = 1; i <= GameGrid.gridSize; i++) {
                if (isValid(empty, i)) {
                    empty.setValue(i);
                    if (solve()) {
                        return true;
                    }
                    empty.clearCell();
                }
            }
            return false;
        }
        return true;
    }

    public void storeAnswers(boolean solve) {
        if (solve)
            solve();

        for (Cell cell : cells) {
            cellAnswers.add(cell.getValue());
            cell.clearCell();
        }
    }

    public void deleteAnswers() {
        cellAnswers.clear();
    }

    public ArrayList<Integer> getAnswers() {
        return cellAnswers;
    }

    public void fillUpCells() {
        for (int i = 0; i < cellAnswers.size(); i++) {
            cells.get(i).setValue(cellAnswers.get(i));
        }
        draw();
    }

    public void hint() {
        Random random = new Random();
        final int maxId = gridSize * gridSize;
        int randID = random.nextInt(maxId - 1) + 1;
        int tempId = randID;

        Cell cell;
        int cellAnswer = -1;

        do {
            cell = findCell(tempId);

            if (cell.isHinted()) {
                tempId = (tempId % maxId) + 1;
            } else {
                cellAnswer = cellAnswers.get(tempId - 1);
                tempId = 0;
                break;
            }

        } while (tempId != randID);

        if (tempId == randID) {
            Alert maxHints = new Alert(Alert.AlertType.INFORMATION);
            maxHints.setHeaderText(null);
            maxHints.setGraphic(null);
            maxHints.getDialogPane().getStylesheets().add("CSS/ClearWindowDesign.css");
            maxHints.setContentText("Its time to calm, the whole grid is already solved!");
            maxHints.showAndWait();

        } else if (cellAnswer == cell.getValue()) {
            cell.setHinted(true);
            hint();
            return;
        } else {
            cell.setValue(cellAnswer);
            cell.setHinted(true);
            lastCellPressed = cell;
            this.addUndoCellPressed(lastCellPressed);
        }

        draw();
    }
}
