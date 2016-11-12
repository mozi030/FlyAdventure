package com.flyadventure.flyadventure;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 16/10/12.
 */
public class Character {
    public int Xspeed;
    public int Yspeed;
    public int x;
    public int y;
    public int width;
    public int height;
    public List<Floor> floorList;
    public Drawable characterDrawable;

    Character(Drawable cDrawable) {
        setCharacterDrawable(cDrawable);

        x = 50;
        y = 70;
        width = 8;
        height = 15;
        Xspeed = 0;
        Yspeed = 0;
    }

    public void setFloorList(List<Floor> floorList) {
        this.floorList = floorList;
    }

    public void fly() {
        int xStand = 0;
        for (Floor floor:floorList) {
            if (floor.xMin <= x && floor.xMin > xStand) {
                if (y + width >= floor.yMin && y <= floor.yMax)  xStand = floor.xMin;
            }

        }

        x += Xspeed;
        if (Xspeed > -4) Xspeed -= 2;

        if (x < xStand) x = xStand;
    }

    public void jump() {
        if (x < 98) Xspeed = 8;
    }

    public void move() {
        y += Yspeed;

        // shrink the Yspeed
        if (Yspeed != 0) {
            if (Yspeed > 0) Yspeed--;
            else Yspeed++;
        }
    }

    public void right() {
        Yspeed = 2;
    }

    public void left() {
        Yspeed = -2;
    }

    public void setCharacterDrawable(Drawable cDrawable) {
        this.characterDrawable = cDrawable;
    }
}
