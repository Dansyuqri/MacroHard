package com.macrohard.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;
import com.badlogic.gdx.Screen;

/**
 * Created by Syuqri on 3/2/2016.
 */
public class MainMenu extends ApplicationAdapter{
    private Texture playButtonImage;
    private Rectangle playButton;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    Game game;

    public void create() {
        playButtonImage = new Texture(Gdx.files.internal("wall1.1.png"));
        playButton = new Rectangle();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
        batch = new SpriteBatch();
        playButton.x = 480 / 2 - 64 / 2;
        playButton.y = 40;
        playButton.width = 64;
        playButton.height = 64;
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
        batch.draw(playButtonImage, playButton.x, playButton.y);
/*        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            // check if touch is within joystick hitbox with buffer
            if (touchPos.x < 480 / 2 + 64 / 2  && touchPos.x > 480 / 2 - 64 / 2){
                if(touchPos.y > 40 && touchPos.y<104){

                    dispose();
                }
            }
        }*/

        batch.end();

    }
/*    @Override
    public void dispose() {
    }

    @Override
    public void show() {
        //game.setScreen(new SomeGame);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void hide() {

    }*/
}
