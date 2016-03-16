package com.macrohard.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 16/3/16.
 */
public class SideWall extends GameObject {
    public SideWall(){
        super();
        this.setImage(new Texture(Gdx.files.internal("wall4.2.png")));
    }
}