package com.example.hangover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Resize screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = Math.min(displayMetrics.heightPixels, 5 * (width / 3));

        GridLayout layout = (GridLayout) findViewById(R.id.main_grid_layout);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = width;
        params.height = height;
        layout.setLayoutParams(params);

        // Initialization
        LinearLayout btnLocalStopWatch = (LinearLayout)findViewById(R.id.local_stopwatch);
        btnLocalStopWatch.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlarmGame.class);
                startActivity(intent);
            }
        });

        LinearLayout btnLocalHitGame = (LinearLayout) findViewById(R.id.local_hitgame);
        btnLocalHitGame.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HitGame.class);
                startActivity(intent);
            }
        });
    }
}
