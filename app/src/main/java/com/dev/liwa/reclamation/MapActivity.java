package com.dev.liwa.reclamation;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;

import java.util.Map;

public class MapActivity extends AppCompatActivity {

    BottomNavigationView navigation;
    private static final int ACTIVITY_NUM = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        navigation = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        ButtomNavigationHelper.setupButtomNavigationView(navigation);
        ButtomNavigationHelper.enableNavigation(MapActivity.this, navigation);

        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
