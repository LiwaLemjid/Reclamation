package com.dev.liwa.reclamation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;

public class MainActivity extends AppCompatActivity {


    private static final int ACTIVITY_NUM = 0;
    BottomNavigationView navigation;
/*
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ic_house:
                    Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                    getApplicationContext().startActivity(intent1);
                    return true;
                case R.id.ic_map:
                    Intent intent2 = new Intent(getApplicationContext(),MapActivity.class);
                    getApplicationContext().startActivity(intent2);
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.ic_circle:
                    Intent intent3 = new Intent(getApplicationContext(),AddActivity.class);
                    getApplicationContext().startActivity(intent3);
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;

                case R.id.ic_fav:
                    Intent intent4 = new Intent(getApplicationContext(),FavoritesActivity.class);
                    getApplicationContext().startActivity(intent4);
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;

                case R.id.ic_profile:
                    Intent intent5 = new Intent(getApplicationContext(),ProfileActivity.class);
                    getApplicationContext().startActivity(intent5);
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ButtomNavigationHelper.enableNavigation(MainActivity.this, navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}