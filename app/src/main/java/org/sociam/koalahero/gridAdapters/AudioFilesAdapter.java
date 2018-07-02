package org.sociam.koalahero.gridAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.sociam.koalahero.AudioRecordingActivity;
import org.sociam.koalahero.R;
import org.sociam.koalahero.audio.AudioPlayer;
import org.sociam.koalahero.audio.AudioStore;
import org.sociam.koalahero.audio.AudioRecorder;
import org.sociam.koalahero.audio.AudioRecording;

import java.util.Calendar;

public class AudioFilesAdapter extends RecyclerView.Adapter<AudioFilesAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView fileTime, audioDuration;
        public ImageView playButton, deleteButton, downloadButton;

        public ViewHolder(View v) {
            super(v);

            fileTime = (TextView) v.findViewById(R.id.file_time);
            audioDuration = (TextView) v.findViewById(R.id.audio_duration);
            playButton = (ImageView) v.findViewById(R.id.play_button);
            deleteButton = (ImageView) v.findViewById(R.id.delete_button);
            downloadButton = (ImageView) v.findViewById(R.id.download_button);
        }

    }


    private AudioRecordingActivity activity;
    private AudioStore audioStore;
    private AudioRecorder audioRecorder;

    // constructor
    public AudioFilesAdapter(AudioRecorder audioRecorder , AudioRecordingActivity act) {
        this.audioStore = audioRecorder.getAudioStore();
        this.audioRecorder = audioRecorder;
        this.activity = act;
    }

    @Override
    public AudioFilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_file_entry, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final AudioRecording ar = audioStore.get(position);

        holder.fileTime.setText( ar.getTitleFromTime() );

        holder.audioDuration.setText(AudioPlayer.secondsToTime(ar.getDuration()/1000) );


        holder.playButton.setVisibility(View.INVISIBLE);
        holder.downloadButton.setVisibility(View.INVISIBLE);
        holder.deleteButton.setVisibility(View.INVISIBLE);

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioStore.play(position);
            }
        });

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioStore.downloadFile( position );
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                audioStore.requestRemoteDelete(position);
            }
        });

        if( ar.isDownloaded() ){
            holder.playButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

        } else {
            holder.downloadButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

        }




    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return audioStore.getNumberFiles();
    }


}
