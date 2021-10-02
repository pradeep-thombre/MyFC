package com.udaan_tech.myfc;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import hotchemi.android.rate.AppRate;

public class ShopProfileActivity extends AppCompatActivity {
    TextView Shopname,shopby,shoplocation,shopupi;
    ImageView shoplogo;
    String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile);
        getSupportActionBar().setTitle("Profile");

        Shopname = findViewById(R.id.shopname);
        shopby = findViewById(R.id.shopowner);
        shopupi = findViewById(R.id.id);
        shoplocation = findViewById(R.id.shoplocation);
        shoplogo = findViewById(R.id.profile_logo);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Email = user.getEmail();
            FirebaseFirestore.getInstance().collection("FC").document(Email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("UPI") == null) {
                        startActivity(new Intent(ShopProfileActivity.this, ShopProfileEditActivity.class));
                        ShopProfileActivity.this.finish();
                    }
                    else {
                        Picasso.get().load(documentSnapshot.getString("Logo")).into(shoplogo);
                        shoplocation.setText(documentSnapshot.getString("Location"));
                        shopupi.setText(documentSnapshot.getString("UPI"));
                        Shopname.setText(documentSnapshot.getString("Shopname"));
                        shopby.setText(documentSnapshot.getString("OName"));
                    }
                }
            });
        }else{
            startActivity(new Intent(ShopProfileActivity.this,MainActivity.class));
        }

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ShopProfileActivity.this,SplasherActivity.class));
    }

    public void editprofile(View view) {
        startActivity(new Intent(ShopProfileActivity.this,ShopProfileEditActivity.class));
        ShopProfileActivity.this.finish();
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
            startActivity(new Intent(ShopProfileActivity.this,ShopHomeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void orders(View view) {
        startActivity(new Intent(ShopProfileActivity.this,OrderShopActivity.class));
    }

    public void rateus(View view) {
        AppRate.with(ShopProfileActivity.this).setInstallDays(0).setLaunchTimes(1).setRemindInterval(1).monitor();
        AppRate.showRateDialogIfMeetsConditions(ShopProfileActivity.this);
    }
}