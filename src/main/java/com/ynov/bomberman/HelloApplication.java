package com.ynov.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javafx.scene.Group;
import javafx.animation.Timeline;

import com.ynov.bomberman.menu.Menu;
import com.ynov.bomberman.menu.MenuItem;
import com.ynov.bomberman.menu.Title;
import com.ynov.bomberman.player.Character;
import com.ynov.bomberman.stage.Game;
import com.ynov.bomberman.stage.Tile;

public class HelloApplication extends Application {

	public static final int WIDTH = 736;
	public static final int HEIGHT = 466;
	TextArea inputName;
	Stage stage;
	List<Scene> listScenes;
	List<Group> listGroups;
	Group groupMenuPage;
	Group groupGamePage;
	Scene sceneMenu;
	Scene sceneGameInitial;
	Timeline tl;
	Group group = new Group();
	int indexActiveSceneGame = 0;
	private HashMap<KeyCode, Boolean> keys = new HashMap<>();

//	Initialisation du joueur
	Character playerOne = new Character(new ImageView(new Image("/RPGMaker.png")));

	static Pane root = new Pane();

	@Override
	public void start(Stage stage) throws IOException {
		listScenes = new ArrayList<Scene>();
		listGroups = new ArrayList<Group>();

		// MenuPage
		groupMenuPage = new Group();
		ImageView img = new ImageView(new Image("/bgMenu.png"));
		img.setFitWidth(WIDTH);
		img.setFitHeight(HEIGHT);
		groupMenuPage.getChildren().add(img);

		Title title = new Title("Bomberman");
		title.setTranslateX(50);
		title.setTranslateY(200);
		MenuItem startGame = new MenuItem("NEW GAME");
		MenuItem highscore = new MenuItem("HIGHSCORE");
		MenuItem exit = new MenuItem("EXIT");
		Menu vbox = new Menu(startGame, highscore, exit);
		vbox.setTranslateX(100);
		vbox.setTranslateY(300);
		inputName = new TextArea();
		inputName.setPrefHeight(30);
		inputName.setPrefWidth(200);
		inputName.setTranslateX(100);
		inputName.setTranslateY(420);
		groupMenuPage.getChildren().addAll(title, vbox, inputName);

		sceneMenu = new Scene(groupMenuPage, WIDTH, HEIGHT);
		stage.setTitle("BOMBERMAN");
		stage.setScene(sceneMenu);

		// Game
		groupGamePage = initGame();
		listGroups.add(groupGamePage);
		sceneGameInitial = new Scene(groupGamePage, WIDTH, HEIGHT, Color.GREY);
		sceneGameInitial.setOnKeyPressed(event -> keys.put(event.getCode(), true));
		sceneGameInitial.setOnKeyReleased(event -> keys.put(event.getCode(), false));
		listScenes.add(sceneGameInitial);
		stage.show();
		startGame.button.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.setScene(sceneGameInitial);
				stage.show();
			}
		});

	}

	private Group initGame() {
//		Sauvegarde de toute les tuiles de la carte
		Tile[] mapPlaces = new Tile[299];

		int y = 0;

		int total = 0;

		for (String line : Game.LEVEL1) {

			int x = 0;

			for (String type : line.split("")) {
				Tile bloc = new Tile(new Rectangle(x * 32, y * 32 + 50, 32, 32), type, total);

				mapPlaces[total] = bloc;

				group.getChildren().add(bloc.tile);
				x++;
				total++;
			}
			y++;
		}

		group.getChildren().add(playerOne);

		Text text = new Text();
		text.setText("Time");
		text.setX(25);
		text.setY(32);
		group.getChildren().add(text);
		text.setFill(Color.WHITE);
		text.setFont(Font.font("Verdana", 25));
//		Actions en boucle
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				characterMovement();

				bombHandler(mapPlaces);
			}
		};

		timer.start();
		return group;
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
	public void bombHandler(Tile[] mapPlaces) {
		if (playerOne.bombExplosed) {

//			multiple de 23
//			-----------------------
//			---------b*b-----b-----
//			----------b-----b*b----
//			-----------------b-----
//			si * est 44eme element alors : 
//			 - 44 - 23 = explosion en haut
//			 - 44 - 1 = explotion à gauche
//			 - 44 + 1 = explotion à droite
//			 - 44 + 23 = explosion en dessous

			for (int i = 0; i < mapPlaces.length; i++) {
				if (playerOne.bomb.getCenterX() - 16 == mapPlaces[i].tile.getX()
						&& playerOne.bomb.getCenterY() - 16 == mapPlaces[i].tile.getY()) {

					if (mapPlaces[i + 1].isBreakable) {
						mapPlaces[i + 1] = new Tile(mapPlaces[i + 1].tile, "1", mapPlaces[i + 1].pos);
						group.getChildren().remove(mapPlaces[i + 1].tile);
						group.getChildren().add(mapPlaces[i + 1].tile);

					}

					if (mapPlaces[i - 1].isBreakable) {
						mapPlaces[i - 1] = new Tile(mapPlaces[i - 1].tile, "1", mapPlaces[i - 1].pos);
						group.getChildren().remove(mapPlaces[i - 1].tile);
						group.getChildren().add(mapPlaces[i - 1].tile);
					}

					if (mapPlaces[i + 23].isBreakable) {
						mapPlaces[i + 23] = new Tile(mapPlaces[i + 23].tile, "1", mapPlaces[i + 23].pos);
						group.getChildren().remove(mapPlaces[i + 23].tile);
						group.getChildren().add(mapPlaces[i + 23].tile);
					}

					if (mapPlaces[i - 23].isBreakable) {
						mapPlaces[i - 23] = new Tile(mapPlaces[i - 23].tile, "1", mapPlaces[i - 23].pos);
						group.getChildren().remove(mapPlaces[i - 23].tile);
						group.getChildren().add(mapPlaces[i - 23].tile);
					}

					playerOne.toFront();
				}
			}

			group.getChildren().remove(playerOne.bomb);

			playerOne.bombExplosed = false;
		}

		if (!playerOne.bombPlanted && isPress(KeyCode.SPACE)) {
			group.getChildren().add(playerOne.generateBomb(mapPlaces));
		}
	}

	private boolean isPress(KeyCode key) {
		return keys.getOrDefault(key, false);
	}

	public static void main(String[] args) {
		launch();
	}
}