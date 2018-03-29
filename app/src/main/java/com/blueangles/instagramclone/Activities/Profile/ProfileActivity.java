package com.blueangles.instagramclone.Activities.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blueangles.instagramclone.Utils.ViewCommentsFragment;
import com.blueangles.instagramclone.Utils.ViewPostFragment;
import com.blueangles.instagramclone.Models.Photo;
import com.blueangles.instagramclone.R;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener {

    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private ProgressBar mProgressBar;
    private ImageView profileImageView;
    private static final int NUMBER_OF_COLOMNS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();


    }

    private void init() {
        Log.e(TAG, "init: profile fragment");

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction mTransaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.container, profileFragment);
        mTransaction.addToBackStack("profile");
        mTransaction.commit();

    }

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.e(TAG, "onGridImageSelected: ");

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }


    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        Log.e(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }
}
