package com.dev.liwa.reclamation;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dev.liwa.reclamation.Profile.EditProfileFragment;
import com.dev.liwa.reclamation.Profile.SignOutFragment;
import com.dev.liwa.reclamation.Utils.SectionsStatePagerAdapter;

import java.util.ArrayList;

public class AccountSettingActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";

    private Context mContext;

    private SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        mContext = AccountSettingActivity.this;
        Log.d(TAG, "onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.container12);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);
        setupFragments();
        setupSettingList();




        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });
    }

    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), "Edit Profile"); //fragment 0
        pagerAdapter.addFragment(new SignOutFragment(), "Sign Out"); //fragment 1
    }

    private void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingList(){
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);
        ArrayList<String> options = new ArrayList<>();
        options.add("Edit Profile");
        options.add("Sign Out");
        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment#: " + position);
                setViewPager(position);
            }
        });
    }
}
