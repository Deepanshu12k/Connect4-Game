package com.JavaFx.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
   private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{

       FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
       GridPane rootGridPane = loader.load();
//         controller
         controller = loader.getController();
         controller.playGround();
//         MenuBar
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind( primaryStage.widthProperty() );
//         Menu
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
//         creating scene
         Scene scene = new Scene(rootGridPane);
         primaryStage.setScene(scene);
         primaryStage.setTitle("Connect4");
         primaryStage.setResizable(false);
//         finally display
         primaryStage.show();
    }

   private MenuBar createMenu(){
       Menu fileMenu = new Menu("File");
       Menu helpMenu = new Menu("Help");
//       fileMenu
       MenuItem  newGame    = new MenuItem("New Game");
       newGame.setOnAction(event -> controller.reset());
       MenuItem  resetGame    = new MenuItem("Reset");
       resetGame.setOnAction(event -> controller.reset());
       SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
       MenuItem  exitGame    = new MenuItem("Exit");
       exitGame.setOnAction(event -> exit());
       fileMenu.getItems().addAll(newGame, resetGame,separatorMenuItem,exitGame);
//    helpMenu
       MenuItem  aboutGame    = new MenuItem("About Connect4");
       aboutGame.setOnAction(event -> aboutConnect4());

       helpMenu.getItems().addAll(aboutGame);
//       MenuBar
       MenuBar menuBar = new MenuBar();
       menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;
   }



    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect4");
        alert.setHeaderText("How to play");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exit() {
        Platform.exit();
        System.exit(0);
    }



    public static void main(String[] args) {
        launch(args);
    }
}
