package com.nicoqueijo.android.instagramclone.share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nicoqueijo.android.instagramclone.R;
import com.nicoqueijo.android.instagramclone.utils.BottomNavigationViewHelper;
import com.nicoqueijo.android.instagramclone.utils.Permissions;

public class ShareActivity extends AppCompatActivity {

    private static final String TAG = ShareActivity.class.getSimpleName();
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: started");

        if (checkPermissionsArray(Permissions.PERMISSIONS)) {

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        //setupBottomNavigationView();
    }

    public void verifyPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(ShareActivity.this, permissions, VERIFY_PERMISSIONS_REQUEST);
    }

    public boolean checkPermissionsArray(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermission(check)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkPermission(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
