package com.macrohard.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 16/3/16.
 */
public class Obstacle extends GameObject {
    public Obstacle(){
        super();
        this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
    }
}
