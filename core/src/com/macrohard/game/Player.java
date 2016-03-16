package com.macrohard.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 16/3/16.
 */
public class Player extends GameObject {
    private String power;
    public Player(){
        super();
        this.setImage(new Texture(Gdx.files.internal("player_temp.png")));
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPower() {
        return power;
    }
}