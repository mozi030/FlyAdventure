package com.flyadventure.flyadventure;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.v4.content.ContextCompat;

/**
 * TODO: document your custom view class.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private DrawViewThread drawThread;

    private  GameController gameController;
    private Character character;
    private Scene scene;

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        //Resources res = getResources();
        Drawable mapDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map);
        Drawable obstaclesDrawable = ContextCompat.getDrawable(getContext(), R.drawable.obstacle);
        Drawable characterDrawable = ContextCompat.getDrawable(getContext(), R.drawable.character);

        this.character = new Character(characterDrawable);
        this.scene = new Scene(mapDrawable, obstaclesDrawable);
        this.gameController = new GameController(character, scene);

        holder = this.getHolder();
        holder.addCallback(this);
        drawThread = new DrawViewThread(this.holder, this.gameController);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = width - paddingLeft - paddingRight;
        int contentHeight = height - paddingTop - paddingBottom;

        gameController.setGameSize(contentWidth, contentHeight);
    }

    public void surfaceCreated(SurfaceHolder holder){
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        gameController.setGameSize(contentWidth, contentHeight);

        drawThread.isRunning = true;
        drawThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.isRunning = false;
    }

    public class DrawViewThread extends Thread {
        private SurfaceHolder holder;
        private GameController gameController;
        boolean isRunning;

        public  DrawViewThread(SurfaceHolder holder, GameController controller)
        {
            this.holder = holder;
            this.gameController = controller;
            isRunning = true;
        }

        public void run(){
            Canvas canvas = null;

            while(isRunning) {
                try {
                    canvas = holder.lockCanvas();

                    //draw game
                    gameController.update(canvas);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(canvas!= null) {
                    holder.unlockCanvasAndPost(canvas);
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}