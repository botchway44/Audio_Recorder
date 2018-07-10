package com.newventuresoftware.waveform;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class RecordingActivity extends AppCompatActivity {

    private WaveformView mRealtimeWaveformView;
    private RecordingThread mRecordingThread;
    private PlaybackThread mPlaybackThread;
    private static final int REQUEST_RECORD_AUDIO = 13;

    //Audio Recorder started
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = "";
    private static String audioUrl = "";

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder mMediaRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);


        mRealtimeWaveformView = (WaveformView) findViewById(R.id.waveformView);
        mRecordingThread = new RecordingThread(new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
                mRealtimeWaveformView.setSamples(data);
            }
        });


        //Init Audio
        initAudio();

    }

    /*Audio Recorder Functionalities here*/
    public void initAudio() {
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        }


//        mRecordButton = new RecordButton(this);
//        mRecordButton = new RecordButton((this));
        final boolean[] mStartRecording = {true};

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRecord(mStartRecording[0]);
                if (mStartRecording[0]) {
                    // setText("Stop recording");
                    Toast.makeText(RecordingActivity.this,"Stop recording",Toast.LENGTH_SHORT).show();
                } else {
                    //setText("Start recording");
                    Toast.makeText(RecordingActivity.this,"Start recording",Toast.LENGTH_SHORT).show();
                }
                mStartRecording[0] = !mStartRecording[0];
            }

        });

       // mPlayButton = new PlayButton(this);
       // mPlayButton = findViewById(R.id.playFab);
        findViewById(R.id.playFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean mStartPlaying = true;

                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    //setText("Stop playing");
                    Toast.makeText(RecordingActivity.this,"stop playing",Toast.LENGTH_SHORT).show();
                } else {
                    //setText("Start playing");
                    Toast.makeText(RecordingActivity.this,"Start playing",Toast.LENGTH_SHORT).show();
                }
                mStartPlaying = !mStartPlaying;
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public class RecordButton extends android.support.design.widget.FloatingActionButton {


     private int id;
     public void setID(int id){
           this.id = id;
           this.findViewById(id);
       }

        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                   // setText("Stop recording");
                    Toast.makeText(getContext(),"Stop recording",Toast.LENGTH_SHORT).show();
                } else {
                    //setText("Start recording");
                    Toast.makeText(getContext(),"Start recording",Toast.LENGTH_SHORT).show();
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            //setText("Start recording");
            Toast.makeText(getContext(),"Start recording",Toast.LENGTH_SHORT).show();
            setOnClickListener(clicker);

        }
    }

    public class PlayButton extends android.support.design.widget.FloatingActionButton {
        boolean mStartPlaying = true;

        View.OnClickListener clicker = new View.OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    //setText("Stop playing");
                    Toast.makeText(getContext(),"stop playing",Toast.LENGTH_SHORT).show();
                } else {
                    //setText("Start playing");
                    Toast.makeText(getContext(),"Start playing",Toast.LENGTH_SHORT).show();
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
           // setText("Start playing");
            Toast.makeText(getContext(),"Start playing",Toast.LENGTH_SHORT).show();
            setOnClickListener(clicker);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


}
