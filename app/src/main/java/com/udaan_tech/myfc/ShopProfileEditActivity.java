package com.udaan_tech.myfc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ShopProfileEditActivity extends AppCompatActivity {
    Spinner spinner;
    String []  city;
    EditText shopname,shopupi,ownername;
    ImageView logoimage,doc_image;
    String location;

    Uri uri1,uri2;
    private static  final int image_req=1;
    private static  final int image_req2=2;
    int check1=1,check2=1;
    String imageurl1="",imageurl2="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile_edit);

        shopname=findViewById(R.id.shopname);
        shopupi=findViewById(R.id.id);
        ownername=findViewById(R.id.ownername);
        logoimage=findViewById(R.id.logoimage);
        doc_image=findViewById(R.id.doc_image);

        logoimage.setVisibility(View.GONE);
        doc_image.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("FC")
                .document(user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ownername.setText(documentSnapshot.getString("OName"));

                if (documentSnapshot.getString("UPI")!=null) {
                    shopname.setText(documentSnapshot.getString("Shopname"));
                    shopupi.setText(documentSnapshot.getString("UPI"));
                    location=documentSnapshot.getString("Location");
                    Picasso.get().load(documentSnapshot.getString("Logo")).into(logoimage);
                    logoimage.setVisibility(View.VISIBLE);
                    imageurl1=documentSnapshot.getString("Logo");
                    imageurl2=documentSnapshot.getString("Doc");
                    Picasso.get().load(documentSnapshot.getString("Doc")).into(doc_image);
                    doc_image.setVisibility(View.VISIBLE);
                }
            }
        });

        spinner=findViewById(R.id.spinner);
        city=getResources().getStringArray(R.array.Select_Location);
        ArrayAdapter myadaptor =new ArrayAdapter(ShopProfileEditActivity.this,android.R.layout.simple_spinner_item,city);
        myadaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myadaptor);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location=city[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void sel_logo(View view) {
        Intent photopicker = new Intent(Intent.ACTION_PICK);
        photopicker.setType("image/*");
        startActivityForResult(photopicker,image_req);
    }

    public void sel_doc(View view) {
        Intent photopicker = new Intent(Intent.ACTION_PICK);
        photopicker.setType("image/*");
        startActivityForResult(photopicker,image_req2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_req){
            if ((resultCode == RESULT_OK) &&  (data !=null)){
                uri1 = data.getData();
                logoimage.setImageURI(uri1);
                logoimage.setVisibility(View.VISIBLE);
                check1=2;
            }
            else {
                Toast.makeText(this, "Please Select Logo", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == image_req2){
            if ((resultCode == RESULT_OK) &&  (data !=null)){
                uri2 = data.getData();
                doc_image.setImageURI(uri2);
                doc_image.setVisibility(View.VISIBLE);
                check2=2;
            }
            else {
                Toast.makeText(this, "Please Select Document", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void update(View view) {
        if  (TextUtils.isEmpty(shopname.getText().toString())){
            shopname.setError("Please enter Shop Name");
            shopname.requestFocus();
        }
        else if  (TextUtils.isEmpty(ownername.getText().toString())){
            ownername.setError("Please enter Shop Owner Name");
            ownername.requestFocus();
        }
        else if  (TextUtils.isEmpty(shopupi.getText().toString())){
            shopupi.setError("Please enter Shop UPI");
            shopupi.requestFocus();
        }
        else if(location.contentEquals("Select Location")){
            Toast.makeText(this, "Please Select Location", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
        }
        else if(imageurl1.contentEquals("") && uri1==null){
            Toast.makeText(this, "Please Select Shop Image or ShopLogo", Toast.LENGTH_SHORT).show();
        }
        else if(imageurl2.contentEquals("") && uri2==null){
            Toast.makeText(this, "Please Select Verificaion Document", Toast.LENGTH_SHORT).show();
        }
        else {
            uploadlogo();
        }
    }

    private void uploadlogo() {
        if (check1==2) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Shop Logo").child(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            final SimpleArcDialog progressDialog = new SimpleArcDialog(ShopProfileEditActivity.this);
            progressDialog.setConfiguration(new ArcConfiguration(ShopProfileEditActivity.this));
            progressDialog.show();
            storageReference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    imageurl1 = urlImage.toString();
                    uploaddoc();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });
        }
        else{
            uploaddoc();
        }
    }

    private void uploaddoc() {
        if (check2==2) {
            String mycurrentDateTime = DateFormat.getDateTimeInstance()
                    .format(Calendar.getInstance().getTime());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Shop Doc").child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).child(mycurrentDateTime);
            final SimpleArcDialog progressDialog = new SimpleArcDialog(ShopProfileEditActivity.this);
            progressDialog.setConfiguration(new ArcConfiguration(ShopProfileEditActivity.this));
            progressDialog.show();
            storageReference.putFile(uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    imageurl2 = urlImage.toString();
                    uploaddata();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });
        }else{
            uploaddata();
        }
    }

    private void uploaddata() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String,Object> map = new HashMap<>();
        map.put("Email",user.getEmail());
        map.put("OName",ownername.getText().toString());
        map.put("Logo",imageurl1);
        map.put("Doc",imageurl2);
        map.put("Location",location);
        map.put("UPI",shopupi.getText().toString());
        map.put("Shopname",shopname.getText().toString());
        map.put("status",false);
        FirebaseFirestore.getInstance().collection("FC").document(user.getEmail()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(ShopProfileEditActivity.this,ShopHomeActivity.class));
                finish();
                Toast.makeText(ShopProfileEditActivity.this, "Shop Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShopProfileEditActivity.this, "Some Error Occured"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}