package com.flyadventure.flyadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 16/10/12.
 */
public class Character {
    public int Xspeed;
    public int Yspeed;
    public int direction;
    public int x;
    public int y;
    public int width;
    public int height;
    public List<Floor> floorList;
    public Context context;

    // character bitmap
    public Bitmap characterBitmap;
    public int bitmapStatus;

    public boolean isDead;
    //public Matrix bitmapMatrix;

    Character(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        x = 2;
        y = 0;
        width = 12;
        height = 7;
        Xspeed = 0;
        Yspeed = 4;
        direction = 0;

        bitmapStatus = 0;
        isDead = false;
        setCharacterBitmap();
    }

    public void setFloorList(List<Floor> floorList) {
        this.floorList = floorList;
    }

    public void jump() {
        Xspeed = 8;
    }

    /* update the positon of character each frame */
    public void update() {
        //dead update
        if (isDead) {
            bitmapStatus = 3; //dead character bitmap
            setCharacterBitmap();
            x += 3;
            return;
        }

        // character is no dead,  update its position
        int xStand = 0;
        for (Floor floor:floorList) {
            if (floor.xMin <= x && floor.xMin > xStand) {
                if (y + width >= floor.yMin && y <= floor.yMax)  xStand = floor.xMin;
            }
        }

        x += Xspeed;
        y += direction * Yspeed;
        if (x < xStand) x = xStand;
        if (x > 110) x = 110;
        if (y < 0) y = 0;
        if (y > 93) y = 93;

        // shrink the Yspeed && Xspeed
        //if (Yspeed > 0) Yspeed--;
        if (Xspeed > -4) Xspeed -= 2;

        // update bitmap status
        if (Yspeed != 0)
            bitmapStatus = (bitmapStatus+1)%3;

        setCharacterBitmap();
    }

    /* handle move event */
    public void move() {
        Yspeed = 4;
        //Log.d("debug", "move");
    }

    /* handle right event */
    public void right() {
        direction = 1;
        //Log.d("debug", "right");
    }

    /* handle left event */
    public void left() {
        direction = -1;
        //Log.d("debug", "left");
    }

    /* character dead */
    public void dead() {
        isDead = true;
    }

    public void setCharacterBitmap() {
        switch (bitmapStatus) {
            case 0:
                this.characterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.character_0);
                break;
            case 1:
                this.characterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.character_1);
                break;
            case 2:
                this.characterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.character_2);
                break;
            case 3:
                this.characterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.death_2);
                break;
            default:
                this.characterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.character_0);
                break;
        }

        //flip the bitmap
        if (direction == -1) {
            Matrix matrix = new Matrix();
            matrix.postScale(1,-1);
            characterBitmap = Bitmap.createBitmap(characterBitmap, 0, 0,
                    characterBitmap.getWidth(), characterBitmap.getHeight(), matrix, true);
        }
    }

}
