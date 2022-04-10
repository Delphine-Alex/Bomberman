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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.Group;
import javafx.animation.Timeline;

import com.ynov.bomberman.menu.Menu;
import com.ynov.bomberman.menu.MenuItem;
import com.ynov.bomberman.menu.Title;
import com.ynov.bomberman.player.Character;
import com.ynov.bomberman.player.Enemy;
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
	boolean isMouvement = false;

//	Initialisation du joueur
	Character playerOne = new Character(new ImageView(new Image("/RPGMaker.png")));

//	Initialisation des ennemies
	Enemy onil = new Enemy(new ImageView(new Image("/Ballom.png")));

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
		int pos = 0;

		for (String line : Game.LEVEL1) {
			int x = 0;
			for (String type : line.split("")) {
				Tile bloc = new Tile(x, y, type, pos);

				mapPlaces[pos] = bloc;

				group.getChildren().add(bloc.tile);
				x++;
				pos++;
			}
			y++;
		}

		group.getChildren().add(playerOne);
		group.getChildren().add(onil);

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
				characterMovement(mapPlaces);
				if (!isMouvement){
					enemyMovement(mapPlaces);
				}
				bombHandler(mapPlaces);
			}
		};

		timer.start();
		return group;
	}

//	characterMovement prend en charge les mouvements du joueur
	public void characterMovement(Tile[] mapPlaces) {

		if (isPress(KeyCode.Z)) {
			for (int i = 0; i < mapPlaces.length; i++) {
				if ((playerOne.getBoundsInParent().getCenterX() >= mapPlaces[i].tile.getX()
						&& playerOne.getBoundsInParent().getCenterX() <= mapPlaces[i].tile.getX() + 32)
						&& (playerOne.getBoundsInParent().getCenterY() - 2 + 16 >= mapPlaces[i].tile.getY()
								&& playerOne.getBoundsInParent().getCenterY() - 2 + 16 <= mapPlaces[i].tile.getY()
										+ 32)) {
					System.out.println("Player is on case " + mapPlaces[i].pos + "from math");
					System.out.println("Player is on case " + playerOne.pos + "from player infos");
					if (mapPlaces[i].isWalkable) {
						playerOne.charachterAnimation.play();
						playerOne.charachterAnimation.setOffsetY(96);
						playerOne.moveY(-2);
						playerOne.pos = mapPlaces[i].pos;
					} else {
						playerOne.charachterAnimation.stop();
					}
					break;
				}
			}

		} else if (isPress(KeyCode.S)) {

			for (int i = 0; i < mapPlaces.length; i++) {
				if ((playerOne.getBoundsInParent().getCenterX() >= mapPlaces[i].tile.getX()
						&& playerOne.getBoundsInParent().getCenterX() <= mapPlaces[i].tile.getX() + 32)
						&& (playerOne.getBoundsInParent().getCenterY() + 2 + 16 >= mapPlaces[i].tile.getY()
								&& playerOne.getBoundsInParent().getCenterY() + 2 + 16 <= mapPlaces[i].tile.getY()
										+ 32)) {
					System.out.println("Player is on case " + mapPlaces[i].pos + "from math");
					System.out.println("Player is on case " + playerOne.pos + "from player infos");
					if (mapPlaces[i].isWalkable) {
						playerOne.charachterAnimation.play();
						playerOne.charachterAnimation.setOffsetY(0);
						playerOne.moveY(2);
						playerOne.pos = mapPlaces[i].pos;
					} else {
						playerOne.charachterAnimation.stop();
					}
					break;
				}
			}
		} else if (isPress(KeyCode.D)) {

			for (int i = 0; i < mapPlaces.length; i++) {

				if ((playerOne.getBoundsInParent().getCenterX() + 2 >= mapPlaces[i].tile.getX()
						&& playerOne.getBoundsInParent().getCenterX() + 2 <= mapPlaces[i].tile.getX() + 32)
						&& (playerOne.getBoundsInParent().getCenterY() + 16 >= mapPlaces[i].tile.getY()
								&& playerOne.getBoundsInParent().getCenterY() + 16 <= mapPlaces[i].tile.getY() + 32)) {
					System.out.println("Player is on case " + mapPlaces[i].pos + "from math");
					System.out.println("Player is on case " + playerOne.pos + "from player infos");
					if (mapPlaces[i].isWalkable) {
						playerOne.charachterAnimation.play();
						playerOne.charachterAnimation.setOffsetY(64);
						playerOne.moveX(2);
						playerOne.pos = mapPlaces[i].pos;
					} else {
						playerOne.charachterAnimation.stop();
					}
					break;
				}
			}

		} else if (isPress(KeyCode.Q)) {
			for (int i = 0; i < mapPlaces.length; i++) {
				if ((playerOne.getBoundsInParent().getCenterX() - 2 >= mapPlaces[i].tile.getX()
						&& playerOne.getBoundsInParent().getCenterX() - 2 <= mapPlaces[i].tile.getX() + 32)
						&& (playerOne.getBoundsInParent().getCenterY() + 16 >= mapPlaces[i].tile.getY()
								&& playerOne.getBoundsInParent().getCenterY() + 16 <= mapPlaces[i].tile.getY() + 32)) {
					System.out.println("Player is on case " + mapPlaces[i].pos + "from math");
					System.out.println("Player is on case " + playerOne.pos + "from player infos");
					if (mapPlaces[i].isWalkable) {
						playerOne.charachterAnimation.play();
						playerOne.charachterAnimation.setOffsetY(32);
						playerOne.pos = mapPlaces[i].pos;
						playerOne.moveX(-2);
					} else {
						playerOne.charachterAnimation.stop();
					}
					break;
				}
			}
		} else {
			playerOne.charachterAnimation.stop();
		}
	}

