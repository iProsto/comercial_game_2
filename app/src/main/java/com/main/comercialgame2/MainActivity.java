package com.main.comercialgame2;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ImageView arrow;
    ImageView winner1Image;
    ImageView winner2Image;
    boolean spinIsStarted = false;
    final long ROTATION_TIME = 1000;
    final int ROTATION_MIN_COUNT = 10;
    final int ROTATION_MAX_COUNT = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(), "Жми на стрелку!", Toast.LENGTH_LONG).show();

        winner1Image = findViewById(R.id.winner1Image);
        winner2Image = findViewById(R.id.winner2Image);
        arrow = findViewById(R.id.spinArrow);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinIsStarted) {
                    winner1Image.setVisibility(View.INVISIBLE);
                    winner2Image.setVisibility(View.INVISIBLE);
                    spinIsStarted = true;
                    Random rand = new Random();
                    int countRotation = rand.nextInt(ROTATION_MAX_COUNT - ROTATION_MIN_COUNT) + ROTATION_MIN_COUNT;
                    int rotationDegrees = 180 * countRotation - 90;
                    v.animate()
                            .rotation(rotationDegrees)
                            .setInterpolator(new DecelerateInterpolator())
                            //.setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(ROTATION_TIME)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (countRotation % 2 == 0) {
                                        v.setRotation(-90);
                                        onRightWinner();
                                    } else {
                                        v.setRotation(90);
                                        onLeftWinner();
                                    }
                                    spinIsStarted = false;
                                }
                            }).start();
                }
            }
        });
    }

    private void onRightWinner() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    winner2Image.setVisibility(View.VISIBLE);
                });
            }
        }, 700);
    }

    private void onLeftWinner() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    winner1Image.setVisibility(View.VISIBLE);
                });
            }
        }, 700);
    }

    private void console(String msg) {
        Log.d("Loger", msg);
    }
}