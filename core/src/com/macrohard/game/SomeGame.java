package com.macrohard.game;


import java.util.Arrays;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Array<Rectangle> sideWalls;
	private long lastDropTime;
	boolean[] path;
	boolean[] current = {false, false, false, false, false, false, false};

	//dfgsfgd

	@Override
	public void create() {
		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("wall1.1.png"));
		wallImage = new Texture(Gdx.files.internal("wall1.2.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

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

		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.x = 480 / 2 - 64 / 2; // center the bucket horizontally
		bucket.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		bucket.width = 64;
		bucket.height = 64;

		// create the raindrops array and spawn the first raindrop
		raindrops = new Array<Rectangle>();
		sideWalls = new Array<Rectangle>();
		boolean[] temp = {true, true, true, true, true, true, true};
		wallCoord(temp);
		spawnRaindrop(current);
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

	private void spawnRaindrop(boolean[] map) {
		for (int i = 0; i < map.length; i++) {
			if (!map[i]) {
				Rectangle raindrop = new Rectangle();
			raindrop.x = (64 * i) + 16;
			raindrop.y = 800;
			raindrop.width = 64;
			raindrop.height = 64;
			raindrops.add(raindrop);
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

		// begin a new batch and draw the bucket and
		// all drops
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		for(Rectangle side: sideWalls){
			batch.draw(wallImage, side.x, side.y);
		}
		batch.end();

		// process user input
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		// make sure the bucket stays within the screen bounds
		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 480 - 64) bucket.x = 480 - 64;

		// check if we need to create a new raindrop
		if(TimeUtils.nanoTime() - lastDropTime > 320000000) {
			wallCoord(path);
			spawnRaindrop(current);
			spawnSides();
		}


		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we play back
		// a sound effect as well.
		Iterator<Rectangle> iter = raindrops.iterator();
		Iterator<Rectangle> iter2 = sideWalls.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200*Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
//			if(raindrop.overlaps(bucket)) {
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

	@Override
	public void dispose() {
		// dispose of all the native resources
		dropImage.dispose();
		wallImage.dispose();
		bucketImage.dispose();
//		dropSound.dispose();
//		rainMusic.dispose();
		batch.dispose();
	}
}
