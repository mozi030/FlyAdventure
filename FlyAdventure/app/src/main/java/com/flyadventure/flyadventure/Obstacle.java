package com.flyadventure.flyadventure;

import android.graphics.drawable.Drawable;

import java.util.Random;

/**
 * Created by arrow on 11/7/16.
 */

public class Obstacle {
    public int speed;
    public int x;
    public int y;
    public int height;
    public int width;
    public Drawable obstacleDrawable;

    Obstacle(Drawable cDrawable) {
        this.obstacleDrawable = cDrawable;
        Random random = new Random();
        speed = -2;

        x = 100;
        y = random.nextInt(98);
        width = 7;
        height = 6;
    }

    public void move() {
        x += speed;
    }

}
