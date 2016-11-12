package com.flyadventure.flyadventure;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 16/10/12.
 * implement Game Controller here
 */

public class GameController {
    private Scene scene;
    private Character character;
    private List<Obstacle> obstacleList;
    private int width;
    private int height;

    GameController(Character c, List<Obstacle> oblist, Scene s) {
        this.character = c;
        this.scene = s;
        this.obstacleList = oblist;
    }

    // update game frame
    public boolean update(Canvas canvas) {
        scene.update();
        character.fly();
        character.move();

        // if the direction change, reset the direction
        // ...

        // clean the obstacle touches the wall
        cleanObstacles();

        for (Obstacle obs:this.obstacleList) {
            obs.move();
        }

//        // detect whether the obstacle is hitted
//        if (!collisionDetection()) {
//            this.draw(canvas);
//        } else {
//            //end the game
//            Log.d("debug", "detect collision!");
//
//            // fail
//
//
//            this.draw(canvas);
//        }
        this.draw(canvas);

        return true;
    }

    // detect whether the obstacles reach the box,
    // if the obstacle touches the walls,
    // destory it
    public void cleanObstacles() {
        if (this.obstacleList.get(0).x == 0) {
            this.obstacleList.remove(0);
        }
    }

    // collision detection
    public boolean collisionDetection() {
        int x1 = character.x;
        int y1 = character.y;
        int w1 = character.width;
        int h1 = character.height;

        for (Obstacle obs:this.obstacleList) {
            int x2 = obs.x;
            int y2 = obs.y;
            int w2 = obs.width;
            int h2 = obs.height;

            if (x1 >= x2 && x1 >= x2 + w2) {
                // false
            } else if (x1 <= x2 && x1 + w1 <= x2) {
                // false
            } else if (y1 >= y2 && y1 >= y2 + h2) {
                // false
            } else if (y1 <= y2 && y1 + h1 <= y2) {
                // false
            } else {
                return true;
            }
        }

        return false;
    }

    //draw game
    public void draw(Canvas canvas) {
        Drawable mapDrawable = this.scene.map;
        Drawable characterDrawable = this.character.characterDrawable;

//        Log.d("debug", "draw");
        mapDrawable.setBounds(0, 0, width, height);
        mapDrawable.draw(canvas);


        for (Obstacle obs:this.obstacleList) {
            Drawable obstacleDrawable = obs.obstacleDrawable;
            obstacleDrawable.setBounds(obs.x * width / 100, obs.y * height / 100, obs.x * width / 100 + width / obs.width, obs.y * height / 100 + height / obs.height);
            obstacleDrawable.draw(canvas);
        }

        characterDrawable.setBounds(character.x * width / 100, character.y * height / 100, character.x * width / 100 + width / character.width, character.y * height / 100 + height / character.height);
        characterDrawable.draw(canvas);
    }

    public void setGameSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setFloorList(List<Floor> floorList) {
        this.character.setFloorList(floorList);
    }
}
