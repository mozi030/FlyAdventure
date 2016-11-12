package com.flyadventure.flyadventure;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    GameView gameView;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGestureDetector = new GestureDetector(this, new MyOnGestureListener());

        // get the reference to game view
        gameView = (GameView) findViewById(R.id.gameView);

        // set onTouchListener
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        gameView.stopGame();
        super.onPause();
    }

    @Override
    protected void onStop() { super.onStop(); }

    @Override
    public void onDestroy(){
        super.onDestroy();
        gameView.releaseResources();
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (gameView == null) return true;
            gameView.getCharacter().jump();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // define the swipe min velocity and swipe min velocity
            final float SWIPE_MIN_VELOCITY = 30;
            final float SWIPE_MIN_DISTANCE = 30;

            float ev1Y = e1.getY();
            float ev2Y = e2.getY();

            //Get distance of Y (e1) to Y (e2)
            final float ydistance = Math.abs(ev1Y - ev2Y);
            //Get velocity of cursor
            final float yvelocity = Math.abs(velocityY);

            if( (yvelocity > SWIPE_MIN_VELOCITY) && (ydistance > SWIPE_MIN_DISTANCE) )
            {
                if(ev1Y > ev2Y) //Switch Left
                {
                    gameView.getCharacter().left();
                }
                else //Switch Right
                {
                    gameView.getCharacter().right();
                }
            }

//            return false;
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_DOWN:
//                if (gameView == null) return true;
//                this.gameView.getCharacter().jump();
//                return true;
//            case MotionEvent.ACTION_UP:
//        }
//
//        // do not modify this sentence
//        return super.onTouchEvent(event);
//    }


}
