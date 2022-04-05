package com.ynov.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ynov.bomberman.player.Character;
import com.ynov.bomberman.stage.Game;

public class HelloApplication extends Application {
    
    public static final int WIDTH = 736;
    public static final int HEIGHT = 466;
    
	private HashMap<KeyCode, Boolean> keys = new HashMap<>();

//	Initialisation du joueur
	Character playerOne = new Character(new ImageView(new Image("/RPGMaker.png")));
	
	static Pane root = new Pane();

	@Override
	public void start(Stage stage) throws IOException {
		
		root.setPrefSize(WIDTH, HEIGHT);
		
		ArrayList<Rectangle> mapPlaces = new ArrayList<>();

		for (int y = 0; y < Game.LEVEL1.length; y++) {
			String ligne = Game.LEVEL1[y];
			//System.out.println(ligne);
			
			String[] tile = ligne.split("");
			for (int x = 0; x < tile.length; x++) {
				//System.out.println(tile[j]);
				
				Rectangle bloc = new Rectangle(x * 32, y * 32 + 50, 32, 32);
				
				switch (tile[x]) {
				case "0":
					Image wall = new Image("/Wall.png");
					bloc.setFill(new ImagePattern(wall));
					break;
				case "1":
					Image grass = new Image("/Grass.png");
					bloc.setFill(new ImagePattern(grass));
					break;
				case "2":
					Image brick = new Image("/Brick.png");
					bloc.setFill(new ImagePattern(brick));
					break;
				default:
					break;
				}
				mapPlaces.add(bloc);
				root.getChildren().add(bloc);
				
			}
		}

		root.getChildren().add(playerOne);

//		On génere la scene
		Scene scene = new Scene(root, Color.GREY);
		
		Text text = new Text();
		text.setText("Time");
		text.setX(20);
		text.setY(10);
		root.getChildren().add(text);
		text.setFill(Color.WHITE);
		text.setFont(Font.font("Verdana",50));

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

//	characterMovement prend en charge les mouvements du joueur
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