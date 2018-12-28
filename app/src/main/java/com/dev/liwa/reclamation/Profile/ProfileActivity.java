package com.dev.liwa.reclamation.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.dev.liwa.reclamation.Profile.AccountSettingActivity;
import com.dev.liwa.reclamation.Home.MainActivity;
import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;
import com.dev.liwa.reclamation.Utils.GridImageAdapter;
import com.dev.liwa.reclamation.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG="ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    BottomNavigationView navigation;
    private ProgressBar progressBar;
    private Context mContext = ProfileActivity.this;
    private ImageView profilePhoto;
    private static final int NUM_GRID_COLUMNS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        navigation = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ButtomNavigationHelper.enableNavigation(ProfileActivity.this, navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);


        setupToolbar();
        setupActivityWidgets();
        setProfileImage();

        tempGridSetup();
       // navigation = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
     //   ButtomNavigationHelper.setupButtomNavigationView(navigation);
//        ButtomNavigationHelper.enableNavigation(ProfileActivity.this, navigation);
        //Menu menu = navigation.getMenu();
        //MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        //menuItem.setChecked(true);

    }




    private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();
        //imgURLs.add("https://pbs.twimg.com/profile_images/616076655547682816/6gMRtQyY.jpg");
        imgURLs.add("https://i.redd.it/9bf67ygj710z.jpg");
        imgURLs.add("https://c1.staticflickr.com/5/4276/34102458063_7be616b993_o.jpg");
        imgURLs.add("http://i.imgur.com/EwZRpvQ.jpg");
        imgURLs.add("http://i.imgur.com/JTb2pXP.jpg");
        imgURLs.add("https://i.redd.it/59kjlxxf720z.jpg");
        imgURLs.add("https://i.redd.it/pwduhknig00z.jpg");
        imgURLs.add("https://i.redd.it/clusqsm4oxzy.jpg");
        imgURLs.add("https://i.redd.it/svqvn7xs420z.jpg");
        imgURLs.add("http://i.imgur.com/j4AfH6P.jpg");
        imgURLs.add("https://i.redd.it/89cjkojkl10z.jpg");
        imgURLs.add("https://i.redd.it/aw7pv8jq4zzy.jpg");

        setupImageGrid(imgURLs);
    }

    private void setupImageGrid(ArrayList<String> imgURLs){
        GridView gridView = (GridView) findViewById(R.id.gridView);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
        gridView.setAdapter(adapter);
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile photo.");
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
        String imgURL = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IeLf";
        UniversalImageLoader.setImage(imgURL, profilePhoto, progressBar, "https://");
    }

    private void setupActivityWidgets(){
        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
    }


    private  void setupToolbar(){
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);

        ImageView imageView = (ImageView) findViewById(R.id.profileImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AccountSettingActivity.class);
                startActivity(intent);
            }
        });

    }


}
