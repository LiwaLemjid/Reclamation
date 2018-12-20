package com.dev.liwa.reclamation.Profile;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG="ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG,"onMenuItemClick :clicked menu item"+item);
                switch (item.getItemId()){
                    case R.id.profileMenu :
                        Log.d(TAG,"onMenuItemClick : Navigation to profile preferences ");
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true ;
    }
}
