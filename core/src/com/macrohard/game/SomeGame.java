package com.macrohard.game;


import java.util.ArrayList;
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
import com.badlogic.gdx.utils.TimeUtils;

public class SomeGame extends ApplicationAdapter {
	//TODO: spawn a power using these arrays (Minh)
	private final String[] TYPES_OF_POWER = {"slowGameDown","fewerObstacles","speedPlayerUp","dangerZoneHigher"};
	private Texture joystickImage;
	private Texture joystickCentreImage;
	private Sound dropSound;
	private Music rainMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Player player;
	private Rectangle joystick;
	private Rectangle joystickCentre;
	private ArrayList<Obstacle> obstacles;
	private ArrayList<SideWall> sideWalls;
	private ArrayList<Switch> switches;
	private ArrayList<Barrier> barriers;
	private ArrayList<Power> powers;
	private long lastDropTime, endPowerTime;
	private boolean touchHeld = false;
	private int gameSpeed, speedIncrement, playerSpeed, dangerZone;
	boolean[] path;
	boolean[] current = {false, false, false, false, false, false, false, false, false};

	@Override
	public void create() {

		//TODO: For later development declare all constants first instead of using them directly
		//TODO: For later development try to have an object hierarchy and place things like their images in private fields (Minh/Syuqri)
		//TODO: For later development also separate certain methods into different threads, e.g. maybe rendering and spawning obstacles can have individual threads (Syuqri)

		// load the images for the droplet and the player, 40x40 pixels each
		joystickImage = new Texture(Gdx.files.internal("joystick.png"));
		joystickCentreImage = new Texture(Gdx.files.internal("joystick_centre.png"));
		gameSpeed = 100;
		speedIncrement = 100;
		playerSpeed = 300;
		dangerZone = 400;
		endPowerTime = System.currentTimeMillis();
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
		player = new Player();
		player.x = 480 / 2 - 50 / 2; // center the player horizontally
		player.y = 400; // bottom left corner of the player is 400 pixels above the bottom screen edge
		player.width = 46;
		player.height = 46;

		// create joystick
		joystick = new Rectangle();
		joystick.height = 100;
		joystick.width = 100;

		joystickCentre = new Rectangle();
		joystickCentre.height = 21;
		joystickCentre.width = 21;

		// create the obstacles array and spawn the first raindrop
		obstacles = new ArrayList<Obstacle>();
		sideWalls = new ArrayList<SideWall>();
		barriers = new ArrayList<Barrier>();
		switches = new ArrayList<Switch>();
		powers = new ArrayList<Power>();
		boolean[] temp = {true, true, true, true, true, true, true, true, true};
		wallCoord(temp);
		spawnObstacle(current);
		spawnSides();
	}

