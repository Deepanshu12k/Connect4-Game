package com.JavaFx.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int COLUMNS = 7;
	private static final int Rows = 6;
	private static final int diameter = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private static String playerOne = "Player one";
	private static String playerTwo = "Player two";

	private boolean isPlayerOneTurn = true;
	private Disc[][] insertedDiscsArray = new Disc[Rows][COLUMNS];
	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField playerOneTextField;

	@FXML
	public TextField playerSecondTextField;

	@FXML
	public Button setNamesButton;

	private boolean isAllowedToInsert = true; // to avoid adding same disc

	public void playGround() {

		setNamesButton.setOnAction(event -> {
			playerOne = playerOneTextField.getText();
			playerTwo = playerSecondTextField.getText();

		});
		Shape rectangleWithHoles = gameStructuralGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
	}

	private Shape gameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * diameter, (Rows + 1) * diameter);

		for (int row = 0; row < Rows; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				Circle circle = new Circle();
				circle.setRadius(diameter / 2);
				circle.setCenterX(diameter / 2);
				circle.setCenterY(diameter / 2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (diameter + 5) + diameter / 4);
				circle.setTranslateY(row * (diameter + 5) + diameter / 4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns() {
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < COLUMNS; col++) {
			Rectangle rectangle = new Rectangle(diameter, (Rows + 1) * diameter);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (diameter + 5) + diameter / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
               if(isAllowedToInsert){
               	isAllowedToInsert = false;
				insertDisc(new Disc(isPlayerOneTurn), column);}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc, int columns) {
		int row = Rows - 1;
		while (row >= 0) {
			if (getDiscIfPresent(row,columns) == null)
				break;

			row--;

		}
		insertedDiscsArray[row][columns] = disc;
		insertedDiscPane.getChildren().add(disc);

		disc.setTranslateX(columns * (diameter + 5) + diameter / 4);

		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (diameter + 5) + diameter / 4);
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert=true;
			if (gameEnded(currentRow, columns)) {
				gameOver();
				return;
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn ? playerOne : playerTwo);
		});
		translateTransition.play();
	}

	private void gameOver() {
       String winner = isPlayerOneTurn ? playerOne: playerTwo;
       System.out.println("Winner is"+ winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("The winner is "+winner);
		alert.setContentText("Want to play again?");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);
		Platform.runLater(()->{
			Optional<ButtonType>  clickedButton = alert.showAndWait();
			if(clickedButton.isPresent()&& clickedButton.get()==yesBtn){
				reset();
			}else{
				Platform.exit();
				System.exit(0);
			}
		});

	}

	public void reset() {
		insertedDiscPane.getChildren().clear(); //Remove all inserted disc from Pane

		for(int row=0;row<insertedDiscsArray.length;row++){
			for(int col=0;col<insertedDiscsArray.length;col++){
				insertedDiscsArray[row][col]=null;
			}
		}
		isPlayerOneTurn=true;
		playerNameLabel.setText(playerOne);
		playGround();
	}

	private boolean gameEnded(int row, int column) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3, row+3)//range of row values : 0,1,2,3,4,5
				                     .mapToObj(r -> new Point2D(r,column))// Points like (0,3)(1,3)
				                     .collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)//range of Columns values : 0,1,2,3,4,5
				.mapToObj(col -> new Point2D(row,col))// Points
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-3,column+3);
		List<Point2D> diagonal1 = IntStream.rangeClosed(0,6)
				                  .mapToObj(i -> startPoint1.add(i,-i))
				                  .collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-3,column-3);
		List<Point2D> diagonal2 = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint2.add(i,i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				          || checkCombinations(diagonal1) || checkCombinations(diagonal2);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain=0;
		for(Point2D point2D:points){

			int rowIndexArray = (int) point2D.getX();
			int columnIndexArray = (int) point2D.getY();

			Disc disc = getDiscIfPresent(rowIndexArray,columnIndexArray);
			if(disc!=null && disc.isPlayerOneMove==isPlayerOneTurn){
				chain++;
				if(chain==4){
					return true;
				}
			  }else{
					chain = 0;
				}
		}
		return false;
	}
	
	private Disc getDiscIfPresent(int row,int column){  //to prevent array out of bounds exception
         if(row >= Rows || row < 0 || column >= COLUMNS || column < 0){
         	return null;
         }
         return insertedDiscsArray[row][column];
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(diameter / 2);
			setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			setCenterX(diameter / 2);
			setCenterY(diameter / 2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
