package com.flyadventure.flyadventure;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flyadventure.flyadventure.VoiceRecognition.VoiceRecorder.MFCCClassifier;
import com.flyadventure.flyadventure.VoiceRecognition.VoiceRecorder.VoiceRecorder;
import com.flyadventure.flyadventure.VoiceRecognition.utils.ConstantValues;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    GameView gameView;
    TextView gameTextView;
    private GestureDetector mGestureDetector;

    private FrameLayout frameLayout;

    private boolean isTimming;
    private int currentTime;
    private Thread timeThread;
    public Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case ConstantValues.MESSAGE_RECEIVE_RESULT:
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
//                    resulTextView.setText(result);
                    break;
                case ConstantValues.MESSAGE_SUCCESS:
                    gameTextView.setText(msg.obj.toString());
                    break;
            }
        }
    };

    private VoiceRecorder mVoiceRecorder = null;
    private MFCCClassifier mfccClassifier = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout)findViewById(R.id.framelayout);
        //frameLayout.setOnTouchListener(new MyOnTouchListener());

        //text view
        gameTextView = (TextView) findViewById(R.id.gameTextView);

        //game controller and game view
        GameController.getInstance().initGame(this);
        gameView = (GameView) findViewById(R.id.gameView);
        GameController.getInstance().startGame(gameView);
        GameController.getInstance().setGameControllerListener(new GameControllerListener());

        // set onTouchListener
        mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }

        });

        //renew time
        tickTime();

//        mVoiceRecorder = VoiceRecorder.getInstance(this);
//        mfccClassifier = MFCCClassifier.getInstance(this);
//        mfccClassifier.setmMFCCClassifier(new MyMFCCClassifier());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() { super.onStop(); }

    @Override
    public void onDestroy(){
        gameView.stopGame();
        super.onDestroy();
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        /* using this function to do the voice recognition! */
        public boolean onSingleTapUp(MotionEvent e) {
            GameController.getInstance().character.jump();
            return false;
        }

        /*
        public void onLongPress(MotionEvent e) {
            GameController.getInstance().character.move();
            try {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(ConstantValues.debugTag, "ACTION_DOWN");
                        mVoiceRecorder.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(ConstantValues.debugTag, "ACTION_UP");
                        mVoiceRecorder.stop();
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        */

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // define the swipe min velocity and swipe min velocity
            final float SWIPE_MIN_VELOCITY = 10;
            final float SWIPE_MIN_DISTANCE = 10;

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
                    GameController.getInstance().character.left();
                }
                else //Switch Right
                {
                    GameController.getInstance().character.right();
                }
                GameController.getInstance().character.move();
            }

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

    //listen GameOver in GameController
    private class GameControllerListener implements GameController.GameControllerListener {
        @Override
        public void OnGameOver() {
            isTimming = false;
            try {
                timeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message msg = Message.obtain();
            msg.obj = "Survive " + Integer.toString(currentTime) + "s!";
            msg.what = ConstantValues.MESSAGE_SUCCESS;
            timeHandler.sendMessage(msg);
            //gameTextView.setText("You survive " + Integer.toString(time) + "seconds!" );
        }

        @Override
        public void OnGameRestart() {
            tickTime();
        }
    }

    public void tickTime() {
        isTimming = true;
        currentTime = 0;
        timeThread = new Thread(new TimeThread(timeHandler));
        timeThread.start();
    }

    class TimeThread implements Runnable {
        private String name;
        private Handler handler;

        public TimeThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            while (isTimming) {
                Message msg = Message.obtain();
                msg.obj = "Time: " + Integer.toString(currentTime);
                msg.what = ConstantValues.MESSAGE_SUCCESS;
                handler.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentTime++;
//                if (currentTime == 15 ) {
//                    //Obstacle.speedUp();
//                }
            }
        }
    }

    private class MyMFCCClassifier implements MFCCClassifier.MFCCResultListener {
        @Override
        public void onReceivedMFCCResult(String result) {
            Bundle bundle = new Bundle();
            bundle.putString("result", result);
            Message message = new Message();
            message.what = ConstantValues.MESSAGE_RECEIVE_RESULT;
            message.setData(bundle);
            timeHandler.sendMessage(message);

            Log.d(ConstantValues.debugTag, "result:" + result);

            if (result.equals("left")) {
                GameController.getInstance().character.left();
            } else if (result.equals("right")) {
                GameController.getInstance().character.right();
            } else if (result.equals("jump")) {
                GameController.getInstance().character.jump();
            } else if (result.equals("move")) {
                GameController.getInstance().character.move();
            }
        }
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        Log.d(ConstantValues.debugTag, "ACTION_DOWN");
                        mVoiceRecorder.start();
                        break;
                    case MotionEvent.ACTION_UP:
//                        Log.d(ConstantValues.debugTag, "ACTION_UP");
                        mVoiceRecorder.stop();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

}
