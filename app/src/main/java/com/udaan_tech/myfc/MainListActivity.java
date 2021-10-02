package com.udaan_tech.myfc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainListActivity extends AppCompatActivity implements ShopListAdaptor.HandleUpdate,ProductListAdaptor.handledesc {
    RecyclerView recycler,recycler1;
    ShopListAdaptor shopListAdaptor;
    ProductListAdaptor productListAdaptor;
    String location,shop,phone,shopemail;
    TextView loc,emptycart;
    LinearLayout empty,sad;
    LottieAnimationView linearprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        recycler=findViewById(R.id.recycler);
        recycler1=findViewById(R.id.recycler1);
        empty =findViewById(R.id.empty);
        sad =findViewById(R.id.sad);
        loc=findViewById(R.id.loc);
        emptycart=findViewById(R.id.emptycart);
        sad.setVisibility(View.GONE);
        emptycart.setVisibility(View.GONE);
        linearprogress=findViewById(R.id.linearprogress);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            phone=user.getPhoneNumber();
            FirebaseFirestore.getInstance().collection("Users").document(phone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if ((documentSnapshot.getLong("ID")) != 0) {
                        location = documentSnapshot.getString("Location");
                        shop = documentSnapshot.getString("FC");
                        loc.setText("Showing Results From " + location + " DC.");
                        prodlisting();
                        checkcart(phone);
                    } else {
                        startActivity(new Intent(MainListActivity.this, UserProfileEditActivity.class));
                        MainListActivity.this.finish();
                    }
                }
            });
        }else{
            startActivity(new Intent(MainListActivity.this,MainActivity.class));
        }
    }

    private void checkcart(String phone) {
        Query query = FirebaseFirestore.getInstance().collection("Cart").document(phone).collection("Data");
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!(queryDocumentSnapshots.isEmpty())){
                    emptycart.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void prodlisting() {
        Query query = FirebaseFirestore.getInstance()
                .collection("FC").orderBy("Shopname");
        //.whereEqualTo("Location",location).whereEqualTo("status",true);
        FirestoreRecyclerOptions<ShopData> options=new FirestoreRecyclerOptions.Builder<ShopData>()
                .setQuery(query,ShopData.class)
                .build();

        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(MainListActivity.this,RecyclerView.HORIZONTAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainListActivity.this, 2);
        shopListAdaptor = new ShopListAdaptor(options,MainListActivity.this);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(shopListAdaptor);
        shopListAdaptor.startListening();
        linearprogress.pauseAnimation();
        linearprogress.setVisibility(View.GONE);
    }


    @Override
    public void handleclick(String shop) {

        final SimpleArcDialog progressDialog = new SimpleArcDialog(MainListActivity.this);
        progressDialog.setConfiguration(new ArcConfiguration(MainListActivity.this));
        progressDialog.show();

        shopemail=shop;
        sad.setVisibility(View.GONE);
        Query query1 = FirebaseFirestore.getInstance()
                .collection("All Products").document(shop)
                .collection("Products").whereEqualTo("status",true);

        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    sad.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    recycler1.setVisibility(View.GONE);
                }
                else{
                    FirestoreRecyclerOptions<ProductData> options1=new FirestoreRecyclerOptions.Builder<ProductData>()
                            .setQuery(query1,ProductData.class)
                            .build();
                    GridLayoutManager gridLayoutManager1 = new GridLayoutManager(MainListActivity.this, 2);
                    productListAdaptor = new ProductListAdaptor(options1,MainListActivity.this);
                    recycler1.setLayoutManager(gridLayoutManager1);
                    recycler1.setAdapter(productListAdaptor);
                    productListAdaptor.startListening();
                    empty.setVisibility(View.GONE);
                    recycler1.setVisibility(View.VISIBLE);
                    checkcart(phone);
                }
                progressDialog.dismiss();
            }
        });

    }

    public void gotocart(View view) {
        startActivity(new Intent(this,CartActivity.class));
    }

    @Override
    public void prod_descr(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dish Info").setIcon(R.drawable.ic_baseline_filter_list_24).setMessage(string).setIcon(R.drawable.ic_baseline_info_24)
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void emptycart(View view) {
        final SimpleArcDialog progressDialog = new SimpleArcDialog(MainListActivity.this);
        progressDialog.setConfiguration(new ArcConfiguration(MainListActivity.this));
        progressDialog.show();

        FirebaseFirestore.getInstance().collection("Cart").document(phone).collection("Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshots= queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot:snapshots){
                    FirebaseFirestore.getInstance().collection("Cart").document(phone)
                            .collection("Data").document(snapshot.getId()).delete();
                }
                Map<String,Object> map = new HashMap<>();
                map.put("FC","");
                FirebaseFirestore.getInstance().collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        emptycart.setVisibility(View.GONE);
                        Toast.makeText(MainListActivity.this, "Cart Empty Now,\nStart adding Products", Toast.LENGTH_SHORT).show();
                        String mail=shopemail;
                        progressDialog.dismiss();
                        handleclick(mail);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}






