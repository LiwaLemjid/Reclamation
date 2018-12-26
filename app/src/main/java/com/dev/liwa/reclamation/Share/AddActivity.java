package com.dev.liwa.reclamation.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.Permissions;
import com.dev.liwa.reclamation.Utils.SectionsPagerAdapter;

public class AddActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2;
    BottomNavigationView navigation;
    private static final String TAG = "AddActivity";

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private ViewPager mViewPager;


    private Context mContext = AddActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //navigation = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //ButtomNavigationHelper.setupButtomNavigationView(navigation);
        //ButtomNavigationHelper.enableNavigation(AddActivity.this, navigation);
        //Menu menu = navigation.getMenu();
        //MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        //menuItem.setChecked(true);

        if (checkPermissionsArray(Permissions.PERMISSIONS)) {

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        setupViewPager();
    }


    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager = (ViewPager) findViewById(R.id.container12);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));

    }

    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                AddActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }


    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(AddActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }
}
