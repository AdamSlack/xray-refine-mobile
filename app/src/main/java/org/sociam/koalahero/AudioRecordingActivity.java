package org.sociam.koalahero;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sociam.koalahero.appsInspector.AppDisplayMode;
import org.sociam.koalahero.appsInspector.Interval;
import org.sociam.koalahero.audio.AudioPlayer;
import org.sociam.koalahero.audio.AudioRecorder;
import org.sociam.koalahero.gridAdapters.AudioFilesAdapter;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

public class AudioRecordingActivity extends AppCompatActivity {

    AudioRecorder audioRecorder;

    private AudioPlayer audioPlayer;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Context context;
    private Activity activity;

    private Handler audioPlayerUIHandler = new Handler();

    private boolean hasMicPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Audio Recording");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        activity = this;

        checkMicPermissions();
        Button permissionButton = (Button) findViewById(R.id.permission_message_grant_button);
        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Microphone Permission
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            MainActivity.AUDIO_PERMISSION_REQUEST_CODE);


                }
            }
        });

        audioPlayer = AudioPlayer.getINSTANCE();
        audioRecorder = AudioRecorder.getINSTANCE(this);
        updateRecordingButton();

        ImageView recordButton = (ImageView) findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( hasMicPermission)
                    audioRecorder.toggleRecording();

                updateRecordingButton();
                updateScreen();

            }
        });


        ImageView deleteButton = (ImageView) findViewById(R.id.delete_all_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                audioRecorder.deleteRecordings();
                updateScreen();

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.file_list);
        //mRecyclerView.setHasFixedSize(true);

        // Use Linear Layout Manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        mAdapter = new AudioFilesAdapter( audioRecorder, this);
        mRecyclerView.setAdapter(mAdapter);
        updateScreen();


        // Audio Player Interface
        Thread audioPlayerInterface = new Thread(new Runnable() {
            @Override
            public void run() {

                while( true ){

                    audioPlayerUIHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            final TextView titleView = (TextView) findViewById(R.id.audio_player_title);
                            final TextView timeView = (TextView) findViewById(R.id.audio_player_time);
                            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.audio_player_bar);

                            titleView.setText( audioPlayer.getTitle() );
                            timeView.setText( AudioPlayer.secondsToTime(audioPlayer.getCurrentPosition()/1000) + "/" + AudioPlayer.secondsToTime(audioPlayer.getDuration()/1000) );

                            progressBar.setProgress( audioPlayer.getProgress() );


                            final TextView currentRecordingTime = (TextView) findViewById(R.id.recording_time);
                            currentRecordingTime.setText( AudioPlayer.secondsToTime(audioRecorder.getCurrentRecordingTime()) );
                        }
                    });

                    try { Thread.sleep(200); } catch ( InterruptedException e){}

                }
            }
        });
        audioPlayerInterface.start();

        ImageView audioPlayButton = (ImageView) findViewById(R.id.audio_player_play);
        audioPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioPlayer.play();
            }
        });

        ImageView audioPauseButton = (ImageView) findViewById(R.id.audio_player_pause);
        audioPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioPlayer.pause();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == MainActivity.AUDIO_PERMISSION_REQUEST_CODE) {

            checkMicPermissions();

        }

    }


    public void checkMicPermissions(){

        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.permission_message);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            cl.setVisibility( View.VISIBLE );
            hasMicPermission = false;
        } else {
            cl.setVisibility( View.INVISIBLE );
            hasMicPermission = true;
        }
    }

    public void updateScreen(){

        TextView noFilesMessage = (TextView) findViewById(R.id.no_recordings_message);
        if( audioRecorder.getAudioStore().getNumberFiles() ==0 ){
            noFilesMessage.setVisibility(View.VISIBLE);
        } else {
            noFilesMessage.setVisibility(View.INVISIBLE);
        }

        mAdapter.notifyDataSetChanged();
        updateRecordingButton();
    }

    public void updateRecordingButton(){

        ImageView recordButton = (ImageView) findViewById(R.id.record_button);

        if (audioRecorder.isRecording()) {
            recordButton.setImageResource(R.drawable.stop_recording);
        } else {
            recordButton.setImageResource(R.drawable.start_recording);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }



}
