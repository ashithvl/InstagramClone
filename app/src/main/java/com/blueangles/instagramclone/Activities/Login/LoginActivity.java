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

/**
 * Created by Ashith VL on 10/14/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;

    private Context mContext = LoginActivity.this;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;
    private AppCompatButton mLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = (ProgressBar) findViewById(R.id.progrssbar_login);
        mPleaseWait = (TextView) findViewById(R.id.please_wait);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginButton = (AppCompatButton) findViewById(R.id.btn_login);

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        mFirebaseMethods = new FirebaseMethods(mContext);

        setupPrivateAuth();

        init();

        setupRegisterClick();
    }

    private void setupRegisterClick() {
        TextView registerTextView = (TextView) findViewById(R.id.register);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    /***********************************************FIREBASE SETUP STARTS****************************************/
    /**
     * Firebase setup
     */

    private void init() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (!isNull(email) && !isNull(password)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    if (mFirebaseMethods.signUsingFirebaseEmail(email, password)) {
                        mPleaseWait.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(mContext, R.string.auth_Success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        mPleaseWait.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(mContext, "You must set all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         *
         * if user is logged in navigate to login
         *
         */
    }

    private boolean isNull(String s) {
        return s.equals("");
    }

    private void setupPrivateAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    checkAuth(user);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void checkAuth(FirebaseUser mFirebaseUser) {
        if (mFirebaseUser != null) {
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
