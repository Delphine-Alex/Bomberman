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

//	Initialisation du joueur
	Character playerOne = new Character(new ImageView(new Image("/RPGMaker.png")));

	static Pane root = new Pane();

	@Override
	public void start(Stage stage) throws IOException {
//		736 = 23 tuile * 32 px ; 416 = 13 tuiles * 32px
		root.setPrefSize(736, 416);

//		Sauvegarde de toute les tuiles de la carte
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

//		On génere la scene
		Scene scene = new Scene(root);

//		On active le support d'entrée du clavier
		scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
		scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

//		Actions en boucle
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

//	characterMovement prend en charge les mouvement du joueur
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

//	bombHandler supporte la pose et l'explosion des bombes du joueur
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