package com.dev.liwa.reclamation;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;

public class AddActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2;
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        navigation = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ButtomNavigationHelper.setupButtomNavigationView(navigation);
        ButtomNavigationHelper.enableNavigation(AddActivity.this, navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
