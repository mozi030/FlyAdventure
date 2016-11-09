package com.flyadventure.flyadventure;

import android.graphics.drawable.Drawable;

import java.util.Random;

/**
 * Created by arrow on 11/7/16.
 */

public class Obstacle {
    public int speed = -4;
    public int x;
    public int y;
    public int height;
    public int width;
    public Drawable obstacleDrawable;


    Obstacle(Drawable cDrawable) {
        this.obstacleDrawable = cDrawable;
        Random random = new Random();
        x = random.nextInt(98);
        y = 100;
//        obstacleDrawable.setBounds(x, y, x+width, y+height);
    }

    public void move() {
        y += speed;
    }

}
