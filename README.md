# MathDokuFX

## Disclaimer

- This is one of the first major projects I've written in java over a year ago and therefore it most likely contains a bunch of bad practices
  - For example, I've never really used the MVC pattern when writing the controller functions
  - Moreover, this project didn't use the fxml for the purposes of better understanding of pure JavaFx
- In the future I am most likely going to polish the project further to improve it's readability but I also welcome any feedback
  - Supporting the project directly by forking/making commits is also very welcome!

## Outline

- The game is capable of generating or loading in the games of [Mathdoku](https://www.kenkenpuzzle.com/howto/solve#), up to 8x8 size
- Settings can be opened by pressing `z` on your keyboard
  - Font size and the overall colours can be changed, as well as `mousemode` enabled if you are unable to use the keyboard
    - ~~ironically, you do have to press `z` to enable the mouse mode~~
- To move around the grid, you can either use the mouse, or the arrow keys or **even** `wasd`
- To complete the game, hit the `mistakes` button and if there are no mistakes, then the game will end
- When pressing `mistakes` button:
  - Yellow cells represent the cells that are part of an (partially) empty cage
  - Red cells represent rows/columns and cages that contain a wrong value
- If the game turns out to be too hard for you use `hint` or `auto` to either get a hint of what the next value in some random cell should be or finish off the game early
- `undo` and `redo` work exactly the same way as in any other game
- Under no circumstances would I recommend pressing the `clear` button, unless you want to start all over again :smile:

## Production

- `mvn package` to create a `.jar` file of the project that will be located in the `target` folder
- Due to the use of libraries specific to the OS, the `.jar` file should be able to work on all OS's

## Development

### Requirements

- You should have `jdk-11` or newer as well as `maven` installed

### Tree

```
.
├── pom.xml
├── README.md
├── src
│   └── main
│       ├── java
│       │   ├── com
│       │   │   └── mathdoku
│       │   │       ├── Cage.java
│       │   │       ├── Cell.java
│       │   │       ├── GameGrid.java
│       │   │       ├── InputReader.java
│       │   │       ├── Main.java
│       │   │       ├── MatrixGenerator.java
│       │   │       ├── Super.java
│       │   │       └── TextAreaInputDialog.java
│       │   └── module-info.java
│       └── resources
│           ├── Background.css
│           ├── Board.jpg
│           ├── CanvasDesign.css
│           ├── ClearWindowDesign.css
│           ├── FireworksGif.gif
│           ├── GameButtons.css
│           ├── LoadFromInputDesign.css
│           ├── NumPadButtons.css
│           ├── SettingsButtons.css
│           ├── SliderStyle.css
│           ├── TitleButtons.css
│           └── WinScreen.css
└── target
    │
    .
    .
    .
```

- The whole project has a default maven structure, where all the source code is contained in `src`, with `main` containing the actual application and `resoruces` containing the relevant stylesheets
- `target` is where all the production files will be at

### Running the Application

- `mvn clean javafx:run` to remove the previously produced binaries and re-compile the application
- `mvn javafx:run` to simply run existing binaries
