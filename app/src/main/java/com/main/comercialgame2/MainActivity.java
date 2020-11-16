package com.main.comercialgame2;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    boolean spinIsStarted = false;
    final long ROTATION_TIME = 7000;
    final int ROTATION_MIN_COUNT = 30;
    final int ROTATION_MAX_COUNT = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinIsStarted) {
                    spinIsStarted = true;
                    Random rand = new Random();
                    int countRotation = rand.nextInt(ROTATION_MAX_COUNT) + (ROTATION_MAX_COUNT - ROTATION_MIN_COUNT);
                    int rotationDegrees = 180 * countRotation;
                    v.animate()
                            .rotation(rotationDegrees)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(ROTATION_TIME)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    v.setRotation(rotationDegrees);
                                    if (countRotation % 2 == 0) {
                                        onRightWinner();
                                    } else {
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
                    Toast.makeText(getApplicationContext(), "Right winner", Toast.LENGTH_SHORT).show();
                });
            }
        }, 1000);
    }

    private void onLeftWinner() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Left winner", Toast.LENGTH_SHORT).show();
                });
            }
        }, 1000);
    }
}