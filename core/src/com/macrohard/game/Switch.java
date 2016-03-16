package com.macrohard.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 16/3/16.
 */
public class Switch extends Obstacle {
    public Switch(){
        super();
        this.setImage(new Texture(Gdx.files.internal("switch_off.png")));
    }
}