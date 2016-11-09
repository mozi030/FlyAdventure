package com.flyadventure.flyadventure;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the reference to game view
        gameView = (GameView) findViewById(R.id.gameView);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                if (gameView == null) return true;
                this.gameView.getCharacter().jump();
//                Log.d("debug", "Touch the screen~");
                return true;
            case MotionEvent.ACTION_UP:
        }

        // do not modify this sentence
        return super.onTouchEvent(event);
    }
}
