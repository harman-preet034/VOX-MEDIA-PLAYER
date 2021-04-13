package com.harman.voxmediaplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFrag} factory method to
 * create an instance of this fragment.
 */
public class ProfileFrag extends Fragment {

    public ProfileFrag() {
        // Required empty public constructor
    }

    GoogleSignInClient mGoogleSignInClient;
    TextView name,email;
    Button signOut;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.activity_profile, container, false);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        //checking if user is logged in
        if (acct != null) {
            //getting personal Data
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            //updating UI
            name=view.findViewById(R.id.profile_name);
            name.setText(personName);
            email=view.findViewById(R.id.email);
            email.setText(personEmail);
            ImageView imageView = (ImageView) view.findViewById(R.id.profile_img);
            Glide.with(this).load(String.valueOf(personPhoto)).into(imageView);
        }
        return view;
    }
}