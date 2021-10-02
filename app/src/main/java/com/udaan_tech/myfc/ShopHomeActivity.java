package com.udaan_tech.myfc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.squareup.picasso.Picasso;

public class ShopHomeActivity extends AppCompatActivity {
    TextView Shopname,shopby;
    CardView cvprofile,cvhome,cvorders,cvnewprod;
    ImageView shoplogo;
    String Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_home);
        getSupportActionBar().setTitle("Dashboard");

        Shopname=findViewById(R.id.Shopname);
        shopby=findViewById(R.id.shopby);
        shoplogo=findViewById(R.id.shoplogo);

        cvhome=findViewById(R.id.cv1);
        cvprofile=findViewById(R.id.cv2);
        cvorders=findViewById(R.id.cv3);
        cvnewprod=findViewById(R.id.cv4);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Email = user.getEmail();
            final SimpleArcDialog mDialog = new SimpleArcDialog(ShopHomeActivity.this);
            mDialog.setConfiguration(new ArcConfiguration(ShopHomeActivity.this));
            mDialog.show();

            FirebaseFirestore.getInstance().collection("FC").document(Email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("UPI") == null) {
                        startActivity(new Intent(ShopHomeActivity.this, ShopProfileEditActivity.class));
                        ShopHomeActivity.this.finish();
                    }
                    else {
                        shopby.setText(documentSnapshot.getString("Email").toUpperCase());
                        Picasso.get().load(documentSnapshot.getString("Logo")).into(shoplogo);
                        Shopname.setText(documentSnapshot.getString("Shopname").toUpperCase());
                    }
                    mDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                }
            });
        }else{
            startActivity(new Intent(ShopHomeActivity.this,MainActivity.class));
            ShopHomeActivity.this.finish();
        }
    }

    public void cvhome(View view) {
        startActivity(new Intent(ShopHomeActivity.this,ShopMainActivity.class));
    }

    public void cvprofile(View view) {
        startActivity(new Intent(ShopHomeActivity.this,ShopProfileActivity.class));
    }

    public void cvorder(View view) {
        startActivity(new Intent(ShopHomeActivity.this,OrderShopActivity.class));
    }

    public void cvnew(View view) {
        startActivity(new Intent(ShopHomeActivity.this,NewProdActivity.class));
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