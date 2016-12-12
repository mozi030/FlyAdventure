package com.flyadventure.flyadventure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by user on 16/10/12.
 * implement Game Controller here
 */

public class GameController {
    private int width;
    private int height;

    GameView gameview;

    public Character character;
    public List<Obstacle> obstacleList;
    public List<Floor> floorList;
    public Scene scene;

    private TimerTask task;
    private Timer timer;

    Context context;

    private static GameController instance = new GameController();

    public static GameController getInstance() {
        return instance;
    }

    public void initGame(Context context) {
        this.context = context;
        this.obstacleList = new ArrayList<Obstacle>();
        this.floorList = new ArrayList<Floor>();

        Drawable mapDrawable = ContextCompat.getDrawable(context, R.drawable.level2map);
        Drawable obstaclesDrawable = ContextCompat.getDrawable(context, R.drawable.obstacle_5);
        //Drawable characterDrawable = ContextCompat.getDrawable(getContext(), R.drawable.character_1);

        //use context, inital the drawable in character class
        this.character = new Character(context);
        this.scene = new Scene(mapDrawable);
        this.obstacleList.add(new Obstacle(obstaclesDrawable));

        // initialize floor list
        double offset = 3;

        // Updated floor
        // double mapWidth = 640;
        // double mapHeight = 1101;
        this.floorList.add(new Floor(9, 0 + offset, 0, 100));
        this.floorList.add(new Floor(23, 0 + offset, 14, 58));
        this.floorList.add(new Floor(59, 0 + offset, 10, 17));
        this.floorList.add(new Floor(39, 0 + offset, 24, 32));
        this.floorList.add(new Floor(16, 0 + offset, 86, 100));
        this.floorList.add(new Floor(50, 0 + offset, 86, 100));
        this.floorList.add(new Floor(65, 0 + offset, 94, 98));

        // define a timer to add obstacles periodically
        task = new TimerTask() {
            @Override
            public void run() {
                addObstacle();
            }
        };

        // set the timer
        timer = new Timer(true);
        timer.schedule(task, 2000, 2000);

        // set the floor list of the game controller
        character.setFloorList(floorList);
    }

    public void startGame(GameView view) {
        gameview = view;
        //view.startGame();
        gameview.setGameViewListener(new GameViewListener());
    }

    public void addObstacle() {
        Drawable obstaclesDrawable = ContextCompat.getDrawable(context, R.drawable.obstacle_5);
        this.obstacleList.add(new Obstacle(obstaclesDrawable));
    }

    // detect whether the obstacles reach the box,
    // if the obstacle touches the walls,
    // destory it
    public void cleanObstacles() {
        if (this.obstacleList.size() == 0)
            return;

        if (this.obstacleList.get(0).x < 0) {
            this.obstacleList.remove(0);
        }
    }

    // collision detection
    public boolean collisionDetection() {
        int x11 = character.x + character.width / 4;
        int y11 = character.y + character.height / 4;
        int x12 = character.x + character.width * 3 / 4;
        int y12 = character.y + character.height * 3 / 4;

        for (Obstacle obs : this.obstacleList) {
            int x21 = obs.x + obs.width / 4;
            int y21 = obs.y + obs.height / 4;
            int x22 = obs.x + obs.width * 3 / 4;
            int y22 = obs.y + obs.height * 3 / 4;

            if (x12 < x21 || x11 > x22) {
                continue;
            } else if (y12 < y21 || y11 > y22) {
                continue;
            } else
                return true;
        }

        return false;
    }

    public void setFloorList(List<Floor> floorList) {
        this.character.setFloorList(floorList);
    }

    //listen GameView update
    private class GameViewListener implements GameView.GameViewListener {
        @Override
        public void OnGameUpdate() {
            scene.update();
            character.update();

            // clean the obstacle touches the wall
            cleanObstacles();

            for (Obstacle obs : GameController.getInstance().obstacleList) {
                obs.move();
            }

            // detect whether the obstacle is hitted
            if (collisionDetection()) {
                character.isDead = true;
                if (mGameControllerListener != null)
                    mGameControllerListener.OnGameOver();
            }

            //rebirth character
            if (character.isDead == true && character.x > 130) {
                character.isDead = false;
                character.init();
                obstacleList.clear();

                if (mGameControllerListener != null) {
                    mGameControllerListener.OnGameRestart();
                }
            }
        }
    }

    /* Game view listener interface */
    public interface GameControllerListener {
        public void OnGameOver();

        public void OnGameRestart();
    }

    private GameControllerListener mGameControllerListener = null;

    public void setGameControllerListener(GameControllerListener listener) {
        mGameControllerListener = listener;
    }
}