	private void wallCoord(boolean[] pathin){
		boolean test = false;
		int out_index = 0;

		while (!test) {
			int temp = MathUtils.random(0, 8);
			for (int i = 0; i < temp; i++) {
				int coord = MathUtils.random(0,8);
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

	//TODO: spawn powers, Barriers + Switch (need check condition tgt), etc. (Minh)
	//TODO: make game speed increases method (Minh)

	private void spawnObstacle(boolean[] map) {
		for (int i = 0; i < map.length; i++) {
			if (!map[i]) {
				Obstacle obstacle = new Obstacle();
				obstacle.x = (50 * i) + 15;
				obstacle.y = 800;
				obstacle.width = 50;
				obstacle.height = 50;
				obstacles.add(obstacle);
			}
			current[i] = false;
		}
		lastDropTime = TimeUtils.nanoTime();
	}
	private void spawnPower(boolean[] map) {
		for (int i = 0; i < map.length; i++) {
			if (!map[i]) {
				Power power = new Power(TYPES_OF_POWER[(int)(Math.random()*TYPES_OF_POWER.length)]);
				power.x = (50 * i) + 15;
				power.y = 800;
				power.width = 50;
				power.height = 50;
				powers.add(power);
			}
			current[i] = false;
		}
		lastDropTime = TimeUtils.nanoTime();
	}

	private void spawnSides(){
		for (int i = 0; i < 2; i++) {
			SideWall sideWall = new SideWall();
			sideWall.x = (465*i);
			sideWall.y = 800;
			sideWall.width = 15;
			sideWall.height = 50;
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
		batch.draw(player.getImage(), player.x, player.y);
		for(Obstacle obstacle: obstacles) {
			batch.draw(obstacle.getImage(), obstacle.x, obstacle.y);
		}
		for(SideWall sideWall: sideWalls){
			batch.draw(sideWall.getImage(), sideWall.x, sideWall.y);
		}
		for (Power power:powers){
			batch.draw(power.getImage(), power.x, power.y);
		}
		for(Switch eachSwitch: switches) {
			batch.draw(eachSwitch.getImage(), eachSwitch.x, eachSwitch.y);
		}
		for(Barrier barrier: barriers){
			batch.draw(barrier.getImage(), barrier.x, barrier.y);
		}

		if (touchHeld) {
			batch.draw(joystickImage, joystick.x, joystick.y);
			batch.draw(joystickCentreImage, joystickCentre.x, joystickCentre.y);
		}
		batch.end();

		// process user input

		processInput();
//		processInputTilt();
		if (barriers.size() != 0) {
			removeBarriers();
		}
//		constantly check if any power/DangerZone's effect still lingers
		effectPower();
		notifyDangerZone();
//		effectDangerZone();

		// check if we need to create a new raindrop
		//TODO: Implement alternate checking mechanism (Sam)
		if(TimeUtils.nanoTime() - lastDropTime > 500000000) {
			wallCoord(path);
			spawnObstacle(current);
			spawnSides();
		}


		// move the obstacles, remove any that are beneath the bottom edge of
		// the screen or that hit the player. In the later case we play back
		// a sound effect as well.

		player.y -= 100*Gdx.graphics.getDeltaTime();

		Iterator<Obstacle> iter = obstacles.iterator();
		Iterator<SideWall> iter2 = sideWalls.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= gameSpeed*Gdx.graphics.getDeltaTime();
			if(raindrop.y + 50 < 0) iter.remove();
//			if(raindrop.overlaps(player)) {
//				dropSound.play();
//				iter.remove();
//			}
		}
		while(iter2.hasNext()) {
			Rectangle side = iter2.next();
			side.y -= gameSpeed*Gdx.graphics.getDeltaTime();
			if(side.y + 50 < 0) iter2.remove();
		}
	}

	private void processInputTilt(){
		float x = Gdx.input.getRoll();
		float y = Gdx.input.getPitch();
		float incx = x * Math.abs(x);
		float incy = y * Math.abs(y);
		if (incx > playerSpeed) incx = playerSpeed;
		if (incx < -playerSpeed) incx = -playerSpeed;
		if (incy > playerSpeed) incy = playerSpeed;
		if (incy < -playerSpeed) incy = -playerSpeed;
		player.x += incx * Gdx.graphics.getDeltaTime();
		player.y += incy * Gdx.graphics.getDeltaTime();
	}

	private void processInput() {
		float relativex = 0;
		float relativey = 0;
		Vector3 touchPos = new Vector3();
		if (Gdx.input.isTouched()) {
			touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			relativex = touchPos.x - (joystick.x + joystick.width/2);
			relativey = touchPos.y - (joystick.y + joystick.height/2);
			if (!touchHeld) {
				joystick.x = touchPos.x - joystick.width/2;
				joystick.y = touchPos.y - joystick.height/2;
				joystickCentre.x = touchPos.x - joystickCentre.width/2;
				joystickCentre.y = touchPos.y - joystickCentre.height/2;
				touchHeld = true;
			}
		} else {
			touchHeld = false;
		}

		if (touchHeld) {
			// check if touch is within joystick hitbox with buffer
			float angle = (float) Math.atan2(relativey, relativex);
			float cos = (float) Math.cos(angle);
			float sin = (float) Math.sin(angle);
			if (Math.abs(relativex) < joystick.width/2
					&& (Math.abs(relativey) < joystick.height/2)) {
				joystickCentre.x = touchPos.x - joystickCentre.width/2;
				joystickCentre.y = touchPos.y - joystickCentre.height/2;
			} else {
				joystickCentre.x = cos * joystick.width/2 + joystick.x + joystick.width/2 - joystickCentre.width/2;
				joystickCentre.y = sin * joystick.width/2 + joystick.y + joystick.height/2 - joystickCentre.height/2;
			}

			omniMove(cos, sin);
		}
	}
	boolean setPowerLock = true;
	private void effectPower(){
		if (System.currentTimeMillis() > endPowerTime) {
			setPowerLock = true;
		}
		if (System.currentTimeMillis() <= endPowerTime){
			if (setPowerLock) {
				endPowerTime = System.currentTimeMillis() + 5000;
				setPowerLock = false;
			}
			if (player.getPower().equals("slowGameDown")) {
				gameSpeed -= speedIncrement;
			} else if (player.getPower().equals("fewerObstacles")) {

			} else if (player.getPower().equals("speedPlayerUp")) {
				playerSpeed += speedIncrement;
			} else if (player.getPower().equals("dangerZoneHigher")) {
				dangerZone += 50;
			}
		}
	}

	private void notifyDangerZone(){
		if (player.y < dangerZone) {
			//notify server
		}
	}

	private void effectDangerZone(){
		// if notified by server
		gameSpeed += speedIncrement;
	}

	private void removeBarriers(){
//		TODO: if notified by server (Ryan)
		barriers.clear();
	}

	private boolean collisionCheck(){
		if (player.x > 465 - player.width ){
			player.x = 465 - player.width;
		}
		if (player.x < 15){
			player.x = 15;
		}
//		collide with normal wall obstacle
		for (Obstacle obstacle: obstacles) {
			if (player.overlaps(obstacle)){
				return true;
			}
		}
//		collide with barriers
		for (Barrier barrier: barriers) {
			if (player.overlaps(barrier)){
				return true;
			}
		}
//		collide with switch
		for (Switch eachSwitch:switches){
			if (player.overlaps(eachSwitch)){
				// change this to another different switch image
				eachSwitch.setImage(new Texture(Gdx.files.internal("joystick_centre.png")));
				// then notify server
			}
		}
//		collide with power up
		for (Power power:powers){
			if (player.overlaps(power)){
				player.setPower(power.getType());
				powers.remove(power);
				// then notify server
			}
		}
		return false;
	}

	private void omniMove(float x, float y){
		float prevx = player.x;
		float prevy = player.y;
		player.x += x * playerSpeed * Gdx.graphics.getDeltaTime();
		if (collisionCheck()){
			player.x = prevx;
		}
		player.y += y * playerSpeed * Gdx.graphics.getDeltaTime();
		if (collisionCheck()){
			player.y = prevy;
		}
	}

	private void orthoMove(float relativex, float relativey){
		if (relativex > 0 && Math.abs(relativex) > Math.abs(relativey)){
			moveRight();
			return;
		}

		if (relativex < 0 && Math.abs(relativex) > Math.abs(relativey)){
			moveLeft();
			return;
		}

		if (relativey > 0 && Math.abs(relativex) < Math.abs(relativey)){
			moveUp();
			return;
		}

		if (relativey < 0 && Math.abs(relativex) < Math.abs(relativey)){
			moveDown();
			return;
		}
	}

	private void moveUp(){
		player.y += playerSpeed*Gdx.graphics.getDeltaTime();
	}

	private void moveDown(){
		player.y -= playerSpeed*Gdx.graphics.getDeltaTime();
	}

	private void moveRight(){
		player.x += playerSpeed*Gdx.graphics.getDeltaTime();
	}

	private void moveLeft(){
		player.x -= playerSpeed*Gdx.graphics.getDeltaTime();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		for (Obstacle obstacle:obstacles) {
			obstacle.getImage().dispose();
		}
		for (Power power:powers) {
			power.getImage().dispose();
		}
		for (Barrier barrier:barriers) {
			barrier.getImage().dispose();
		}
		for (Switch eachSwitch:switches) {
			eachSwitch.getImage().dispose();
		}
		for (SideWall sideWall:sideWalls) {
			sideWall.getImage().dispose();
		}
		player.getImage().dispose();
		joystickImage.dispose();
		joystickCentreImage.dispose();
//		dropSound.dispose();
//		rainMusic.dispose();
		batch.dispose();
	}
}

class GameObject extends Rectangle {
	private Texture image;

	public Texture getImage() {
		return image;
	}
	public void setImage(Texture image) {
		this.image = image;
	}
}

class SideWall extends GameObject {
	public SideWall(){
		super();
		this.setImage(new Texture(Gdx.files.internal("wall4.2.png")));
	}
}

class Power extends GameObject {
	private String type;
	public Power(String type){
		super();
		this.setImage(new Texture(Gdx.files.internal("droplet.png")));
		this.type = type;
	}

	public String getType() {
		return type;
	}
}

class Obstacle extends GameObject {
	public Obstacle(){
		super();
		this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
	}
}

class Switch extends Obstacle {
	public Switch(){
		super();
		this.setImage(new Texture(Gdx.files.internal("bucket.png")));
	}
}

class Barrier extends Obstacle {
	public Barrier(){
		super();
		this.setImage(new Texture(Gdx.files.internal("bucket.png")));
	}
}

class Player extends GameObject {
	private String power;
	public Player(){
		super();
		this.setImage(new Texture(Gdx.files.internal("player_temp1.png")));
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getPower() {
		return power;
	}
}
