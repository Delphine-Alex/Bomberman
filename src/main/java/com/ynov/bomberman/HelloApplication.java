package com.ynov.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ynov.bomberman.player.Character;

public class HelloApplication extends Application {

	private HashMap<KeyCode, Boolean> keys = new HashMap<>();

	Character playerOne = new Character(new ImageView(new Image("/RPGMaker.png")));
	static Pane root = new Pane();

	@Override
	public void start(Stage stage) throws IOException {
		root.setPrefSize(736, 416);

//		Should generate map here
//		13 / 23
//		***********************		23 * 32px = 736
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		***********************
//		32px * 13 = 416px
//		total tuiles = 23 * 13 = 299
//		Map here

		ArrayList<Rectangle> mapPlaces = new ArrayList<>();
		int xPass = 0;
		while (xPass != 23) {
			int yPass = 0;
			while (yPass != 13) {
				Rectangle tuile = new Rectangle(xPass * 32, yPass * 32, 32, 32);
				tuile.setFill(Color.PINK);
				tuile.setStroke(Color.PURPLE);
				tuile.setStrokeWidth(3);
				mapPlaces.add(tuile);
				root.getChildren().add(tuile);
				yPass++;
			}
			xPass++;
		}

		root.getChildren().add(playerOne);

		Scene scene = new Scene(root);

		scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
		scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				characterMovement();

				bombHandler(mapPlaces);
			}
		};

		timer.start();
		stage.setTitle("BOMBERMAN");
		stage.setScene(scene);
		stage.show();
	}

	public void characterMovement() {
		if (isPress(KeyCode.Z)) {
			playerOne.charachterAnimation.play();
			playerOne.charachterAnimation.setOffsetY(96);
			playerOne.moveY(-2);
		} else if (isPress(KeyCode.S)) {
			playerOne.charachterAnimation.play();
			playerOne.charachterAnimation.setOffsetY(0);
			playerOne.moveY(2);
		} else if (isPress(KeyCode.D)) {
			playerOne.charachterAnimation.play();
			playerOne.charachterAnimation.setOffsetY(64);
			playerOne.moveX(2);
		} else if (isPress(KeyCode.Q)) {
			playerOne.charachterAnimation.play();
			playerOne.charachterAnimation.setOffsetY(32);
			playerOne.moveX(-2);
		} else {
			playerOne.charachterAnimation.stop();
		}
	}

	public void bombHandler(ArrayList<Rectangle> mapPlaces) {
		if (playerOne.bombExplosed) {
			root.getChildren().remove(playerOne.bomb);
			playerOne.bombExplosed = false;
		}

		if (!playerOne.bombPlanted && isPress(KeyCode.SPACE)) {
			root.getChildren().add(playerOne.generateBomb(mapPlaces));
		}
	}

	private boolean isPress(KeyCode key) {
		return keys.getOrDefault(key, false);
	}

	public static void main(String[] args) {
		launch();
	}
}