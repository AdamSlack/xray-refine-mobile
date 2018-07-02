package org.sociam.koalahero.audio;

import java.util.ArrayList;
import java.util.List;

public class AudioStore {

    private static AudioStore audioStore;

    private AudioPlayer audioPlayer;

    public static AudioStore getInstance(){
        if( audioStore == null) audioStore = new AudioStore();
        return audioStore;
    }

    private AudioStore(){

        audioPlayer = AudioPlayer.getINSTANCE();

        download();

    }

    private List<AudioRecording> files = new ArrayList<AudioRecording>();

    public void addNew( AudioRecording ar ){

        // Upload

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
