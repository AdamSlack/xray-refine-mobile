package org.sociam.koalahero.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.sociam.koalahero.AudioRecordingActivity;
import org.sociam.koalahero.MainActivity;
import org.sociam.koalahero.R;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    private static AudioRecorder INSTANCE;

    private int notificationID = 0;

    public static AudioRecorder getINSTANCE( Context con ) {
        if (INSTANCE == null) INSTANCE = new AudioRecorder( con );
        return INSTANCE;
    }

    private AudioStore audioStore;

    private File recordingDir;
    private Context context;

    private AudioRecorder( Context con ){
        this.context = con;

        recordingDir = new File(con.getFilesDir().getPath() + "/recordings/" );
        if( !recordingDir.exists()) recordingDir.mkdir();

        audioStore = AudioStore.getInstance(con);
    }

    private MediaRecorder recorder;

    private boolean isRecording = false;
    public boolean isRecording() {
        return isRecording;
    }

    private AudioRecording currentRecording = null;

    public int getCurrentRecordingTime(){
        if( isRecording )
            return (int)(System.currentTimeMillis() - currentRecording.getTimeStarted())/1000;
        return 0;
    }

    public boolean toggleRecording(){
        if( isRecording ) return stopRecording();
        else return startRecording();

    }

    public boolean startRecording(){

        recorder = new MediaRecorder();

        String filePath = recordingDir.getPath() + File.separator + "recording-" + System.currentTimeMillis() + ".mp4";
        currentRecording = new AudioRecording(filePath);
        currentRecording.setTimeStarted(System.currentTimeMillis());

        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile( filePath );
            recorder.prepare();
            recorder.start();
            isRecording = true;

            //Toast.makeText(context, "Audio Recording Started " + filePath, Toast.LENGTH_SHORT).show();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_fiber_manual_record_red)
                    .setContentTitle("Koala Hero")
                    .setContentText("Koala Hero is now recording audio...")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationID++, mBuilder.build());

        } catch (IOException e ){
            e.printStackTrace();
            return false;
        }

        return true;
    }



    public boolean stopRecording(){
        try {
            isRecording = false;
            recorder.stop();
            recorder.release();
            Toast.makeText(context, "Audio Recording Stopped", Toast.LENGTH_SHORT).show();


            // Set duration of audio
            try {
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(currentRecording.getFilePath());
                mp.prepare();

                int duration = mp.getDuration();
                currentRecording.setDuration(duration);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add to file store
            audioStore.addNew(currentRecording);

            //audioPlayer(files.get(files.size()-1));


            currentRecording = null;

            return true;
        } catch ( Exception e ){
            e.printStackTrace();
            return false;
        }
    }


    public void deleteRecordings(){
        File[] recordings = recordingDir.listFiles();
        for( int i = 0 ; i < recordings.length; i++ ){
            recordings[i].delete();
        }
        Toast.makeText(context, "Audio Recordings Deleted", Toast.LENGTH_SHORT).show();
    }

    // Show red dot in corner if recording
    public void updateRecordingUI(Activity activity){

        ImageView recordingDot = (ImageView) activity.findViewById(R.id.recording_dot);
        if( recordingDot != null ) {
            recordingDot.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fiber_manual_record_red));

            if (isRecording) {
                recordingDot.setVisibility(View.VISIBLE);
            } else {
                recordingDot.setVisibility(View.INVISIBLE);
            }

            recordingDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, AudioRecordingActivity.class);
                    context.startActivity(intent);

                }
            });
        }
    }

    public File getRecordingDir(){
        return recordingDir;
    }

    public AudioStore getAudioStore() {
        return audioStore;
    }
}
