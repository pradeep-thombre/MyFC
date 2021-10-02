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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewProdActivity extends AppCompatActivity {
    Spinner spinner;
    String []  Cate;
    String Category,imageurl="";
    private static  final int image_req=1;
    Uri uri;
    ImageView Productimage;
    EditText prodname,price,dishnote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_prod);
        getSupportActionBar().setTitle("New Dish");

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            startActivity(new Intent(NewProdActivity.this,MainActivity.class));
            NewProdActivity.this.finish();
        }
        else {
            FirebaseFirestore.getInstance().collection("FC")
                    .document(user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("UPI") == null) {
                        startActivity(new Intent(NewProdActivity.this, ShopProfileEditActivity.class));
                        NewProdActivity.this.finish();
                    }
                }
            });
        }

        Productimage=findViewById(R.id.Productimage);
        Productimage.setVisibility(View.GONE);
        prodname=findViewById(R.id.Prodname);
        price=findViewById(R.id.price);
        dishnote=findViewById(R.id.dishnote);

        spinner=findViewById(R.id.spinner);
        Cate=getResources().getStringArray(R.array.Select_Category);
        ArrayAdapter myadaptor =new ArrayAdapter(NewProdActivity.this,android.R.layout.simple_spinner_item,Cate);
        myadaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myadaptor);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category=Cate[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void sel_img(View view) {
        Intent photopicker = new Intent(Intent.ACTION_PICK);
        photopicker.setType("image/*");
        startActivityForResult(photopicker,image_req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_req){
            if ((resultCode == RESULT_OK) &&  (data !=null)){
                uri = data.getData();
                Productimage.setImageURI(uri);
                Productimage.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(this, "Please Select Product Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addprod(View view) {
        if(uri==null){
            Toast.makeText(this, "Please Select Product Image", Toast.LENGTH_SHORT).show();
        }
        else if  (TextUtils.isEmpty(prodname.getText().toString())){
            prodname.setError("Please enter Product Name");
            prodname.requestFocus();
        }
        else if  (TextUtils.isEmpty(price.getText().toString())){
            price.setError("Please enter Product Price");
            price.requestFocus();
        }
        else if  (TextUtils.isEmpty(dishnote.getText().toString())){
            dishnote.setError("Please enter Some Description or Note");
            dishnote.requestFocus();
        }
        else if(Category.contentEquals("Select Category")){
            Toast.makeText(this, "Please Select Location", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
        }
        else {
            uploadimage();
        }
    }

    private void uploadimage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Product Images")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).child(prodname.getText().toString().toUpperCase());
        final SimpleArcDialog progressDialog = new SimpleArcDialog(NewProdActivity.this);
        progressDialog.setConfiguration(new ArcConfiguration(NewProdActivity.this));
        progressDialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageurl = urlImage.toString();
                uploaddata();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void uploaddata() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String mycurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());
        Timestamp timestamp =new Timestamp(new Date());
        ProductData newProdActivity = new ProductData(user.getEmail(),prodname.getText().toString().toUpperCase(),imageurl,
                dishnote.getText().toString(),Category,Integer.parseInt(price.getText().toString()),true,
                timestamp,prodname.getText().toString()+mycurrentDateTime);

        FirebaseFirestore.getInstance().collection("All Products").document(user.getEmail()).collection("Products")
                .document(prodname.getText().toString()+mycurrentDateTime).set(newProdActivity).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(NewProdActivity.this,ShopMainActivity.class));
                finish();
                Toast.makeText(NewProdActivity.this, "Product Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewProdActivity.this, "Some Error Occured"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}