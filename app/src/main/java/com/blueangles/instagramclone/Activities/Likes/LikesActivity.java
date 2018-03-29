package com.blueangles.instagramclone.Activities.Likes;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.blueangles.instagramclone.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import static com.blueangles.instagramclone.Utils.BottomNavigationHelper.enableNavigation;
import static com.blueangles.instagramclone.Utils.BottomNavigationHelper.setupBottomNavigationSetUp;

public class LikesActivity extends AppCompatActivity {

    private static final String TAG = "LikesActivity";
    private Context mContext = LikesActivity.this;
    private static final int ACTIVITY_NUM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationViewSetup();
    }

    private void bottomNavigationViewSetup(){
        BottomNavigationViewEx bottomNavigationViewEx  = (BottomNavigationViewEx) findViewById(R.id.bottomNavigationView);
        setupBottomNavigationSetUp(bottomNavigationViewEx);
        enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
