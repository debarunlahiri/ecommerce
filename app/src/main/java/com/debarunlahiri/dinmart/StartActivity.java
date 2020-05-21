package com.debarunlahiri.dinmart;

import android.content.Intent;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.debarunlahiri.dinmart.activity.LoginActivity;
import com.debarunlahiri.dinmart.next.R;

public class StartActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button button5, button9;

    private MediaPlayer mMediaPlayer;
    private int currentPosition = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        videoView = findViewById(R.id.videoView);
        button5 = findViewById(R.id.button5);
        button9 = findViewById(R.id.button9);

//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background3);
//        videoView.setVideoURI(uri);
//        videoView.start();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mMediaPlayer = mp;
//                mp.setLooping(true);
//                if (currentPosition != 0) {
//                    mp.seekTo(0);
//                    mp.start();
//                }
//            }
//        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
        });

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        currentPosition = mMediaPlayer.getCurrentPosition();
//        videoView.pause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        videoView.start();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mMediaPlayer.release();
//        mMediaPlayer = null;
//    }
}
