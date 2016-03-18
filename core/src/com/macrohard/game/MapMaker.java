package com.macrohard.game;

/**
 * Created by hj on 18/3/16.
 */
public class MapMaker extends Thread {

    ThisGame game;
    MapMaker(ThisGame game){
        this.game = game;
        game.wallCoord();
        game.createBg();
        game.createObstacle(game.path);
        game.createSides();
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()){
                break;
            }
            if (game.running) {
                game.wallCoord();
            }
        }
    }
}
