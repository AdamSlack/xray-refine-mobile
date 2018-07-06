package org.sociam.koalahero.audio;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer {


    private static AudioPlayer INSTANCE;

    public static AudioPlayer getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new AudioPlayer();
        return INSTANCE;
    }

    private AudioPlayer(){

    }


    private MediaPlayer mp = new MediaPlayer();

    boolean isLoaded = false;
    private String title = "";

    public boolean loadFile( String filePath , String title ){
        try {
            mp.release();
            mp = new MediaPlayer();
            mp.setDataSource(filePath);
            mp.prepare();

            isLoaded = true;
            this.title = title;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean play(){
        if( isLoaded && !mp.isPlaying() ) {
            mp.start();
            return true;
        }

        return false;
    }

    public boolean pause(){

        if( mp.isPlaying())
            mp.pause();
        return true;
    }

    public boolean stop(){

        mp.stop();

        return true;
    }

    public boolean setTime(int seconds){
        mp.seekTo(seconds*1000);

        return false;
    }

    public boolean isPlaying(){
        return mp.isPlaying();
    }

    public int getCurrentPosition(){
        return mp.getCurrentPosition();
    }

    public int getDuration(){
        return mp.getDuration();
    }

    public String getTitle(){

        if( isLoaded ) return title;
        return "";
    }

    public int getProgress(){

        if( mp.getDuration() == 0 ) return 0;
        return (int)((double)mp.getCurrentPosition()/ (double) mp.getDuration()* (double)100);

    }


    public static String secondsToTime( int seconds ){

        return (seconds/60) + ":" + String.format("%02d", (seconds%60));
    }
}
