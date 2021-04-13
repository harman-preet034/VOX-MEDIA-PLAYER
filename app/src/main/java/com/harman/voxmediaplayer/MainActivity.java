package com.harman.voxmediaplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.harman.voxmediaplayer.SongsFrag.musicAdapter;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int REQUEST_CODE=1;
    public static ArrayList<MusicFiles> myMusic;
    static boolean shuffle=false,repeat=false;
    private String Sorting="SortOrder";
    FrameLayout minPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checking for permissions
        permissions();


    }

    //Checking permissions method
    private void permissions() {
        //if permissions not granted ask for permissiions
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
        else{
            // Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            myMusic=getAllAudio(this);
            initViewPager();
        }

    }

    //Overriding on granting permission result method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
               // Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                myMusic=getAllAudio(this);
                initViewPager();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }

    //Initializing tabs
    private void initViewPager() {
        ViewPager viewPager=findViewById(R.id.viewpagerr);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPageAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragments(new SongsFrag(),"Songs");
        viewPageAdapter.addFragments(new ProfileFrag(),"My Profile");
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


//Creating View Pager Adapter Class to connect fragments and aadapters
    public static class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
    //adding fragments
        void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }

    //reteriving all audio files from the phone
    public ArrayList<MusicFiles> getAllAudio(Context context){
        SharedPreferences sp= getSharedPreferences(Sorting,MODE_PRIVATE);
        String sortingOrder=sp.getString("Sorting","Size");
        String order=null;
        //sorting files in Player
        switch (sortingOrder){
            case "Name":
                order=MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
                break;
            case "Date":
                order=MediaStore.MediaColumns.DATE_ADDED+" ASC";
                break;
            case "Size":
                order=MediaStore.MediaColumns.SIZE+" DESC";
                break;
        }
        //creating list of files
        ArrayList<MusicFiles>  tempAudio=new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //projection string
        String[] projection={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };
        //cursor to fetch data from phone
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,order);
   //iterating over cursor to get all the data
    if(cursor!=null){
        while (cursor.moveToNext()){
            String album=cursor.getString(0);
            String title=cursor.getString(1);
            String duration=cursor.getString(2);
            String artist=cursor.getString(3);
            String path=cursor.getString(4);
            //creating file object
            MusicFiles m=new MusicFiles(path,title,artist,album,duration);
            //Log.e("Path:"+path,"Album:"+album);
            tempAudio.add(m);
        }
        cursor.close();
    }
    return tempAudio;
    }

    //Creating Option MEnu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem mItem=menu.findItem(R.id.search_bar);
        SearchView searchView= (SearchView) mItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //searching songs on query input by user
    @Override
    public boolean onQueryTextChange(String newText) {
        String inp=newText.toLowerCase();
        ArrayList<MusicFiles> SearchFiles=new ArrayList<>();
        for(MusicFiles song:myMusic){
            if(song.getTitle().toLowerCase().contains(inp)){
                SearchFiles.add(song);
            }
        }
        musicAdapter.updateSongs(SearchFiles);
        return false;
    }
    //sorting starts here

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor edit=getSharedPreferences(Sorting,MODE_PRIVATE).edit();
        switch(item.getItemId()){
            case R.id.name:
                edit.putString("Sorting","Name");
                edit.apply();
                this.recreate();
                break;
            case R.id.date:
                edit.putString("Sorting","Date");
                edit.apply();
                this.recreate();
                break;
            case R.id.size:
                edit.putString("Sorting","Size");
                edit.apply();
                this.recreate();
                break;
            case R.id.about:
                Intent intent=new Intent(this,About.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}