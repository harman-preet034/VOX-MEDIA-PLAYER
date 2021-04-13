package com.harman.voxmediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

import static com.harman.voxmediaplayer.MainActivity.myMusic;
import static com.harman.voxmediaplayer.MainActivity.repeat;
import static com.harman.voxmediaplayer.MainActivity.shuffle;
import static com.harman.voxmediaplayer.MusicAdapter.musicFiles;

public class PlayerActivity extends AppCompatActivity {
//Player Activity Working
    //Taking Frontend Views vairiables
    TextView song_name,artist_name,song_duration,song_total;
    ImageView album_art,nextBtn,backbtn,prevbtn,shufflebtn,repeatbtn;
    FloatingActionButton playBtn;
    SeekBar bar;
    int index=-1;
    //song list array
   static  ArrayList<MusicFiles> Songs;
   static Uri uri;
   static MediaPlayer mediaplayer;
   private Thread prevThread,nextThread,playThread;
   private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        //start views
        initViews();
        getIntents();
        //set Playing SOng Name
        song_name.setText(Songs.get(index).getTitle());
        artist_name.setText(Songs.get(index).getArtist());
        //Updating Seek Bar when USer clicks and moves it
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaplayer!=null && fromUser) mediaplayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Thread function to implement Continusly playing of sonmgs
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaplayer!=null){
                    int currPos=mediaplayer.getCurrentPosition()/1000;
                    bar.setProgress(currPos);
                    song_duration.setText(formattedTime(currPos));

                }
                handler.postDelayed(this,1000);
            }
        });
        //Event Listener For Back Button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();

            }
        });
        //Event Listener For Shuffle Button
        shufflebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle){
                    shuffle=false;
                    //Toggling shuffle status and updating shuffle button image
                    shufflebtn.setImageResource(R.drawable.ic_shuffle_off);

                }
                else{
                    shuffle=true;
                    //Toggling shuffle status and updating shuffle button image
                    shufflebtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        //Event Listener For Repeat Button
        repeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeat){
                    repeat=false;
//                    Toggling repeat Status and image
                    repeatbtn.setImageResource(R.drawable.ic_repeat_off);

                }
                else{
                    repeat=true;
                    //                    Toggling repeat Status and image
                    repeatbtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
    }

    //function to return time in minutes:Seconds(eg 05:22)
    private String formattedTime(int currPos) {
        String total="";
        String newtotal ="";
        String seconds=String.valueOf(currPos%60);
        String mins=String.valueOf(currPos/60);
        total=mins+":"+seconds;
        newtotal=mins+":"+"0"+seconds;
        if(seconds.length()==1){
            return newtotal;
        }
        else{
            return total;

        }

    }

    //getfile Itents
    private void getIntents() {
        index=getIntent().getIntExtra("index",-1);
        Songs=musicFiles;
        if(Songs!=null){
            playBtn.setImageResource(R.drawable.ic_pause);
            uri= Uri.parse(Songs.get(index).getPath());
            song_total.setText(formattedTime(Integer.valueOf(Songs.get(index).getDuration())));
        }
        if(mediaplayer!=null){
            mediaplayer.stop();
            mediaplayer.release();
        }
        mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaplayer.start();
        bar.setMax(mediaplayer.getDuration()/1000);
        getMetadata(uri);
    }

    //Initializing View
    private void initViews() {
        song_name=findViewById(R.id.Song_name);
        artist_name=findViewById(R.id.singer_name);
        song_duration=findViewById(R.id.songdurationplayed);
        song_total=findViewById(R.id.songdurationtotal);
        album_art=findViewById(R.id.art);
        nextBtn=findViewById(R.id.skip_next);
        backbtn=findViewById(R.id.back_btn);
        prevbtn=findViewById(R.id.skip_prev);
        shufflebtn=findViewById(R.id.shuffle_off);
        repeatbtn=findViewById(R.id.repeat_off);
        playBtn=findViewById(R.id.play_pause);
        bar=findViewById(R.id.seekbar);
    }
    //Extracting Metadata From the file.
    private void getMetadata(Uri uri){
        MediaMetadataRetriever ret =new MediaMetadataRetriever();
        ret.setDataSource(uri.toString());
        int duratonTotal=Integer.parseInt(Songs.get(index).getDuration())/1000;
        song_total.setText(formattedTime(duratonTotal));
        byte[] art=ret.getEmbeddedPicture();
        if(art!=null){
            Glide.with(this).asBitmap().load(art).into(album_art);
        }
        else{
            Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(album_art);
        }

    }


    @Override
    protected void onPostResume() {
        playThreadBtn();
        prevThreadBtn();
        nexThreadBtn();
        super.onPostResume();
    }
