package com.nicoqueijo.android.instagramclone.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nicoqueijo.android.instagramclone.R;
import com.nicoqueijo.android.instagramclone.utils.BottomNavigationViewHelper;
import com.nicoqueijo.android.instagramclone.utils.SectionsStatePagerAdapter;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = AccountSettingsActivity.class.getSimpleName();

    private static final int ACTIVITY_NUM = 4;

    private Context mContext;

    private SectionsStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        mViewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayout1);
        setupSettingsList();
        setupBottomNavigationView();
        setupFragments();
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupFragments() {
        mPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));
        mPagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));
    }

    private void setViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingsList() {
        ListView listView = findViewById(R.id.lvAccountSettings);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));
        options.add(getString(R.string.sign_out_fragment));
        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setViewPager(position);
            }
        });
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
