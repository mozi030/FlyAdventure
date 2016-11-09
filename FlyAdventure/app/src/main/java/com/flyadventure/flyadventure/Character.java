package com.flyadventure.flyadventure;

import android.graphics.drawable.Drawable;

/**
 * Created by user on 16/10/12.
 */
public class Character {
//    public float Yspeed = 10;
    public float Xspeed;
//    public float jumpHeight = 8;
    public int x;
    public int y;
    public int width;
    public int height;
    public Drawable characterDrawable;

    Character(Drawable cDrawable) {
        this.characterDrawable = cDrawable;
        x = 0;
        y = 10;
        //characterDrawable.setBounds(x, y, x+width, y+height);
    }

    public void move() {
        if (Xspeed > -4) Xspeed -= 2;
        x += Xspeed;

        if (x <= 0) x = 0;
    }

    public void jump() {
        if (x < 98) Xspeed = 8;
    }
}