//PLay Thread button
    private void playThreadBtn() {
        playThread=new Thread(){
            @Override
            public void run() {
                super.run();
                playBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        playBtnClicked();
                    }
                });

            }
        };
        playThread.start();
    }
    //Previous Button Thread
    private void prevThreadBtn() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                prevbtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });

            }
        };
        prevThread.start();
    }
    //next Button Thread
    private void nexThreadBtn() {
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });

            }
        };
        nextThread.start();
    }

    //On Next Click Button calling next Buttn Task to play next song and update cover Art
    private void nextBtnClicked() {
        if(mediaplayer.isPlaying()){

            nextBtnTask();
            playBtn.setImageResource(R.drawable.ic_pause);
            mediaplayer.start();
        }else{
            playBtn.setImageResource(R.drawable.ic_play);
            nextBtnTask();

        }
    }
    //Next Button task controls all the activity To controll The working of how next song will be played
    public void nextBtnTask(){
        //pauses Media player
        mediaplayer.stop();
        mediaplayer.release();
        //checking shuffle and repeat status
        if(shuffle &&!repeat){
            Random rand=new Random();
            index=rand.nextInt((Songs.size()-1)+1);
        }
        else if(!shuffle && !repeat){
            index=((index+1)%Songs.size());
        }
    //getting next songs
        uri=Uri.parse(Songs.get(index).getPath());
        mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
        getMetadata(uri);
        //updating UI
        song_name.setText(Songs.get(index).getTitle());
        artist_name.setText(Songs.get(index).getArtist());
        bar.setMax(mediaplayer.getDuration()/1000);
        //playing next song
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaplayer!=null){
                    int currPos=mediaplayer.getCurrentPosition()/1000;
                    bar.setProgress(currPos);
                }
                handler.postDelayed(this,1000);
            }
        });

    }
    public void prevBtnTask(){
        //pausing media player
        mediaplayer.stop();
        mediaplayer.release();
    //checking shuffle and repeat status
        if(shuffle &&!repeat){
            Random rand=new Random();
            index=rand.nextInt((Songs.size()-1)+1);
        }
        else if(!shuffle && !repeat){
            if(index-1<0){
                index=Songs.size()-1;
            }
            else{
                index=index-1;
            }
        }
        //gettingnext song
        uri=Uri.parse(Songs.get(index).getPath());
        //upadting UI
        mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
        getMetadata(uri);
        song_name.setText(Songs.get(index).getTitle());
        artist_name.setText(Songs.get(index).getArtist());
        bar.setMax(mediaplayer.getDuration()/1000);
        //Playing Song
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaplayer!=null){
                    int currPos=mediaplayer.getCurrentPosition()/1000;
                    bar.setProgress(currPos);
                }
                handler.postDelayed(this,1000);
            }
        });

    }
    //On click on previous button previous button task is called to execute previous Task
    private void prevBtnClicked() {
        if(mediaplayer.isPlaying()){
            prevBtnTask();
            playBtn.setImageResource(R.drawable.ic_pause);
            mediaplayer.start();
        }else{
            playBtn.setImageResource(R.drawable.ic_play);
            prevBtnTask();

        }
    }

//on Click Of play button Fistly we checked song play status and id song is playing we pause it and update the resource image
    private void playBtnClicked() {
        if(mediaplayer.isPlaying()){
            playBtn.setImageResource(R.drawable.ic_play);
            mediaplayer.pause();
            bar.setMax(mediaplayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaplayer!=null){
                        int currPos=mediaplayer.getCurrentPosition()/1000;
                        bar.setProgress(currPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        //else we pausing the song and updating resource image
        else{
            playBtn.setImageResource(R.drawable.ic_pause);
            mediaplayer.start();
            bar.setMax(mediaplayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaplayer!=null){
                        int currPos=mediaplayer.getCurrentPosition()/1000;
                        bar.setProgress(currPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });

        }
    }
}