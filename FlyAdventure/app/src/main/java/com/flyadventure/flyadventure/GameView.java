package com.flyadventure.flyadventure;

import android.content.Context;
import android.graphics.Canvas;
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

    private TimerTask task;
    private Timer timer;

    private SurfaceHolder holder;
    private DrawViewThread drawThread;

    private GameController gameController;
    private Character character;
    private List<Obstacle> obstacleList;
    private List<Floor> floorList;
    private Scene scene;

    public GameView(Context context) {
        super(context);
        init(null, 0);

        Log.i("GameView", "~");
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);

        Log.i("GameView", "~");
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        //Resources res = getResources();
        this.obstacleList = new ArrayList<Obstacle>();
        this.floorList = new ArrayList<Floor>();

        Drawable mapDrawable = ContextCompat.getDrawable(getContext(), R.drawable.level4map);
        Drawable obstaclesDrawable = ContextCompat.getDrawable(getContext(), R.drawable.obstacle);
        Drawable characterDrawable = ContextCompat.getDrawable(getContext(), R.drawable.running_1);

        this.character = new Character(characterDrawable);
        this.scene = new Scene(mapDrawable);
        this.obstacleList.add(new Obstacle(obstaclesDrawable));

        this.gameController = new GameController(character, obstacleList, scene);

        // initialize floor list
        Floor ground_floor = new Floor(2, 2, 0, 100);
        Floor upper_floor_1 = new Floor(30, 30, 4, 30);
        Floor upper_floor_2 = new Floor(67, 67, 8, 35);
        Floor upper_floor_3 = new Floor(57, 57, 65, 88);
        Floor upper_floor_4 = new Floor(18, 18, 70, 90);
        this.floorList.add(ground_floor);
        this.floorList.add(upper_floor_1);
        this.floorList.add(upper_floor_2);
        this.floorList.add(upper_floor_3);
        this.floorList.add(upper_floor_4);

        // set the floor list of the game controller
        this.gameController.setFloorList(floorList);

        Log.i("debug", "msg");

        // define a timer to add obstacles periodically
        task = new TimerTask() {
            @Override
            public void run() {
                addObstacle();
            }
        };

        // set the timer
        timer = new Timer(true);
        timer.schedule(task, 1500, 1500);

        holder = this.getHolder();
        holder.addCallback(this);
        drawThread = new DrawViewThread(this.holder, this.gameController);
    }

    public void addObstacle() {
        Drawable obstaclesDrawable = ContextCompat.getDrawable(getContext(), R.drawable.obstacle);
        this.obstacleList.add(new Obstacle(obstaclesDrawable));
    }

    public Character getCharacter() {
        return this.character;
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

        drawThread.threadIsRunning = true;
        drawThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.threadIsRunning = false;
    }

    public void stopGame() {
        if (drawThread != null) {
            drawThread.setRunning(false);
        }
    }

    public void releaseResources() {

    }

    public class DrawViewThread extends Thread {
        private SurfaceHolder holder;
        private GameController gameController;
        boolean threadIsRunning;

        public DrawViewThread(SurfaceHolder holder, GameController controller)
        {
            this.holder = holder;
            this.gameController = controller;
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