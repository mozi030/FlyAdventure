package com.flyadventure.flyadventure;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by user on 16/10/12.
 * implement Game Controller here
 */
public class GameController {
    private Character character;
    private Scene scene;
    private int width;
    private int height;

    GameController(Character c, Scene s) {
        this.character = c;
        this.scene = s;
    }

    //update game frame
    public boolean update(Canvas canvas) {
        scene.update();
        character.move();

        //detect whether the obstacle is hitted
        if (!collisionDetection()) {
            this.draw(canvas);
        } else {
            //end the game
        }

        return true;
    }

    //collision detection
    public boolean collisionDetection() {
        return false;
    }

    //draw game
    public void draw(Canvas canvas) {
        Drawable characterDrawable = this.character.characterDrawable;
        Drawable mapDrawable = this.scene.map;
        Drawable obstacleDrawable = this.scene.obstacle;


        //Log.d("debug", "draw");
        //obstacleDrawable.draw(canvas);
        mapDrawable.setBounds(0, 0, width, height);
        mapDrawable.draw(canvas);

        characterDrawable.setBounds(character.x*width/100, height/4, character.x*width/100+width/10, height/4 + height/5);
        characterDrawable.draw(canvas);
    }

    public void setGameSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
