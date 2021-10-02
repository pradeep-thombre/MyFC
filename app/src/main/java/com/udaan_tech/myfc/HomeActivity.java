package com.udaan_tech.myfc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    TextView Shopname,shopby;
    ImageView shoplogo;
    String Email,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Shopname=findViewById(R.id.Shopname);
        shopby=findViewById(R.id.shopby);
        shoplogo=findViewById(R.id.shoplogo);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){
            phone = user.getPhoneNumber();
            FirebaseFirestore.getInstance().collection("Users").document(phone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try {
                        if (documentSnapshot.getLong("ID") == 0) {
                            startActivity(new Intent(HomeActivity.this, UserProfileEditActivity.class));
                        } else {
                            Shopname.setText(documentSnapshot.getString("Name"));
                            shopby.setText(documentSnapshot.getString("Email"));
                        }
                    }catch (Exception e){
                        Map<String,Object> map = new HashMap<>();
                        map.put("Phone",phone);
                        map.put("ID",0);
                        map.put("FC","");
                        FirebaseFirestore.getInstance().collection("Users").document(phone).set(map);
                    }
                }
            });
        }
        else{
            startActivity(new Intent(HomeActivity.this,MainActivity.class));
        }

    }

    public void cvhome(View view) {
        startActivity(new Intent(HomeActivity.this,MainListActivity.class));
    }

    public void cvprofile(View view) {
        startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
    }

    public void cvorder(View view) {
        startActivity(new Intent(HomeActivity.this,OrdersActivity.class));
    }

    public void cvcart(View view) {
        startActivity(new Intent(HomeActivity.this,CartActivity.class));
    }

    public void share(View view) {
        String msg="Hey, We are happy to announce that we have Lounched 'MyFC' App, Now You can Purchase " +
                "anything from any Food Courts of across DC's India."+
                " \nDownload App Now https://play.google.com/store/apps/details?id=com.udaan_tech.myfc";
        //boolean installed=appInstalledOrNot("com.whatsapp");
        Intent intent= new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent,"Share Via"));
    }
}