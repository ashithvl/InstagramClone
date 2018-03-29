package com.blueangles.instagramclone.Activities.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blueangles.instagramclone.Models.Comment;
import com.blueangles.instagramclone.Models.Like;
import com.blueangles.instagramclone.Models.Photo;
import com.blueangles.instagramclone.Models.UserAccountSetting;
import com.blueangles.instagramclone.Models.UserSetting;
import com.blueangles.instagramclone.R;
import com.blueangles.instagramclone.Utils.FirebaseMethods;
import com.blueangles.instagramclone.Utils.GridImageAdapter;
import com.blueangles.instagramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.blueangles.instagramclone.Utils.BottomNavigationHelper.enableNavigation;
import static com.blueangles.instagramclone.Utils.BottomNavigationHelper.setupBottomNavigationSetUp;

/**
 * Created by Ashith VL on 10/18/2017.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private TextView mPost, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfileImage;
    private GridView gridView;
    private Toolbar mToolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx mBottomNavigationViewEx;
    private static final int ACTIVITY_NUM = 4;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Write a message to the database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseMethods mFirebaseMethods;

    private Context mContext;

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int activityNumber);
    }

    OnGridImageSelectedListener mOnGridImageSelectedListener;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mPost = view.findViewById(R.id.textViewPost);
        mFollowers = view.findViewById(R.id.textViewFollowers);
        mFollowing = view.findViewById(R.id.textViewFollowing);
        mDisplayName = view.findViewById(R.id.display_name);
        mUsername = view.findViewById(R.id.profile_name);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);

        mProgressBar = view.findViewById(R.id.profile_progress_bar);
        mProfileImage = view.findViewById(R.id.profile_image);
        gridView = view.findViewById(R.id.grid_view);
        mToolbar = view.findViewById(R.id.profile_toolbar);
        profileMenu = view.findViewById(R.id.profile_menu);
        mBottomNavigationViewEx = view.findViewById(R.id.bottomNavigationView);

        mContext = getActivity();
        mFirebaseMethods = new FirebaseMethods(mContext);

        setupToolbar();

        bottomNavigationViewSetup();

        setupPrivateAuth();

        setupGridView();

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

        TextView editTextView = view.findViewById(R.id.textViewEditProfile);
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra("call", "edit_profile");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        return view;
    }

    private void setupGridView() {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }
                        photo.setLikes(likesList);
                        photos.add(photo);
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage());
                    }
                }

                //setup our image grid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth / NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<String>();
                for (int i = 0; i < photos.size(); i++) {
                    imgUrls.add(photos.get(i).getImage_path());
                }
                GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_image_view,
                        "", imgUrls);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }


    private void setUserWidget(UserSetting mUserWidget) {
        Log.e(TAG, "setUserWidget: ");

        // User mUser = mUserWidget.getUser();
        UserAccountSetting mUserAccountSetting = mUserWidget.getUserAccountSetting();

        UniversalImageLoader.setSingleImage(mUserAccountSetting.getProfile_path(), mProfileImage, null, "");

        mDisplayName.setText(mUserAccountSetting.getDisplay_name());
        mUsername.setText(mUserAccountSetting.getUsername());
        mWebsite.setText(mUserAccountSetting.getWebsite());
        mDescription.setText(mUserAccountSetting.getDescription());
        mPost.setText(String.valueOf(mUserAccountSetting.getPost()));
        mFollowers.setText(String.valueOf(mUserAccountSetting.getFollowers()));
        mFollowing.setText(String.valueOf(mUserAccountSetting.getFollowing()));
        mProgressBar.setVisibility(View.GONE);

    }


    private void bottomNavigationViewSetup() {
        setupBottomNavigationSetUp(mBottomNavigationViewEx);
        enableNavigation(mContext, getActivity(), mBottomNavigationViewEx);
        Menu menu = mBottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void setupToolbar() {

        ((ProfileActivity) getActivity()).setSupportActionBar(mToolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(settingIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }


    /***********************************************FIREBASE SETUP STARTS****************************************/
    /**
     * Firebase setup
     */

    private void setupPrivateAuth() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrive From firebase Db
                UserSetting mUserSetting = mFirebaseMethods.getUserAccountSetting(dataSnapshot);

                setUserWidget(mUserSetting);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }

}
