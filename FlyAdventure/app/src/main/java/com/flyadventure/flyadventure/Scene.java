package com.flyadventure.flyadventure;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by user on 16/10/12.
 */
public class Scene {
    public Drawable map;
    public Drawable obstacle;
    public int offset;
    public int max_offset;

    Scene(Drawable map, Drawable obstacle) {
        this.map = map;
        this.obstacle = obstacle;
        offset = 0;
        max_offset = 100;
    }

    public void update() {
        offset = (offset +1)%max_offset;
    }
}
