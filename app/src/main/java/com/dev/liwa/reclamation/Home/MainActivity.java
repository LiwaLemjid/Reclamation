package com.dev.liwa.reclamation.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.dev.liwa.reclamation.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;

public class  MainActivity extends AppCompatActivity {
    private static final String  TAG="MainActivity";

    private static final int ACTIVITY_NUM = 0;
    BottomNavigationView navigation;
    private Context mContext = MainActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
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
        setupFirebaseAuth();
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ButtomNavigationHelper.enableNavigation(MainActivity.this, this, navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
        setupViewPager();
       // mAuth.signOut();
    }

    private void setupViewPager (){
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new MessageFragment());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container12);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById( R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_house);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_arrow);

    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

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
