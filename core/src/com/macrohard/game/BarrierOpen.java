package com.macrohard.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 16/3/16.
 */
public class BarrierOpen extends Obstacle{
    public BarrierOpen(){
        super();
        this.setImage(new Texture(Gdx.files.internal("gate_open.png")));
    }
}