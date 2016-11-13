package com.flyadventure.flyadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO: document your custom view class.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private DrawViewThread drawThread;

    private int width;
    private int height;

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

    private void init(AttributeSet attrs, int defStyle) {}

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        this.width = width - paddingLeft - paddingRight;
        this.height = height - paddingTop - paddingBottom;
    }

    public void surfaceCreated(SurfaceHolder holder){
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        
        this.width = getWidth() - paddingLeft - paddingRight;
        this.height = getHeight() - paddingTop - paddingBottom;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.threadIsRunning = false;
    }

    public void startGame() {
        holder = this.getHolder();
        holder.addCallback(this);
        drawThread = new DrawViewThread(this);

        drawThread.threadIsRunning = true;
        drawThread.start();
    }

    public void stopGame() {
        if (drawThread != null) {
            drawThread.setRunning(false);
        }
    }

    public static class DrawViewThread extends Thread {
        private GameView view;
        private SurfaceHolder holder;
        boolean threadIsRunning;

        public DrawViewThread(GameView view)
        {
            this.view = view;
            this.holder = view.holder;
            threadIsRunning = true;
        }

        public void setRunning (boolean running){
            threadIsRunning = running;
        }

        public void run(){
            Canvas canvas = null;

            while(threadIsRunning) {
                try {
                    canvas = holder.lockCanvas();

                    //draw game
                    draw(canvas);
                    if (view.mGameViewListener != null) {
                        view.mGameViewListener.OnGameUpdate();
                    }

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

        public void draw(Canvas canvas) {
            int width = view.width;
            int height = view.height;

            Character character = GameController.getInstance().character;
            List<Obstacle> obstacleList = GameController.getInstance().obstacleList;

            Drawable mapDrawable = GameController.getInstance().scene.map;
            Bitmap characterBitmap = GameController.getInstance().character.characterBitmap;

            mapDrawable.setBounds(0, 0, view.width, view.height);
            mapDrawable.draw(canvas);

            float wDiv = width/100;
            float hDiv = height/100;

            //x, y are all percentage, transform them to real value before setBound
            for (Obstacle obs:obstacleList) {
                Drawable obstacleDrawable = obs.obstacleDrawable;
                obstacleDrawable.setBounds(intMul(obs.x, wDiv), intMul(obs.y, hDiv), intMul(obs.x+obs.width, wDiv), intMul(obs.y+obs.height, hDiv));
                obstacleDrawable.draw(canvas);
            }

            canvas.drawBitmap(characterBitmap, null,
                    new Rect( intMul(character.x, wDiv), intMul(character.y, hDiv),
                            intMul(character.x+character.width, wDiv), intMul(character.y+character.height, hDiv) )
                    , null);
        }

        /* multiply */
        public int intMul(int a, float b) {
            return (int)(a*b);
        }
    }

    /* Game view listener interface */
    public interface GameViewListener {
        public void OnGameUpdate();
    }

    private GameViewListener mGameViewListener = null;
    public void setGameViewListener(GameViewListener listener) {
        mGameViewListener = listener;
    }
}