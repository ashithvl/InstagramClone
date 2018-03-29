package com.blueangles.instagramclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.blueangles.instagramclone.Activities.Home.HomeActivity;
import com.blueangles.instagramclone.Activities.Likes.LikesActivity;
import com.blueangles.instagramclone.Activities.Profile.ProfileActivity;
import com.blueangles.instagramclone.Activities.Search.SearchActivity;
import com.blueangles.instagramclone.Activities.Share.ShareActivity;
import com.blueangles.instagramclone.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Ashith VL on 10/13/2017.
 */

public class BottomNavigationHelper {

    public static void setupBottomNavigationSetUp(BottomNavigationViewEx bnve){
        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        bnve.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx bnve){
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.ic_house:
                        Intent homeIntent = new Intent(context, HomeActivity.class);
                        context.startActivity(homeIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_search:
                        Intent searchIntent = new Intent(context, SearchActivity.class);
                        context.startActivity(searchIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_circle:
                        Intent circleIntent = new Intent(context, ShareActivity.class);
                        context.startActivity(circleIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_alert:
                        Intent alertIntent = new Intent(context, LikesActivity.class);
                        context.startActivity(alertIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_android:
                        Intent androidIntent = new Intent(context, ProfileActivity.class);
                        context.startActivity(androidIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                }
                return false;
            }
        });
    }

}
