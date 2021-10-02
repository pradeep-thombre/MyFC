package com.udaan_tech.myfc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ShopMainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, ShopProductAdaptor.statelistener{
    RecyclerView recyclerview;
    ShopProductAdaptor shopProductAdaptor;
    FirebaseAuth mFirebaseAuth;
    TextView greet,searchtext;
    String user;
    LinearLayout empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);
        getSupportActionBar().hide();

        mFirebaseAuth=FirebaseAuth.getInstance();
        recyclerview=findViewById(R.id.recyclerview);
        greet=findViewById(R.id.greet);
        searchtext=findViewById(R.id.searchtext);

        empty=findViewById(R.id.empty);
        empty.setVisibility(View.GONE);

        FirebaseUser users=FirebaseAuth.getInstance().getCurrentUser();
        if(users!=null){
            user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            FirebaseFirestore.getInstance().collection("FC")
                    .document(user).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("UPI") == null) {
                        startActivity(new Intent(ShopMainActivity.this, ShopProfileEditActivity.class));
                        ShopMainActivity.this.finish();
                    }
                    if (documentSnapshot.getString("Shopname") != null) {
                        greet.setText("Hii... "+documentSnapshot.getString("OName").toUpperCase()+" !");
                    }
                }
            });
        }else{
            startActivity(new Intent(ShopMainActivity.this,MainActivity.class));
            ShopMainActivity.this.finish();
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
        try {
            shopProductAdaptor.stopListening();
        }catch (Exception e){}
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        try {
            if (mFirebaseAuth.getCurrentUser()== null) {
                startActivity(new Intent(ShopMainActivity.this, MainActivity.class));
                ShopMainActivity.this.finish();
            }
            else {
                initRecyclerView(firebaseAuth.getCurrentUser());
            }
        }
        catch (Exception e){}
        FirebaseAuth.getInstance().getAccessToken(true);
    }
    private  void initRecyclerView(FirebaseUser user){
        if (user!=null) {
            FirebaseFirestore.getInstance().collection("All Products").document(user.getEmail())
                    .collection("Products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()){
                        Toast.makeText(ShopMainActivity.this, "No Products added\nPlease Add New Products !!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ShopMainActivity.this,NewProdActivity.class));
                    }
                    else{
                        Query query = FirebaseFirestore.getInstance()
                                .collection("All Products").document(user.getEmail()).collection("Products")
                                .orderBy("status", Query.Direction.DESCENDING);

                        adaptormodel(query);
                    }
                }
            });
        }
    }

    private void adaptormodel(Query query) {
        FirestoreRecyclerOptions<ProductData> options=new FirestoreRecyclerOptions.Builder<ProductData>()
                .setQuery(query,ProductData.class)
                .build();
        shopProductAdaptor = new ShopProductAdaptor(options,this);
        recyclerview.setAdapter(shopProductAdaptor);
        shopProductAdaptor.startListening();
    }

    @Override
    public void handlecheckchanged(boolean ischecked, DocumentSnapshot documentSnapshot) {
        documentSnapshot.getReference().update("status",ischecked);
    }

    public void addmoreproduct(View view) {
        startActivity(new Intent(ShopMainActivity.this,NewProdActivity.class));
    }

    public void searchbtn(View view) {

        if  (TextUtils.isEmpty(searchtext.getText().toString())){
            searchtext.setError("Please enter Product Name");
            searchtext.requestFocus();
        }else {
            empty.setVisibility(View.GONE);
            Query query = FirebaseFirestore.getInstance()
                    .collection("All Products").document(user).collection("Products")
                    .whereEqualTo("productname", searchtext.getText().toString().toUpperCase());
            adaptormodel(query);

            FirebaseFirestore.getInstance().collection("All Products").document(user)
                    .collection("Products").whereEqualTo("productname", searchtext.getText().toString().toUpperCase())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}