//	enemyMovement prend en charge les mouvements des ennemies
	public void enemyMovement(Tile[] mapPlaces) {

		for (int i = 0; i < mapPlaces.length; i++) {
			if ((onil.getBoundsInParent().getCenterX() >= mapPlaces[i].tile.getX()
					&& onil.getBoundsInParent().getCenterX() <= mapPlaces[i].tile.getX() + 32)
					&& (onil.getBoundsInParent().getCenterY() + 16 >= mapPlaces[i].tile.getY()
							&& onil.getBoundsInParent().getCenterY() + 16 <= mapPlaces[i].tile.getY() + 32)) {
				
				System.out.println(mapPlaces[i].pos);
				
				ArrayList<Integer> mouvementAllow = new ArrayList<>();
				if (mapPlaces[i - 1].isWalkable) {
					mouvementAllow.add(- 1);
				}
				if (mapPlaces[i + 1].isWalkable) {
					mouvementAllow.add(+ 1);
				}
				if (mapPlaces[i + 23].isWalkable) {
					mouvementAllow.add(+ 23);
				}
				if (mapPlaces[i - 23].isWalkable) {
					mouvementAllow.add(- 23);
				}
				
				int random = (int)(Math.random()*(mouvementAllow.size()));
				int mouvementToDo = mouvementAllow.get(random);
				
				if (mouvementToDo == 23 ){
					//onil.moveY(32);
					Timer t = new Timer();
				    TimerTask task = new TimerTask() {
				      int i=0;
				      public void run() {
				    	  onil.moveY(2);
				        if(i == 32) {
				        	isMouvement = false;
				        	t.cancel();
				        }
				        i += 2;
				      }
				    };
				    
				    isMouvement = true;
				    t.schedule(task, new Date(), 50);
					
//					TimerTask task = new TimerTask() {
//				        public void run() {
//				        System.out.println("mvt");
//				        int pos = 0;
//						do {
//							onil.moveY(2);
//							pos += 2;
//						} while (pos != 32);
//				        }
//				    };
//					Timer timer = new Timer();
//					timer.schedule(task, 0, 5000);
					
				}
				if (mouvementToDo == - 23 ){
					
//					TimerTask task = new TimerTask() {
//				        public void run() {
//				        System.out.println("mvt");
//				        int pos = 0;
//						do {
//							onil.moveY(- 2);
//							pos += 2;
//						} while (pos != 32);
//				        }
//				    };
//					Timer timer = new Timer();
//					timer.schedule(task, 0, 5000);
					
					Timer t = new Timer();
				    TimerTask task = new TimerTask() {
				      int i=0;
				      public void run() {
				    	  onil.moveY(- 2);
				        if(i == 32) {
				        	isMouvement = false;
				        	t.cancel();
				        }
				        i += 2;
				      }
				    };
				    
				    isMouvement = true;
				    t.schedule(task, new Date(), 50);
				}
				if (mouvementToDo == 1 ){
					//onil.moveX(32);
//					TimerTask task = new TimerTask() {
//				        public void run() {
//				        System.out.println("mvt");
//				        int pos = 0;
//						do {
//							onil.moveX(2);
//							pos += 2;
//						} while (pos != 32);
//				        }
//				    };
//					Timer timer = new Timer();
//					timer.schedule(task, 0, 5000);
					
					Timer t = new Timer();
				    TimerTask task = new TimerTask() {
				      int i=0;
				      public void run() {
				    	  onil.moveX(2);
				        if(i == 32) {
				        	isMouvement = false;
				        	t.cancel();
				        }
				        i += 2;
				      }
				    };
				    
				    isMouvement = true;
				    t.schedule(task, new Date(), 50);
					
				}
				if (mouvementToDo == - 1 ){
					//onil.moveX(- 32);
//					TimerTask task = new TimerTask() {
//				        public void run() {
//				        System.out.println("mvt");
//				        int pos = 0;
//						do {
//							onil.moveX(-2);
//							pos += 2;
//						} while (pos != 32);
//					}
//				        
//				    };
//					Timer timer = new Timer();
//					timer.schedule(task, 0, 5000);
					
					Timer t = new Timer();
				    TimerTask task = new TimerTask() {
				      int i=0;
				      public void run() {
				    	  onil.moveX(- 2);
				        if(i == 32) {
				        	isMouvement = false;
				        	t.cancel();
				        }
				        i += 2;
				      }
				    };
				    
				    isMouvement = true;
				    t.schedule(task, new Date(), 50);
			}
	
							
				break;
				}
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
						mapPlaces[i + 1].setStyle("1");
					}

					if (mapPlaces[i - 1].isBreakable) {
						mapPlaces[i - 1].setStyle("1");
					}

					if (mapPlaces[i + 23].isBreakable) {
						mapPlaces[i + 23].setStyle("1");
					}

					if (mapPlaces[i - 23].isBreakable) {
						mapPlaces[i - 23].setStyle("1");
					}

					if (playerOne.pos == mapPlaces[i].pos || playerOne.pos == mapPlaces[i + 1].pos
							|| playerOne.pos == mapPlaces[i - 1].pos || playerOne.pos == mapPlaces[i + 23].pos
							|| playerOne.pos == mapPlaces[i - 23].pos) {
//						Handle death here
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