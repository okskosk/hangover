package com.example.hangover;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class HitGame extends Activity implements SensorListener {
    private SensorManager sensorMgr ;
    private static final int FORCE_THRESHOLD = 1000;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_DURATION = 150;
    static final int UNDEFINED = 0;
    static final int FAST = 1;
    static final int NORMAL = 2;
    static final int SLOW = 3;

    private float mLastY=-1.0f;
    private long mLastTime;
    private int mShakeCount = 0;
    private long mLastShake;
    private int gameMode = 0;

    private TextView mShakeCountTV;
    private Button btnFast;
    private Button btnNormal;
    private Button btnSlow;
    private Button btnStart;
    private ImageView imageReady;

    private SoundPool soundPool;
    private AudioManager audioManager;
    private int coinSoundID, bgmSoundID, endSoundID;
    private int bgmSoundStream;
    private float volume;
    boolean loaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hit_game);

        // Initialize Sensor
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);

        mShakeCountTV = (TextView)findViewById(R.id.tvShakeCount);
        mShakeCountTV.setVisibility(View.INVISIBLE);
        imageReady = (ImageView)findViewById(R.id.image_ready);


        // Initialize Sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        coinSoundID = soundPool.load(this, R.raw.get_coin, 1);
        bgmSoundID = soundPool.load(this, R.raw.hit_game_bgm, 1);
        endSoundID = soundPool.load(this, R.raw.grenade_sound, 1);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;


        // Initialize Buttons
        btnFast = (Button)findViewById(R.id.btn_fast);
        btnNormal = (Button)findViewById(R.id.btn_normal);
        btnSlow = (Button)findViewById(R.id.btn_slow);
        btnStart = (Button)findViewById(R.id.btn_start);
        View.OnClickListener btnGameModeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable default_drawable =  getResources().getDrawable(R.drawable.button_white);
                Drawable highlited_drawable = getResources().getDrawable(R.drawable.button_black);
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

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameMode == UNDEFINED) {
                    Toast.makeText(getApplicationContext(), "Choose timer before start game.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Initialize display
                mShakeCountTV.setText("SHAKE!");
                btnStart.setVisibility(View.INVISIBLE);
                imageReady.setVisibility(View.INVISIBLE);
                mShakeCountTV.setVisibility(View.VISIBLE);

                // Init gameData
                mShakeCount = new Random().nextInt(31) + 10 + (gameMode - 1) * 50;

                // Start Music
                bgmSoundStream = soundPool.play(bgmSoundID, volume * 0.7f, volume * 0.7f, 1, 0, 1f);
            }
        });
    }

    public void onGameEnds() {
        btnStart.setVisibility(View.VISIBLE);
        imageReady.setVisibility(View.VISIBLE);
        mShakeCountTV.setVisibility(View.INVISIBLE);
        soundPool.stop(bgmSoundStream);
        soundPool.play(endSoundID, volume, volume, 1, 0, 1f);
    }

    public void onSensorChanged(int sensor, float[] values)
    {
        if (sensor != SensorManager.SENSOR_ACCELEROMETER) return;
        long now = System.currentTimeMillis();

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(values[SensorManager.DATA_Y] - mLastY) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    onShake();
                }
            }
            mLastTime = now;
            mLastY = values[SensorManager.DATA_Y];
        }
    }

    public void onAccuracyChanged(int sensor, int accuracy) { }

    public void onShake() {
        if (mShakeCountTV.getVisibility() == View.VISIBLE) {
            mShakeCount -= 1;
            if (mShakeCount <= 0) {
                onGameEnds();
            }
            mShakeCountTV.setText(String.valueOf(mShakeCount));
            if (loaded) {
                soundPool.play(coinSoundID, volume, volume, 1, 0, 1f);
            } else {
                Toast.makeText(getApplicationContext(), "NOT LOADED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
