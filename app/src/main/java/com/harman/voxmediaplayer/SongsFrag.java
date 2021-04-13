package com.harman.voxmediaplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.harman.voxmediaplayer.MainActivity.myMusic;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFrag} factory method to
 * create an instance of this fragment.
 */
public class SongsFrag extends Fragment {

    RecyclerView recView;
   static MusicAdapter musicAdapter;
    public SongsFrag() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //creating song List
        View view=inflater.inflate(R.layout.fragment_songs, container, false);
        recView=view.findViewById(R.id.recycleId);
        recView.setHasFixedSize(true);
        if(myMusic.size() >1){
            musicAdapter=new MusicAdapter(getContext(),myMusic);
            recView.setAdapter(musicAdapter);
            recView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        }
        return view;
    }


}