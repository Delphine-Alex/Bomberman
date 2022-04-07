package com.ynov.bomberman.stage;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Tile {
	public Rectangle tile;

	public boolean isBreakable;

	public int pos;

	public Tile(Rectangle tile, String tileID, int pos) {
		this.tile = tile;
		switch (tileID) {
		case "0":
			Image wall = new Image("/Wall.png");
			tile.setFill(new ImagePattern(wall));
			this.isBreakable = false;
			break;
		case "1":
			Image grass = new Image("/Grass.png");
			tile.setFill(new ImagePattern(grass));
			this.isBreakable = false;
			break;
		case "2":
			Image brick = new Image("/Brick.png");
			tile.setFill(new ImagePattern(brick));
			this.isBreakable = true;
			break;
		default:
			this.isBreakable = false;
			break;
		}
		this.pos = pos;
	}
}
