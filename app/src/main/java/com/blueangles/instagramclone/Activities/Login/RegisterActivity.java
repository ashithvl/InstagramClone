package com.blueangles.instagramclone.Activities.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blueangles.instagramclone.Activities.Home.HomeActivity;
import com.blueangles.instagramclone.R;
import com.blueangles.instagramclone.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Ashith VL on 10/14/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext = RegisterActivity.this;

    private ProgressBar mProgressBar;
    private String email, password, username;
    private EditText mEmail, mPassword, mUserName;
    private TextView mPleaseWait;
    private AppCompatButton mRegisterButton;

    private String append = "";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;

    // Write a message to the database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mProgressBar = (ProgressBar) findViewById(R.id.progrssbar_register);
        mPleaseWait = (TextView) findViewById(R.id.please_wait);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mUserName = (EditText) findViewById(R.id.username);
        mRegisterButton = (AppCompatButton) findViewById(R.id.btn_register);

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        mFirebaseMethods = new FirebaseMethods(mContext);

        init();

        setupPrivateAuth();

    }

    /***********************************************FIREBASE SETUP STARTS****************************************/
    /**
     * Firebase setup
     */

    private void init() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUserName.getText().toString();

                if (!isNull(email) && !isNull(password) && !isNull(username)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mFirebaseMethods.registerUsingEmail(email, password);

                } else {
                    Toast.makeText(mContext, "You must set all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isNull(String s) {
        return s.equals("");
    }

    private void setupPrivateAuth() {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Log.d(TAG, "onDataChange: ");

                            //check username
                            checkIfUserExits(username);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
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

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.exists()) {
                        append = mDatabaseReference.push().getKey().substring(3 - 10);
                        Log.d(TAG, "onDataChange: username exits and appending random string " + append);
                    }

                    String mUserName = "";
                    mUserName = mUserName + append;

                    //add user to the database
                    mFirebaseMethods.addNewUser(mUserName, email, "", "", "");

                    Toast.makeText(mContext, "Sign Up successfull", Toast.LENGTH_SHORT).show();

                    mAuth.signOut();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkAuth(FirebaseUser mUser) {
        if (mUser != null) {
            Intent homeIntent = new Intent(mContext, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
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
}

