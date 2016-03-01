package com.macrohard.game;


import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class SomeGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture wallImage;
	private Texture playerImage;
	private Texture joystickImage;
	private Sound dropSound;
	private Music rainMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle player;
	private Rectangle joystick;
	private Array<Rectangle> obstacles;
	private Array<Rectangle> sideWalls;
	private long lastDropTime;
	boolean[] path;
	boolean[] current = {false, false, false, false, false, false, false};

	@Override
	public void create() {

		//TODO: For later development declare all constants first instead of using them directly
		//TODO: For later development try to have an object hierarchy and place things like their images in private fields (Minh/Syuqri)
		//TODO: For later development also separate certain methods into different threads, e.g. maybe rendering and spawning obstacles can have individual threads (Syuqri)

		// load the images for the droplet and the player, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("wall1.1.png"));
		wallImage = new Texture(Gdx.files.internal("wall1.2.png"));
		playerImage = new Texture(Gdx.files.internal("player.png"));
		joystickImage = new Texture(Gdx.files.internal("joystick.png"));

		// load the drop sound effect and the rain background "music"
//		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
//		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// start the playback of the background music immediately
//		rainMusic.setLooping(true);
//		rainMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the player
		player = new Rectangle();
		player.x = 480 / 2 - 64 / 2; // center the player horizontally
		player.y = 400; // bottom left corner of the player is 20 pixels above the bottom screen edge
		player.width = 64;
		player.height = 64;

		// create joystick
		joystick = new Rectangle();
		joystick.x = 350;
		joystick.y = 30;
		joystick.height = 100;
		joystick.width = 100;

		// create the obstacles array and spawn the first raindrop
		obstacles = new Array<Rectangle>();
		sideWalls = new Array<Rectangle>();
		boolean[] temp = {true, true, true, true, true, true, true};
		wallCoord(temp);
		spawnObstacle(current);
		spawnSides();
	}

	private void wallCoord(boolean[] pathin){
		boolean test = false;
		int out_index = 0;

		while (!test) {
			int temp = MathUtils.random(0, 5);
			for (int i = 0; i < temp; i++) {
				int coord = MathUtils.random(0,6);
				current[coord] = true;
			}
			for (int i = 0; i < current.length; i++){
				if (current[i] && pathin[i]){
					test = true;
					break;
				}
			}
		}
		for (int k = 0; k < current.length; k++) {
			if (current[k] && pathin[k]) {
				pathin[k] = true;
				out_index = k;
			} else {
				pathin[k] = false;
			}
		}
		for (int j = 1; j < current.length; j++) {
			if (out_index + j < current.length) {
				if (current[out_index + j] && pathin[out_index + j - 1]) {
					pathin[out_index + j] = true;
				}
			}
			if (out_index - j >= 0) {
				if (current[out_index - j] && pathin[out_index - j + 1]) {
					pathin[out_index - j] = true;
				}
				else{
					break;
				}
			}
		}
		path = pathin;
	}

	private void spawnObstacle(boolean[] map) {
		for (int i = 0; i < map.length; i++) {
			if (!map[i]) {
				Rectangle obstacle = new Rectangle();
			obstacle.x = (64 * i) + 16;
			obstacle.y = 800;
			obstacle.width = 64;
			obstacle.height = 64;
			obstacles.add(obstacle);
		}
		current[i] = false;
	}
	lastDropTime = TimeUtils.nanoTime();
}

	private void spawnSides(){
		for (int i = 0; i < 2; i++) {
			Rectangle sideWall = new Rectangle();
			sideWall.x = (464*i);
			sideWall.y = 800;
			sideWall.width = 16;
			sideWall.height = 64;
			sideWalls.add(sideWall);
		}
	}

	@Override
	public void render() {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the player and
		// all drops
		batch.begin();
		batch.draw(playerImage, player.x, player.y);
		for(Rectangle obstacle: obstacles) {
			batch.draw(dropImage, obstacle.x, obstacle.y);
		}
		for(Rectangle side: sideWalls){
			batch.draw(wallImage, side.x, side.y);
		}
		batch.draw(joystickImage, joystick.x, joystick.y);
		batch.end();

		// process user input
		processInput();

		// make sure the player stays within the screen bounds

		collisionCheck();

//		if(player.x < 0) player.x = 0;
//		if(player.x > 480 - 64) player.x = 480 - 64;

		// check if we need to create a new raindrop
		//TODO: Implement alternate checking mechanism (Sam)
		if(TimeUtils.nanoTime() - lastDropTime > 320000000) {
			wallCoord(path);
			spawnObstacle(current);
			spawnSides();
		}


		// move the obstacles, remove any that are beneath the bottom edge of
		// the screen or that hit the player. In the later case we play back
		// a sound effect as well.

		player.y -= 200*Gdx.graphics.getDeltaTime();

		Iterator<Rectangle> iter = obstacles.iterator();
		Iterator<Rectangle> iter2 = sideWalls.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200*Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
//			if(raindrop.overlaps(player)) {
//				dropSound.play();
//				iter.remove();
//			}
		}
		while(iter2.hasNext()) {
			Rectangle side = iter2.next();
			side.y -= 200*Gdx.graphics.getDeltaTime();
			if(side.y + 64 < 0) iter2.remove();
		}
	}

	private void processInput() {
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			// check if touch is within joystick hitbox with buffer

			float relativex = touchPos.x - (joystick.x + joystick.width/2);
			float relativey = touchPos.y - (joystick.y + joystick.height/2);
			if (Math.abs(relativex) < joystick.width
					&& (Math.abs(relativey) < joystick.height)){
				if (relativex > 0 && relativex > relativey){
					moveRight();
					return;
				}

				if (relativex < 0 && relativex < relativey){
					moveLeft();
					return;
				}

				if (relativey > 0 && relativey > relativex){
					moveUp();
					return;
				}

				if (relativey < 0 && relativey < relativex){
					moveDown();
					return;
				}
			}
		}
	}

	private void collisionCheck(){
		//TODO: Check for collisions and handle them (Minh)
	}

	private void moveUp(){
		player.y += 200*Gdx.graphics.getDeltaTime();
	}

	private void moveDown(){
		player.y -= 200*Gdx.graphics.getDeltaTime();
	}

	private void moveRight(){
		player.x += 200*Gdx.graphics.getDeltaTime();
	}

	private void moveLeft(){
		player.x -= 200*Gdx.graphics.getDeltaTime();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		dropImage.dispose();
		wallImage.dispose();
		playerImage.dispose();
		joystickImage.dispose();
//		dropSound.dispose();
//		rainMusic.dispose();
		batch.dispose();
	}
}
