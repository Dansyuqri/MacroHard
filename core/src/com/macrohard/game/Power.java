package com.macrohard.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 16/3/16.
 */
public class Power extends GameObject {
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