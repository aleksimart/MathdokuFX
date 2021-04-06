package com.mathdoku;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.*;
import java.util.ArrayList;

class InputReader {

    private String input;
    private GameGrid mathdoku;
    private int maxvalue;

    private ArrayList<String> labels;
    private ArrayList<int[]> ids;
    private boolean error;

    InputReader(String input) {
        this.input = input;
        maxvalue = 0;

        labels = new ArrayList<>();
        ids = new ArrayList<>();

        try {
            this.readCages();
            mathdoku = this.createGame();
            this.createCages();
            this.checkCellUsage();
            mathdoku.storeAnswers(true);
            error = false;

        } catch (Exception e) {
            this.ExceptionDialogCreator(e);
            error = true;
        }

    }

    InputReader(File input) throws IOException {
        this.input = readInputFromFile(input);
        maxvalue = 0;

        labels = new ArrayList<>();
        ids = new ArrayList<>();

        try {
            this.readCages();
            mathdoku = this.createGame();
            this.createCages();
            this.checkCellUsage();
            mathdoku.storeAnswers(true);
            error = false;

        } catch (Exception e) {
            this.ExceptionDialogCreator(e);
            error = true;
        }
    }

    /**
     * @link{https://code.makery.ch/blog/javafx-dialogs-official/}
     */
    private void ExceptionDialogCreator(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("FATAL ERROR");
        alert.setHeaderText("Error While reading the input");
        alert.setContentText(ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    boolean getError() {
        return error;
    }

    private String readInputFromFile(File file) throws IOException {
        Reader reader = new FileReader(file);
        StringBuilder input = new StringBuilder();

        int temp;

        while ((temp = reader.read()) != -1) {
            input.append((char) temp);
        }

        return input.toString();
    }

    private void readCages() {
        String[] lines = input.split("\r|\n|\r\n");

        for (String line : lines) {

            if (line.length() != 0) {

                String label = line.split(" ")[0];
                String[] cellIDs = line.split(" ")[1].split(",");
                int[] cellIDsInt = new int[cellIDs.length];

                for (int i = 0; i < cellIDs.length; i++) {
                    int cellIDInt = Integer.parseInt(cellIDs[i]);
                    cellIDsInt[i] = cellIDInt;

                    if (cellIDInt > maxvalue) {
                        maxvalue = cellIDInt;
                    }
                }

                labels.add(label);
                ids.add(cellIDsInt);
            }
        }
    }

    private void createCages() throws Exception {
        for (int i = 0; i < labels.size(); i++) {
            Cage cage = new Cage(labels.get(i));

            for (int id : ids.get(i)) {
                Cell cell = GameGrid.findCell(id);
                cage.addCell(cell);
            }

            cage.checkNeighbours();
            mathdoku.addCage(cage);
        }
    }

    private GameGrid createGame() throws Exception {
        double gridSize = Math.sqrt(maxvalue);

        if (!((gridSize == Math.floor(gridSize)) && !Double.isInfinite(gridSize))) {
            throw new Exception("Inadequate grid size. Must be a square");
        }

        return new GameGrid((int) gridSize);
    }

    private void checkCellUsage() throws Exception {
        ArrayList<Cell> unusedCells = new ArrayList<>();
        for (Cell cell : mathdoku.getCells()) {
            if (cell.getRespondingCage() == null) {
                unusedCells.add(cell);
            }
        }

        if (unusedCells.size() != 0) {
            StringBuilder err = new StringBuilder();
            err.append("Cell with ID/ID's ");

            for (int i = 0; i < unusedCells.size() - 1; i++) {
                err.append(unusedCells.get(i).getId());
                err.append(", ");
            }
            err.append(unusedCells.get(unusedCells.size() - 1).getId());
            err.append(" is not used anywhere");
            throw new Exception(err.toString());
        }
    }

    GameGrid getMathdoku() {
        return mathdoku;
    }
}
