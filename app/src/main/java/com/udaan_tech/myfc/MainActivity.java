package com.udaan_tech.myfc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    int AUTHUI_REQUEST_CODE = 10001;
    int AUTHUI_REQUEST_CODE1 = 10002;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth=FirebaseAuth.getInstance();
        getSupportActionBar().hide();
    }

    public void login(View view) {
            List<AuthUI.IdpConfig> provider = Arrays.asList(
                    new AuthUI.IdpConfig.PhoneBuilder().build()
            );
            Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(provider)
                    .setLogo(R.drawable.logoblack)
                    .setIsSmartLockEnabled(false)
                    .build();
            startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }

    public void shoplogin(View view) {
            List<AuthUI.IdpConfig> provider1 = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );
            Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(provider1)
                    .setLogo(R.drawable.logoblack)
                    .setIsSmartLockEnabled(false)
                    .setTosAndPrivacyPolicyUrls(
                            "https://example.com/terms.html",
                            "https://example.com/privacy.html")
                    .build();
            startActivityForResult(intent, AUTHUI_REQUEST_CODE1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==AUTHUI_REQUEST_CODE){
            if (resultCode== RESULT_OK){
                startActivity(new Intent(this, HomeActivity.class));
            }
        }
        else if (requestCode==AUTHUI_REQUEST_CODE1){
            if (resultCode== RESULT_OK){
                startActivity(new Intent(MainActivity.this,ShopHomeActivity.class));
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        try {
            if (mFirebaseAuth.getCurrentUser().getEmail().length()>5) {
                startActivity(new Intent(MainActivity.this, ShopHomeActivity.class));
                MainActivity.this.finish();
            } else if (mFirebaseAuth.getCurrentUser().getPhoneNumber() != null) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                MainActivity.this.finish();
            }
        }
        catch (Exception e){}
        FirebaseAuth.getInstance().getAccessToken(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainActivity.this,SplasherActivity.class));
    }
}