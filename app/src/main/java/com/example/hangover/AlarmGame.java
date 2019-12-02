package com.example.hangover;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Random;

import static java.lang.Math.min;

public class AlarmGame extends Activity {
    static final int UNDEFINED = 0;
    static final int FAST = 1;
    static final int NORMAL = 2;
    static final int SLOW = 3;
    static int gameMode;
    static int colorDirection = 1;
    static int colorStart = Integer.parseInt("69e781", 16);
    static int colorEnd = Integer.parseInt("BE0000", 16);
    MediaPlayer mediaPlayer;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyMediaPlayer();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_game);
        final Button btnStart = (Button)findViewById(R.id.btn_start);
        final Button btnFast = (Button)findViewById(R.id.btn_fast);
        final Button btnNormal = (Button)findViewById(R.id.btn_normal);
        final Button btnSlow = (Button)findViewById(R.id.btn_slow);
        final ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.alarm_game_root);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        // Initialize gameMode btn
        View.OnClickListener btnGameModeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable default_drawable =  getResources().getDrawable(R.drawable.button_white);
                Drawable highlited_drawable = getResources().getDrawable(R.drawable.button_yellow);
                btnFast.setBackground(default_drawable);
                btnNormal.setBackground(default_drawable);
                btnSlow.setBackground(default_drawable);
                switch (v.getId()) {
                    case R.id.btn_fast:
                        gameMode = FAST;
                        btnFast.setBackground(highlited_drawable);
                        break;
                    case R.id.btn_normal:
                        gameMode = NORMAL;
                        btnNormal.setBackground(highlited_drawable);
                        break;
                    case R.id.btn_slow:
                        gameMode = SLOW;
                        btnSlow.setBackground(highlited_drawable);
                        break;
                }
            }
        };
        btnFast.setOnClickListener(btnGameModeClickListener);
        btnNormal.setOnClickListener(btnGameModeClickListener);
        btnSlow.setOnClickListener(btnGameModeClickListener);

        // Initialize gameStart btn
        int i = 0;
        View.OnClickListener btnGameStartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameMode == UNDEFINED) {
                    Toast.makeText(getApplicationContext(), "Choose timer before start game.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final int duration = getRandomDuration() * 1000;
                progressBar.setProgress(100);
                new CountDownTimer(duration, 100) {
                    int i = 0;
                    int color = colorStart;
                    int delta = (colorEnd - colorStart) / 10;

                    public void onTick(long millisUntilFinished) {
                        // Progress bar
                        ++i;
                        progressBar.setProgress(Math.max(0, 100 - 100 * i / (duration / 100)));

                        // Background change
                        if (i % 2 == 0) {
                            color += getNextColor(color, delta);
                            constraintLayout.setBackgroundColor(0xff000000 + color);
                        }

                        if (i > duration / 100 * 2 / 3) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        // Bomb Animation
                    }
                    public void onFinish() {
                        constraintLayout.setBackgroundColor(0xff000000 + colorStart);
                        btnStart.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        destroyMediaPlayer();
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.grenade_sound);
                        mediaPlayer.start();
                    }
                }.start();
                destroyMediaPlayer();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bensound_house);
                mediaPlayer.start();
                btnStart.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        };
        btnStart.setOnClickListener(btnGameStartListener);
    }

    protected int getNextColor(int color, int delta) {
        if (color < min(colorStart, colorEnd) || color > Math.max(colorStart, colorEnd)) {
            colorDirection *= -1;
        }
        return delta * colorDirection;
    }

    protected int getRandomDuration() {
        int lower_bound = (gameMode - 1) * 10 + 5;
        int random = new Random().nextInt(11);
        return lower_bound + random;
    }

    protected void destroyMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
