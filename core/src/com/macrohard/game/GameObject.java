package com.macrohard.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by hj on 16/3/16.
 */
public class GameObject extends Rectangle {
    private Texture image;

    public Texture getImage() {
        return image;
    }
    public void setImage(Texture image) {
        this.image = image;
    }
}