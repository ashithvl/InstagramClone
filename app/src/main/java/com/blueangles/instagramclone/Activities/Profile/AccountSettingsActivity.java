package com.blueangles.instagramclone.Activities.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.blueangles.instagramclone.R;
import com.blueangles.instagramclone.Utils.FirebaseMethods;
import com.blueangles.instagramclone.Utils.SectionsStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Ashith VL on 10/13/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private Context mContext = AccountSettingsActivity.this;
    public SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private static final int ACTIVITY_NUM = 4;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.main_relative);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        setupSettingsList();

        //bottomNavigationViewSetup();

        setupFragments();

        ImageView backArrowImageView = (ImageView) findViewById(R.id.back_arrow);
        backArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getIncomingIntent();

    }

    private void getIncomingIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.selected_image))) {
            if (intent.getStringExtra(getString(R.string.return_to_fragment))
                    .equals(getString(R.string.edit_profile_fragment))) {

                if (intent.hasExtra(getString(R.string.selected_image))) {
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                    firebaseMethods.uploadNewPhoto(mProgressBar, getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null);

                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                    firebaseMethods.uploadNewPhoto(mProgressBar, getString(R.string.profile_photo), null, 0,
                            null, (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }
            }
        }
        if (intent.hasExtra("call")) {
            setupViewPager(mSectionsStatePagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    private void setupFragments() {
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mSectionsStatePagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); //fragment 0
        mSectionsStatePagerAdapter.addFragment(new SignOutFragment(), "Sign Out"); //fragment 1
    }

    public void setupViewPager(Integer fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(mSectionsStatePagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingsList() {
        ListView listView = (ListView) findViewById(R.id.list_view_account_setting);

        ArrayList<String> options = new ArrayList<>();
        options.add("Edit Profile");
        options.add("Sign Out");

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                setupViewPager(position);
            }
        });

    }


}

