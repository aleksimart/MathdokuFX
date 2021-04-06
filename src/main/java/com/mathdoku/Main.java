package com.mathdoku;

import com.jfoenix.controls.JFXSlider;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    private Stage primaryStage;
    private ObjectProperty<Color> baseColor;
    private InputReader inputReader;
    private MatrixGenerator generator;

    /**
     * Define the colours to be used to change the letters in different nodes Only
     * applies to some of the nodes (majority), others have their own keyvalues
     * defined
     */
    private void setupBaseColor() {

        // Initialise the attribute
        baseColor = new SimpleObjectProperty<>();

        // Add the appropriate colours
        KeyValue keyValue1 = new KeyValue(this.baseColor, Color.rgb(139, 69, 19));
        KeyValue keyValue2 = new KeyValue(this.baseColor, Color.WHITE);
        KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);

        // Run the animation
        Timeline timeline = new Timeline(keyFrame1, keyFrame2);

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Apply a listener to a particular node to make it's letters change colour
     * 
     * @param node node to apply the listener to
     */
    private void dynamicColorText(Node node) {

        baseColor.addListener((obs, oldColor, newColor) -> node
                .setStyle(String.format("-gradient-base: #%02x%02x%02x; ", (int) (newColor.getRed() * 255),
                        (int) (newColor.getGreen() * 255), (int) (newColor.getBlue() * 255))));
    }

    /**
     * Method for defining the title screen
     * 
     * @param primaryStage stage to display the title screen on
     */
    @Override
    public void start(Stage primaryStage) {

        this.setupBaseColor();
        // Removed fullscreen because it messes everything up
        primaryStage.initStyle(StageStyle.UTILITY);

        // If exit was pressed, quit the previous stage
        if (this.primaryStage != null) {
            this.primaryStage.close();
        }

        this.primaryStage = primaryStage;

        // Define all the panes to be used
        BorderPane rootRoot = new BorderPane();
        GridPane root = new GridPane();
        HBox labelPane = new HBox();

        // Create the scene and give the appropriate stylesheet to it
        Scene scene = new Scene(rootRoot);
        scene.getStylesheets().add("TitleButtons.css");
        scene.getStylesheets().add("Background.css");

        // Define label to display the game name
        Label mathDokuLable = new Label("MathDoku");

        // Place the label in the hbox to centralise it
        labelPane.getChildren().add(mathDokuLable);
        labelPane.setAlignment(Pos.CENTER);

        // Create all the buttons
        mainMenuButtons(root);

        dynamicColorText(mathDokuLable);
        root.add(labelPane, 0, 0, 2, 1);

        // Extra changes for the panes
        root.setAlignment(Pos.CENTER);

        root.setVgap(10);
        root.setHgap(5);

        root.setPadding(new Insets(100, 100, 100, 100));
        rootRoot.setCenter(root);

        // Finalising
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);

        primaryStage.show();
    }

    /**
     * All the functional buttons to add to the main menu
     * 
     * @param root GridPane to add the buttons to
     */
    private void mainMenuButtons(GridPane root) {
        // Create functional buttons
        Button generateGame = new Button("Generate game");
        generateGame.setOnAction(event -> {

            List<Integer> choices = new ArrayList<>();
            choices.add(2);
            choices.add(3);
            choices.add(4);
            choices.add(5);
            choices.add(6);
            choices.add(7);
            choices.add(8);

            ChoiceDialog<Integer> dialog = new ChoiceDialog<>(2, choices);
            dialog.setTitle("Matrix size");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Choose your matrix size:");

            Optional<Integer> result = dialog.showAndWait();
            // The Java 8 way to get the response value (with lambda expression).
            if (result.isPresent()) {
                generator = new MatrixGenerator(MatrixGenerator.Difficulty.EASY, result.get());
                startGameGrid();
            }
        });

        Button loadfromFile = new Button("Load from File");
        loadfromFile.getStyleClass().add("load-buttons");
        loadfromFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File to Load");

            FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
            fileChooser.getExtensionFilters().add(txtFilter);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    inputReader = new InputReader(file);
                    primaryStage.close();
                    this.startGameGrid();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button loadFromInput = new Button("Load from Input");
        loadFromInput.setOnAction(event -> {

            TextAreaInputDialog dialog = new TextAreaInputDialog();
            dialog.setHeaderText("Enter the game designed in the specified format:");
            dialog.setGraphic(null);

            // Show the dialog and capture the result.

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                inputReader = new InputReader(result.get());
                if (!inputReader.getError()) {
                    primaryStage.close();
                    this.startGameGrid();
                }
            }
        });

        loadFromInput.getStyleClass().add("load-buttons");

        Button loadGame = new Button("Load game");

        Button rules = new Button("Rules");
        rules.setOnAction(event -> getHostServices().showDocument("https://www.kenkenpuzzle.com/howto/solve"));

        dynamicColorText(generateGame);
        dynamicColorText(loadGame);
        dynamicColorText(rules);
        dynamicColorText(loadfromFile);
        dynamicColorText(loadFromInput);

        // Makes the button disappear when it is clicked
        loadGame.setOnAction(event -> {
            FadeTransition ft = new FadeTransition(Duration.millis(500), loadGame);
            ft.setFromValue(1.0);
            ft.setToValue(0);
            ft.setCycleCount(1);
            ft.play();
        });

        // Add the other two options once the button disappears
        loadGame.opacityProperty().addListener(observable -> {
            if (loadGame.getOpacity() == 0) {
                // loadGame.setVisible(false);
                root.add(loadfromFile, 0, 2);
                root.add(loadFromInput, 1, 2);
            }
        });

        // Add the components
        root.add(generateGame, 0, 1, 2, 1);
        root.add(loadGame, 0, 2, 2, 1);
        root.add(rules, 0, 3, 2, 1);
    }

    /**
     * Defines the functionality and looks for the actual game and the game menu
     */
    private void startGameGrid() {

        // Close title screen
        primaryStage.close();
        primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);

        // Root pane to display the grid and options on
        StackPane rootRoot = new StackPane();

        // Pane for displaying the game and the button
        BorderPane root = new BorderPane();
        root.setPrefSize(500, 600);

        rootRoot.getChildren().add(root);

        // Creating an instance of the game and setting it to center
        GameGrid mathDoku;

        // Never implemented the matrix generation, therefore generate game simply
        // creates a 6x6 game with no cages
        if (inputReader != null) {
            mathDoku = inputReader.getMathdoku();
        } else {
            mathDoku = generator.getGameGrid();
        }

        root.setCenter(mathDoku.getCanvasPane());

        // Pane for all helping buttons
        GridPane buttons = new GridPane();
        buttons.setAlignment(Pos.CENTER);
        root.setBottom(buttons);

        // Buttons to add to the bottom and their functionality
        Button undo = new Button("Undo");
        undo.setFocusTraversable(false);
        root.addEventFilter(Event.ANY, event -> undo.setDisable(mathDoku.undoIsEmpty()));
        undo.setOnAction(event -> {
            ShowMistakesEnabler.disableShowMistakesEnabled();
            mathDoku.setShowMistakes(false);
            mathDoku.undoCellPressed();
        });

        Button redo = new Button("Redo");
        redo.setFocusTraversable(false);
        root.addEventFilter(Event.ANY, event -> redo.setDisable(mathDoku.redoIsEmpty()));
        redo.setOnAction(event -> {
            ShowMistakesEnabler.disableShowMistakesEnabled();
            mathDoku.setShowMistakes(false);
            mathDoku.redoCellPressed();
        });

        Button clear = new Button("Clear");
        clear.setFocusTraversable(false);
        clear.setOnAction(event -> {

            ShowMistakesEnabler.disableShowMistakesEnabled();
            mathDoku.setShowMistakes(false);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to clear the board? This cannot be undone.");
            alert.setHeaderText(null);

            alert.setGraphic(null);

            alert.getDialogPane().getStylesheets().add("ClearWindowDesign.css");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (Cell cell : mathDoku.getCells()) {
                    cell.clearCell();
                }
                mathDoku.clearUndoCells();
                mathDoku.clearRedoCells();
                mathDoku.draw();
            }
        });

        Button hint = new Button("Hint");
        hint.setFocusTraversable(false);
        hint.setOnAction(event -> mathDoku.hint());

        Button autoFill = new Button("Auto");
        autoFill.setFocusTraversable(false);
        autoFill.setOnAction(event -> {
            mathDoku.fillUpCells();
            mathDoku.checkAnswers(true);
            winningAnimation(rootRoot);
        });
        Button showMistakes = new Button("Mistakes");
        showMistakes.setFocusTraversable(false);
        showMistakes.setOnAction(event -> {
            mathDoku.setShowMistakes(ShowMistakesEnabler.toggleShowMistakesEnabled());
            mathDoku.draw();

            if (mathDoku.isCorrect()) {
                winningAnimation(rootRoot);
            }
        });

        // Adding buttons to the grid
        buttons.add(undo, 0, 0);
        buttons.add(redo, 1, 0);
        buttons.add(clear, 2, 0);
        buttons.add(hint, 0, 1);
        buttons.add(showMistakes, 1, 1);
        buttons.add(autoFill, 2, 1);

        // Scene setup and a few handlers/listeners
        Scene scene = new Scene(rootRoot);

        /*
         * // Make the sidepanes vary in size with the scene size changes
         * scene.widthProperty().addListener(observable -> {
         * colourPane1.setMinWidth(scene.getWidth()/10);
         * colourPane2.setMinWidth(scene.getWidth()/10); });
         * 
         */

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> settings(rootRoot, event, mathDoku));

        scene.getStylesheets().add("Background.css");
        scene.getStylesheets().add("GameButtons.css");

        primaryStage.setScene(scene);

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(800);

        primaryStage.show();
    }

    private void settings(StackPane rootRoot, KeyEvent event, GameGrid mathDoku) {

        // Open menu if z is pressed
        if (event.getCode() == KeyCode.Z) {

            // If settings isn't opened already
            if (rootRoot.getChildren().size() == 1) {

                // Buttons to add to settings and their functionality
                Button resume = new Button("Resume");
                resume.setOnAction(event1 -> rootRoot.getChildren().remove(1));

                Button exit = new Button("Exit");
                exit.setOnAction(event1 -> start(new Stage()));

                Button fontSize = new Button("Font");
                fontSize.setOnAction(event1 -> fontSettings(rootRoot, mathDoku));

                // Start of mousemode
                Button mouseMode = new Button("Mouse Mode");

                Stage numPadStage = new Stage();
                numPadStage.initStyle(StageStyle.UTILITY);
                numPadStage.setAlwaysOnTop(true);
                numPadStage.setMaxHeight(300);
                numPadStage.setMaxWidth(200);
                numPadStage.setMinWidth(200);
                numPadStage.setMinHeight(300);
                // numPadStage.initOwner(primaryStage);

                GridPane numPad = new GridPane();

                for (int i = 0; i < 9; i++) {
                    int row;
                    if (i <= 2) {
                        row = 0;
                    } else if (i <= 5) {
                        row = 1;
                    } else {
                        row = 2;
                    }

                    Button num = new Button(Integer.toString(i + 1));
                    final int value = i + 1;
                    num.setOnAction(event2 -> mathDoku.inputCellValue(value));

                    numPad.add(num, i % 3, row);
                }

                Button erase = new Button("Clear Cell");
                erase.setOnAction(event2 -> mathDoku.inputCellValue(0));
                numPad.add(erase, 0, 3, 3, 1);

                Scene numPadScene = new Scene(numPad, 200, 300);
                numPad.setAlignment(Pos.CENTER);

                numPadStage.setScene(numPadScene);
                numPad.getStylesheets().add("NumPadButtons.css");
                numPadScene.getStylesheets().add("BackGround.css");
                // End of mousemode

                mouseMode.setOnAction(event1 -> {
                    if (!numPadStage.isShowing()) {
                        numPadStage.show();
                    }
                });

                // Root pane of menu
                BorderPane menu = new BorderPane();
                menu.getStylesheets().add("SettingsButtons.css");
                menu.setStyle("-fx-background-color: rgba(210,180,140,0.5);");

                // Pane for aligning label
                HBox labelPane = new HBox();
                labelPane.setAlignment(Pos.CENTER);
                menu.setTop(labelPane);

                // Actual settings label
                Label settingsLabel = new Label("Settings");
                labelPane.getChildren().add(settingsLabel);

                // Pane to add the settings buttons to
                GridPane settings = new GridPane();
                settings.setVgap(10);
                settings.setAlignment(Pos.CENTER);

                settings.add(resume, 0, 0);
                settings.add(fontSize, 0, 1);
                settings.add(mouseMode, 0, 2);
                settings.add(exit, 0, 3);

                // Add buttons to center of menu
                menu.setCenter(settings);
                rootRoot.getChildren().add(menu);

                // Add colored text to menu label, buttons
                dynamicColorText(settingsLabel);
                dynamicColorText(resume);
                dynamicColorText(fontSize);
                dynamicColorText(mouseMode);
                dynamicColorText(exit);

            } else {
                rootRoot.getChildren().remove(rootRoot.getChildren().size() - 1);
            }
        }

        // Makes sure that user doesn't control the grid on the background while in menu
        if (rootRoot.getChildren().size() >= 2) {
            event.consume();
        }
    }

    private void fontSettings(StackPane rootRoot, GameGrid mathDoku) {
        // Pane to show the whole menu
        BorderPane menu = new BorderPane();

        // Add styling to the main pane
        menu.getStylesheets().add("SettingsButtons.css"); // getStylesheets().add(getClass().getResource("CSS/TitleButtons.css").toExternalForm());
        menu.setStyle("-fx-background-color: rgba(210,180,140,0.9);");

        // Put the label in this pane and center it
        HBox labelPane = new HBox();
        labelPane.setAlignment(Pos.CENTER);
        menu.setTop(labelPane);

        // Actual label for the screen
        Label fontSettingsLabel = new Label("Font Settings");
        labelPane.getChildren().add(fontSettingsLabel);

        // Pane for keeping the settings
        GridPane settings = new GridPane();

        settings.setVgap(10);
        settings.setHgap(10);
        settings.setAlignment(Pos.CENTER);
        // settings.setFocusTraversable(false);

        Label sizeLabel = new Label("Size: ");

        // Slider looks off without extra padding
        HBox sliderAligner = new HBox();
        sliderAligner.setAlignment(Pos.CENTER);
        sliderAligner.setPadding(new Insets(15, 0, 0, 0));

        JFXSlider fontSizeSlider = new JFXSlider();
        fontSizeSlider.setOrientation(Orientation.HORIZONTAL);
        fontSizeSlider.setMin(15);
        fontSizeSlider.setMax(20);
        fontSizeSlider.setValue(mathDoku.getFontSize());

        /*
         * fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue)
         * -> { fontsize = newValue; mathDoku.setFontSize((double) newValue); });
         */

        fontSizeSlider.getStylesheets().add("SliderStyle.css");
        sliderAligner.getChildren().addAll(fontSizeSlider);

        ObjectProperty<Color> baseColorNew = new SimpleObjectProperty<>();

        KeyValue keyValue1 = new KeyValue(baseColorNew, Color.rgb(139, 69, 19));
        KeyValue keyValue2 = new KeyValue(baseColorNew, Color.rgb(255, 122, 32));
        KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(1000), keyValue2);

        // Run the animation
        Timeline timeline = new Timeline(keyFrame1, keyFrame2);

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        baseColorNew.addListener((obs, oldColor, newColor) -> fontSizeSlider
                .setStyle(String.format("-thumb-colored-track-color: #%02x%02x%02x; ", (int) (newColor.getRed() * 255),
                        (int) (newColor.getGreen() * 255), (int) (newColor.getBlue() * 255))));

        Label colorLabel = new Label("Color: ");

        HBox colors = new HBox(50);
        colors.setPadding(new Insets(15, 0, 0, 20));

        Rectangle white = new Rectangle(150, 80);
        white.setStrokeWidth(5);
        white.setStroke(Color.rgb(139, 69, 19));
        white.setFill(Color.WHITE);

        Rectangle lightBrown = new Rectangle(150, 80);
        lightBrown.setStrokeWidth(5);
        lightBrown.setStroke(Color.rgb(139, 69, 19));
        lightBrown.setFill(Color.rgb(255, 243, 0));

        lightBrown.setOnMouseClicked(event1 -> {
            lightBrown.setStroke(Color.rgb(253, 223, 51));
            mathDoku.setFontColor(Color.rgb(255, 243, 0));
            white.setStroke(Color.rgb(139, 69, 19));
        });

        white.setOnMouseClicked(event1 -> {
            white.setStroke(Color.rgb(253, 223, 51));
            mathDoku.setFontColor(Color.WHITE);
            lightBrown.setStroke(Color.rgb(139, 69, 19));
        });

        if (mathDoku.getFontColor() == Color.WHITE) {
            white.setStroke(Color.rgb(253, 223, 51));
        } else {
            lightBrown.setStroke(Color.rgb(253, 223, 51));
        }

        colors.getChildren().addAll(white, lightBrown);

        settings.add(sizeLabel, 0, 0);
        settings.add(sliderAligner, 1, 0);
        settings.add(colorLabel, 0, 1);
        settings.add(colors, 1, 1);

        // Add buttons to center of menu
        menu.setCenter(settings);

        // Add back button
        HBox backButton = new HBox();
        backButton.setAlignment(Pos.CENTER);
        menu.setBottom(backButton);

        Button back = new Button("Back");
        back.setOnAction(event -> {
            rootRoot.getChildren().remove(rootRoot.getChildren().size() - 1);
            mathDoku.setFontSize(fontSizeSlider.getValue());
            // System.out.println(fontSizeSlider.getValue());
        });
        rootRoot.addEventFilter(KeyEvent.KEY_PRESSED, event -> mathDoku.setFontSize(fontSizeSlider.getValue()));

        backButton.getChildren().add(back);

        dynamicColorText(back);
        dynamicColorText(fontSettingsLabel);
        dynamicColorText(sizeLabel);
        dynamicColorText(colorLabel);

        rootRoot.getChildren().add(menu);
    }

    private void winningAnimation(StackPane rootRoot) {
        Image firework = new Image(getClass().getClassLoader().getResource("FireworksGif.gif").toExternalForm());
        ImageView iv1 = new ImageView(firework);
        ImageView iv2 = new ImageView(firework);

        BorderPane fireworksPane = new BorderPane();

        iv1.setFitWidth(300);
        iv1.setPreserveRatio(true);
        iv2.setFitWidth(300);
        iv2.setPreserveRatio(true);

        BorderPane.setAlignment(iv1, Pos.CENTER);
        BorderPane.setAlignment(iv2, Pos.CENTER);

        fireworksPane.setLeft(iv1);
        fireworksPane.setRight(iv2);

        GridPane winPane = new GridPane();
        HBox labelBox = new HBox();

        Label winLabel = new Label(" Congrats! \n You Won!");

        labelBox.getChildren().add(winLabel);
        labelBox.setAlignment(Pos.CENTER);

        Button ok = new Button("OK");
        ok.setOnAction(event -> {
            inputReader = null;
            primaryStage.close();
            this.start(new Stage());
        });

        winPane.add(winLabel, 0, 0, 2, 1);
        winPane.setAlignment(Pos.CENTER);
        winPane.add(ok, 0, 1, 2, 1);

        fireworksPane.setCenter(winPane);

        // Initialise the attribute
        ObjectProperty<Color> baseColor1 = new SimpleObjectProperty<>();

        // Add the appropriate colours
        KeyValue keyValue1 = new KeyValue(baseColor1, Color.rgb(255, 246, 0));
        KeyValue keyValue2 = new KeyValue(baseColor1, Color.rgb(107, 255, 0));
        KeyValue keyValue3 = new KeyValue(baseColor1, Color.rgb(255, 127, 0));

        KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(1000), keyValue2);
        KeyFrame keyFrame3 = new KeyFrame(Duration.millis(500), keyValue3);

        // Run the animation
        Timeline timeline = new Timeline(keyFrame1, keyFrame2, keyFrame3);

        baseColor1.addListener((obs, oldColor, newColor) -> winLabel
                .setStyle(String.format("-gradient-base: #%02x%02x%02x; ", (int) (newColor.getRed() * 255),
                        (int) (newColor.getGreen() * 255), (int) (newColor.getBlue() * 255))));

        baseColor1.addListener((obs, oldColor, newColor) -> ok.setStyle(String.format("-gradient-base: #%02x%02x%02x; ",
                (int) (newColor.getRed() * 255), (int) (newColor.getGreen() * 255), (int) (newColor.getBlue() * 255))));

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        fireworksPane.getStylesheets().add("WinScreen.css");
        fireworksPane.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        rootRoot.getChildren().add(fireworksPane);
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Class storing the boolean value for the 'showMistakes' button Ensures that if
     * mistakes are shown and a user enters a value/moves to another cell, the
     * effect will be undone as if they clicked a button again
     */
    static class ShowMistakesEnabler {
        static boolean showMistakesEnabled = false;

        /**
         * Toggles the value of the boolean between true and false
         * 
         * @return showMistakesEnabled with negated valued
         */
        static boolean toggleShowMistakesEnabled() {
            showMistakesEnabled = !showMistakesEnabled;
            return showMistakesEnabled;
        }

        /**
         * Sets the value of a value to false
         */
        static void disableShowMistakesEnabled() {
            showMistakesEnabled = false;
        }
    }
}
