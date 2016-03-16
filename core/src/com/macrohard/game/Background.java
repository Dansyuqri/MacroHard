package com.macrohard.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 16/3/16.
 */
public class Background extends GameObject {
    public Background(){
        super();
        this.setImage(new Texture(Gdx.files.internal("bg.png")));
    }
}
