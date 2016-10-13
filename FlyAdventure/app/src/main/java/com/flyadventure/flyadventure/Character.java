package com.flyadventure.flyadventure;

import android.graphics.drawable.Drawable;

/**
 * Created by user on 16/10/12.
 */
public class Character {
    public float speed = 10;
    public int x;
    public int y;
    public int width;
    public int height;
    public Drawable characterDrawable;

    Character(Drawable cDrawable) {
        this.characterDrawable = cDrawable;
        x = 0;
        characterDrawable.setBounds(x, y, x+width, y+height);
    }

    public void move() {
        x = (x+1)%100;
    }
}
