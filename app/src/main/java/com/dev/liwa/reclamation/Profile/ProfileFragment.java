package com.dev.liwa.reclamation.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.liwa.reclamation.Models.User;
import com.dev.liwa.reclamation.Models.UserAccountSettings;
import com.dev.liwa.reclamation.Models.UserSettings;
import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;
import com.dev.liwa.reclamation.Utils.FirebaseMethods;
import com.dev.liwa.reclamation.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 4;

        //firebase
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference myRef ;

    private TextView mPosts, mFollowers, mFollowing, mDsiplayName, mUserName, mWebSite, mDescription;
    private ProgressBar progressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationView bottomNavigationView;
    private Context mcontext;
    private FirebaseMethods mFirebaseMethods ;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mDsiplayName = (TextView) view.findViewById(R.id.display_name);
        mUserName = (TextView) view.findViewById(R.id.username);
        mWebSite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        progressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileImage);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        mcontext = getActivity();
        mFirebaseMethods = new FirebaseMethods(getActivity());
        TextView editProfile = (TextView) view.findViewById(R.id.textEditProfile);



        /**
         * setup buttom navigation view
         */


        ButtomNavigationHelper.enableNavigation(mcontext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);



       setupToolbar();
        setupFirebaseAuth();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"on click navigating to:");
                Intent intent = new Intent(getActivity(),AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.profile_activity));
                startActivity(intent);
            }
        });
        Log.d(TAG, "onCreateView : started ");
        return view;
    }

    private void SetProfileWidget (UserSettings userSettings){
        Log.d(TAG,"SetProfileWidget: setting with data retrievieng from firebase database"+userSettings.toString());
        Log.d(TAG,"SetProfileWidget: setting with data retrievieng from firebase database"+userSettings.getSettings().toString());
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mcontext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());


        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mDsiplayName.setText(settings.getDisplay_name());
        mUserName.setText(settings.getUsername());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        progressBar.setVisibility(View.GONE);
    }

 private void setupToolbar() {

        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

       profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, AccountSettingActivity.class);
                startActivity(intent);
            }
        });

    }


       /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieve user information from database

                SetProfileWidget (mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user in question
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}