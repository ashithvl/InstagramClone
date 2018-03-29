package com.blueangles.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blueangles.instagramclone.Activities.Home.HomeActivity;
import com.blueangles.instagramclone.Activities.Profile.AccountSettingsActivity;
import com.blueangles.instagramclone.Models.Photo;
import com.blueangles.instagramclone.Models.User;
import com.blueangles.instagramclone.Models.UserAccountSetting;
import com.blueangles.instagramclone.Models.UserSetting;
import com.blueangles.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Ashith VL on 10/14/2017.
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // Write a message to the database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private double mPhotoUploadProgress = 0;

    private String mUserId;
    private Context mContext;

    public FirebaseMethods(Context mContext) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mStorageReference = FirebaseStorage.getInstance().getReference();

        this.mContext = mContext;

        if (mAuth.getCurrentUser() != null) {
            mUserId = mAuth.getCurrentUser().getUid();
        }
    }

    public void registerUsingEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            mUserId = mAuth.getCurrentUser().getUid();
                            sendVerificationEmail();
                        }

                        // ...
                    }
                });
    }


    public boolean signUsingFirebaseEmail(String email, String password) {
        final boolean[] authResult = new boolean[1];
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:failed", task.getException());
                            authResult[0] = false;
                        } else {
                            Log.d(TAG, "onComplete: Login Successful");
                            authResult[0] = true;
                            try {
                                if (mFirebaseUser.isEmailVerified()) {
                                    Log.d(TAG, "onComplete: emailVerified");
                                    Intent homeIntent = new Intent(mContext, HomeActivity.class);
                                    mContext.startActivity(homeIntent);
                                } else {
                                    Toast.makeText(mContext, "Email not verified", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }

                            } catch (NullPointerException e) {
                                //null pointer
                            }
                        }
                    }
                });
        return authResult[0];
    }

    /**
     * checking username
     *
     * @param userName
     * @param mDataSnapshot
     * @return
     */
//    public boolean checkUserExists(String userName, DataSnapshot mDataSnapshot) {
//
//        Log.d(TAG, "checkUserExists: " + userName);
//
//        User mUser = new User();
//
//        for (DataSnapshot ds : mDataSnapshot.child(mUserId).getChildren()) {
//
//            mUser.setUser_name(ds.getValue(User.class).getUser_name());
//            return StringManipulation.expandUserName(mUser.getUser_name()).equals(userName);
//
//        }
//        return false;
//    }
    public void addNewUser(String userName, String email, String description, String website, String photo_url) {

        Log.d(TAG, "addNewUser: userName " + userName);

        User user = new User(email, 1, mUserId, StringManipulation.condenseUserName(userName));

        mDatabaseReference.child(mContext.getString(R.string.users))
                .child(mUserId)
                .setValue(user);

        UserAccountSetting userAccountSetting = new UserAccountSetting(
                description, userName, photo_url, StringManipulation.condenseUserName(userName), website, 0, 0, 0, mUserId);

        mDatabaseReference.child(mContext.getString(R.string.users_account_setting))
                .child(mUserId)
                .setValue(userAccountSetting);
    }

    public void sendVerificationEmail() {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null) {
            mFirebaseUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "Couldn't send Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * reriving accountsetting from firebase db
     *
     * @param mDataSnapshot
     * @return
     */

    public UserSetting getUserAccountSetting(DataSnapshot mDataSnapshot) {
        Log.e(TAG, "getUserAccountSetting: ");

        UserAccountSetting mUserAccountSetting = new UserAccountSetting();
        User mUser = new User();

        for (DataSnapshot d : mDataSnapshot.getChildren()) {

            if (d.getKey().equals(mContext.getResources().getString(R.string.users_account_setting))) {
                Log.e(TAG, "getUserAccountSetting: users_account_setting");
                try {
                    mUserAccountSetting
                            .setDisplay_name(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getDisplay_name());
                    mUserAccountSetting
                            .setUsername(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getUsername());
                    mUserAccountSetting
                            .setWebsite(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getWebsite());
                    mUserAccountSetting
                            .setDescription(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getDescription());
                    mUserAccountSetting
                            .setProfile_path(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getProfile_path());

                    mUserAccountSetting
                            .setPost(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getPost());
                    mUserAccountSetting
                            .setFollowers(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getFollowers());
                    mUserAccountSetting
                            .setFollowing(d
                                    .child(mUserId)
                                    .getValue(UserAccountSetting.class)
                                    .getFollowing());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSetting: " + e.getMessage());
                }
            } else if (d.getKey().equals(mContext.getResources().getString(R.string.users))) {
                try {
                    mUser.setUser_name(d.
                            child(mUserId)
                            .getValue(User.class)
                            .getUser_name());
                    mUser.setEmail(d.
                            child(mUserId)
                            .getValue(User.class)
                            .getEmail());
                    mUser.setPhone_number(d.
                            child(mUserId)
                            .getValue(User.class)
                            .getPhone_number());
                    mUser.setUser_id(d.
                            child(mUserId)
                            .getValue(User.class)
                            .getUser_id());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSetting: " + e.getMessage());
                }
            }

        }
        return new UserSetting(mUser, mUserAccountSetting);
    }

    public void updateUserName(String userName) {

        mDatabaseReference.child("users")
                .child(mUserId)
                .child("user_name")
                .setValue(userName);

        mDatabaseReference.child("user_account_setting")
                .child(mUserId)
                .child("username")
                .setValue(userName);
    }

    public void updateEmail(String email) {

        mDatabaseReference.child("users")
                .child(mUserId)
                .child("email")
                .setValue(email);

    }

    /**
     * Update 'user_account_settings' node for the current user
     *
     * @param displayName
     * @param website
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber) {

        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if (displayName != null) {
            mDatabaseReference.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(mUserId)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }


        if (website != null) {
            mDatabaseReference.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(mUserId)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if (description != null) {
            mDatabaseReference.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(mUserId)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if (phoneNumber != 0) {
            mDatabaseReference.child(mContext.getString(R.string.dbname_users))
                    .child(mUserId)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
    }


    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {
            count++;
        }
        return count;
    }


    public void uploadNewPhoto(final ProgressBar mProgressBar, String photoType, final String caption, final int count,
                               final String imgUrl, Bitmap bm) {
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        FilePaths filePaths = new FilePaths();
        //case1) new photo
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            if (bm == null) {
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node
                    addPhotoToDatabase(caption, firebaseUrl.toString());

                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();

                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }
        //case new profile photo
        else if (photoType.equals(mContext.getString(R.string.profile_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");

            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if (bm == null) {
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    setProfilePhoto(firebaseUrl.toString());

                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    ((AccountSettingsActivity) mContext).setupViewPager(
                            ((AccountSettingsActivity) mContext).mSectionsStatePagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();

                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }

    }

    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        mDatabaseReference.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = mDatabaseReference.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        mDatabaseReference.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);
        mDatabaseReference.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }


}
