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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.dev.liwa.reclamation.AccountSettingActivity;
import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG="ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    BottomNavigationView navigation;
    private ProgressBar progressBar;
    private Context mContext = ProfileActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(View.GONE);
       // navigation = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
     //   ButtomNavigationHelper.setupButtomNavigationView(navigation);
//        ButtomNavigationHelper.enableNavigation(ProfileActivity.this, navigation);
        //Menu menu = navigation.getMenu();
        //MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        //menuItem.setChecked(true);
        setupToolbar();
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
