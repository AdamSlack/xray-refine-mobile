package org.sociam.koalahero.audio;

import android.content.Context;
import android.os.CpuUsageInfo;

import org.sociam.koalahero.PreferenceManager.PreferenceManager;
import org.sociam.koalahero.koala.KoalaAPI;
import org.sociam.koalahero.koala.KoalaData.AudioLogRequestDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioStore {

    private static AudioStore audioStore = null;

    private AudioPlayer audioPlayer;
    private Context context;

    private AudioStore(){}

    public static AudioStore getInstance(Context context){
        if( audioStore == null) audioStore = new AudioStore(context);
        return audioStore;
    }

    private AudioStore(Context context){
        this.audioPlayer = AudioPlayer.getINSTANCE();
        this.context = context;
        download();

    }

    private List<AudioRecording> files = new ArrayList<AudioRecording>();

    public void addNew( AudioRecording ar ){

        // Upload
        KoalaAPI koalaAPI = KoalaAPI.getInstance();
        AudioLogRequestDetails details = new AudioLogRequestDetails();
        details.length = ar.getDuration();
        details.date = new Date(ar.getTimeStarted());
        details.filePath = ar.getFilePath();
        details.studyID = PreferenceManager.getInstance(this.context).getKoalaStudyID();

        koalaAPI.executeAudioLogRequest(details);

        // Store
        files.add(0,ar);

    }

    public int getNumberFiles(){
        return files.size();
    }

    public AudioRecording get( int i){
        return files.get(i);
    }

    public void requestRemoteDelete( int i ){

        // Handle user requesting delete of file on server

    }

    public void downloadFile( int i ){

        // Handle Download of individual audio file

    }

    public void play( int i ){

        AudioRecording ar = files.get(i);

        audioPlayer.loadFile( ar.getFilePath(), ar.getTitleFromTime());
        audioPlayer.play();

    }



    // download list of recordings from server
    public boolean download(){

        return true;
    }

}
