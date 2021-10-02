package com.udaan_tech.myfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

import hotchemi.android.rate.AppRate;

public class ProfileActivity extends AppCompatActivity {
    TextView logout,editprofile,name,email,location,id;
    String Phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name=findViewById(R.id.name);
        id=findViewById(R.id.id);
        email=findViewById(R.id.email);
        location=findViewById(R.id.location);

        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Phone = user.getPhoneNumber();
        }catch (Exception e){
            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        }


        final SimpleArcDialog mDialog = new SimpleArcDialog(ProfileActivity.this);
        mDialog.setConfiguration(new ArcConfiguration(ProfileActivity.this));
        mDialog.show();

        FirebaseFirestore.getInstance().collection("Users").document(Phone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getLong("ID") == 0) {
                    startActivity(new Intent(ProfileActivity.this,UserProfileEditActivity.class));
                    ProfileActivity.this.finish();
                }
                else{
                    name.setText(documentSnapshot.getString("Name"));
                    id.setText(String.valueOf(documentSnapshot.getLong("ID")));
                    email.setText(documentSnapshot.getString("Email"));
                    location.setText(documentSnapshot.getString("Location"));
                }
                mDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
            }
        });

        editprofile=findViewById(R.id.editprofile);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            }
        });
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,UserProfileEditActivity.class));
                ProfileActivity.this.finish();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profilemenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.home){
            startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void rateus(View view) {
        AppRate.with(ProfileActivity.this).setInstallDays(0).setLaunchTimes(1).setRemindInterval(1).monitor();
        AppRate.showRateDialogIfMeetsConditions(ProfileActivity.this);
    }
}