package com.harman.voxmediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private Context musicContext;
    static ArrayList<MusicFiles> musicFiles;
    MusicAdapter(Context mContext,ArrayList<MusicFiles> musicFiles){
        this.musicContext=mContext;
        this.musicFiles=musicFiles;
    }

    //creating Song lISt Frag
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView my_file_name;
        ImageView art;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            my_file_name=itemView.findViewById(R.id.file_name);
            art=itemView.findViewById(R.id.mimg);
        }
    }
    //get Album Art From The song file
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever ret=new MediaMetadataRetriever();
        Log.e("Path:"+uri," a");
        ret.setDataSource(uri);
        byte[] art = ret.getEmbeddedPicture();
        ret.release();
        return art;

    }



    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileview= LayoutInflater.from(musicContext).inflate(R.layout.music_files,parent,false);
        return new MyViewHolder(fileview);
    }

    //Creating Song list in the player
    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, int position) {
        holder.my_file_name.setText(musicFiles.get(position).getTitle());
        byte[] img=getAlbumArt(musicFiles.get(position).getPath());
        if(img!=null){
            Glide.with(musicContext).asBitmap().load(img).into(holder.art);
        }
        else{
            Glide.with(musicContext).load(R.mipmap.ic_launcher_foreground).into(holder.art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(musicContext,PlayerActivity.class);
                intent.putExtra("index",position);
                musicContext.startActivity(intent);
            }
        });

    }

    //getiing Item Count from list
    @Override
    public int getItemCount() {
        return musicFiles.size();
    }
    //Updating Songs in player
    void updateSongs(ArrayList<MusicFiles> updatedList){
        musicFiles=new ArrayList<>();
        musicFiles.addAll(updatedList);
        notifyDataSetChanged();
    }



}
