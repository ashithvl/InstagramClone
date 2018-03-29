package com.blueangles.instagramclone.Activities.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blueangles.instagramclone.Activities.Share.ShareActivity;
import com.blueangles.instagramclone.Activities.dialogs.ConfirmPasswordDialog;
import com.blueangles.instagramclone.Models.UserSetting;
import com.blueangles.instagramclone.R;
import com.blueangles.instagramclone.Utils.FirebaseMethods;
import com.blueangles.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ashith VL on 10/13/2017.
 */

public class EditProfileFragment extends Fragment implements
        ConfirmPasswordDialog.OnConfirmPasswordListener {

    private static final String TAG = "EditProfileFragment";
    private String mUserID;

    //edit profile fragment
    private CircleImageView profileImageView;
    private EditText displayName, userName, website, description, email, phonenumber;
    private TextView changeProfilePhoto;
    private Context mContext;
    private UserSetting mUserSetting;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Write a message to the database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseMethods mFirebaseMethods;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_edit_profile, container, false);

        Log.d(TAG, "" + R.layout.fragement_edit_profile);
        profileImageView = view.findViewById(R.id.profile_photo);
        displayName = view.findViewById(R.id.display_name);
        userName = view.findViewById(R.id.username);
        website = view.findViewById(R.id.website);
        description = view.findViewById(R.id.description);
        email = view.findViewById(R.id.email);
        phonenumber = view.findViewById(R.id.phone);
        changeProfilePhoto = view.findViewById(R.id.change_profile_photo);

        mContext = getActivity();

        // setupProfilePicture();
        mFirebaseMethods = new FirebaseMethods(mContext);
        setupPrivateAuth();

        ImageView backArrowImageView = view.findViewById(R.id.back_arrow);
        backArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        ImageView checkImageView = view.findViewById(R.id.save_changes);
        checkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileSetting();
            }
        });


        return view;
    }

    private void saveProfileSetting() {

        final String displayNameString = displayName.getText().toString();
        final String userNameString = userName.getText().toString();
        final String websiteString = website.getText().toString();
        final String descriptionString = description.getText().toString();
        final String emailString = email.getText().toString();
        final String phoneString = phonenumber.getText().toString();


        //no change in username
        if (!mUserSetting.getUserAccountSetting().getUsername().equals(userNameString)) {
            checkIfUserExits(userNameString);
        }
        //change in username
        else if (!mUserSetting.getUser().getEmail().equals(emailString)) {
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1);
        }

        /**
         * change the rest of the settings that do not require uniqueness
         */
        if (!mUserSetting.getUserAccountSetting().getDisplay_name().equals(displayNameString)) {
            //update displayname
            mFirebaseMethods.updateUserAccountSettings(displayNameString, null, null, 0);
        }
        if (!mUserSetting.getUserAccountSetting().getWebsite().equals(websiteString)) {
            //update website
            mFirebaseMethods.updateUserAccountSettings(null, websiteString, null, 0);
        }
        if (!mUserSetting.getUserAccountSetting().getDescription().equals(descriptionString)) {
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, descriptionString, 0);
        }
        if (!mUserSetting.getUserAccountSetting().getProfile_path().equals(phoneString)) {
            //update phoneNumber
            mFirebaseMethods.updateUserAccountSettings(null, null, null, Long.parseLong(phoneString));
        }
    }

    private void checkIfUserExits(final String userNameString) {

        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        Query query = mReference
                .child(getString(R.string.users))
                .orderByChild("user_name")
                .equalTo(userNameString);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    mFirebaseMethods.updateUserName(userNameString);
                    Toast.makeText(getActivity(), "Username Saved!!!", Toast.LENGTH_SHORT).show();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.exists()) {
                        Toast.makeText(getActivity(), "Username Already Exits!!!", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupProfilePicture() {
        String imageUri = "www.w3schools.com/w3images/fjords.jpg";
        UniversalImageLoader.setSingleImage(imageUri, profileImageView, null, "https://");
    }


    private void setUserWidget(UserSetting mUserWidget) {
        Log.e(TAG, "setUserWidget: ");

        mUserSetting = mUserWidget;

        UniversalImageLoader.setSingleImage(mUserSetting.getUserAccountSetting().getProfile_path(), profileImageView, null, "");

        displayName.setText(mUserSetting.getUserAccountSetting().getDisplay_name());
        userName.setText(mUserSetting.getUserAccountSetting().getUsername());
        website.setText(mUserSetting.getUserAccountSetting().getWebsite());
        description.setText(mUserSetting.getUserAccountSetting().getDescription());
        email.setText(mUserSetting.getUser().getEmail());
        phonenumber.setText(String.valueOf(mUserSetting.getUser().getPhone_number()));

        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//not zero num
                getActivity().startActivity(intent);
                getActivity().finish();
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

        mUserID = mAuth.getCurrentUser().getUid();

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
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        ///////////////////// Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");

                            ///////////////////////check to see if the email is not already present in the database
                            mAuth.fetchProvidersForEmail(email.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                            if (task.isSuccessful()) {
                                                try {
                                                    if (task.getResult().getProviders().size() == 1) {
                                                        Log.d(TAG, "onComplete: that email is already in use.");
                                                        Toast.makeText(getActivity(), "That email is already in use",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.d(TAG, "onComplete: That email is available.");

                                                        //////////////////////the email is available so update it
                                                        mAuth.getCurrentUser().updateEmail(email.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "User email address updated.");
                                                                            Toast.makeText(getActivity(), "email updated",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                            mFirebaseMethods.updateEmail(email.getText().toString());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } catch (NullPointerException e) {
                                                    Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                                }
                                            }
                                        }
                                    });

                        } else {
                            Log.e(TAG, "onComplete: re-authentication failed.");
                        }

                    }
                });
    }
}
