package com.main.goplay;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private boolean showGame = true;

    public static String webViewURL = "https://google.com";
    private ImageView winner1Image;
    private ImageView winner2Image;
    boolean spinIsStarted = false;
    final long ROTATION_TIME = 1000;
    final int ROTATION_MIN_COUNT = 10;
    final int ROTATION_MAX_COUNT = 30;
    private Memory memory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memory = new Memory(getPreferences(MODE_PRIVATE));
        webViewURL = memory.loadLink(webViewURL);
        new RequestToGit(new RequestListener() {
            @Override
            public void waiterForBool(boolean bool) {
                if (showGame != bool) {
                    showGame = bool;
                    memory.saveState(showGame);
                }
            }

            @Override
            public void waiterForLink(String link) {
                webViewURL = link;
                showGameHideWebView(showGame);
            }

            @Override
            public void rejection() {
                if (memory.firstStart()) {
                    showGameHideWebView(true);
                }
            }
        });

        if (!memory.firstStart()) {
            showGameHideWebView(memory.isShowGame(showGame));
        }

    }

    public void openWebActivity(String url) {
        Intent intent = new Intent(this, WebView.class);
        intent.putExtra(EXTRA_MESSAGE, url);
        startActivity(intent);
        finish();
    }

    private void initGame() {
        Toast.makeText(getApplicationContext(), "Жми на стрелку!", Toast.LENGTH_SHORT).show();

        winner1Image = findViewById(R.id.winner1Image);
        winner2Image = findViewById(R.id.winner2Image);
        ImageView arrow = findViewById(R.id.spinArrow);

        arrow.setOnClickListener(v -> startSpin(v));
    }

    private void showGameHideWebView(boolean showGame) {
        runOnUiThread(() -> {
            this.showGame = showGame;
            memory.saveState(showGame);
            if (!showGame) {
                openWebActivity(webViewURL);
            } else {
                LinearLayout gameScene = findViewById(R.id.gameScene);
                gameScene.setVisibility(View.VISIBLE);

                initGame();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        memory.saveLink(webViewURL);
        memory.saveState(showGame);
    }

    private void startSpin(View v) {
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