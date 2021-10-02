package com.udaan_tech.myfc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class OrderShopActivity extends AppCompatActivity implements OrderShopAdaptor.Handlename {
    RecyclerView recycler;
    OrderShopAdaptor cartAdapter;
    TextView searchtext;
    LinearLayout empty;
    String Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_shop);
        recycler=findViewById(R.id.recycler);
        empty = findViewById(R.id.empty);
        empty.setVisibility(View.GONE);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Email=user.getEmail();
            Query query = FirebaseFirestore.getInstance().collection("Orders").document(Email)
                    .collection("Successful")
                    .orderBy("ordercounter", Query.Direction.DESCENDING);
            adaptormodel(query);
            FirebaseFirestore.getInstance().collection("Orders").document(Email)
                    .collection("Successful").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else{
            startActivity(new Intent(OrderShopActivity.this,MainActivity.class));
            OrderShopActivity.this.finish();
        }
        searchtext=findViewById(R.id.searchtext);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.sortdata){
            showsortdialog();
            return true;
        }
        else if (id==R.id.home){
            startActivity(new Intent(OrderShopActivity.this,ShopMainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showsortdialog() {
        String[] sortoptions={"Delivered","Undelivered"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter List")
                .setIcon(R.drawable.ic_baseline_filter_list_24)
                .setItems(sortoptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            sortimplementation(true);
                        }
                        else if(which==1){
                            sortimplementation(false);
                        }
                    }
                });
        builder.show();
    }

    private void sortimplementation(boolean b) {
        empty.setVisibility(View.GONE);
        Query query = FirebaseFirestore.getInstance().collection("Orders").document(Email).collection("Successful")
                .orderBy("ordercounter", Query.Direction.DESCENDING).whereEqualTo("deliverystatus",b);
        adaptormodel(query);
        FirebaseFirestore.getInstance().collection("Orders").document(Email).collection("Successful")
                .whereEqualTo("deliverystatus",b).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void adaptormodel(Query query) {
        FirestoreRecyclerOptions<OrderData> options=new FirestoreRecyclerOptions.Builder<OrderData>()
                .setQuery(query,OrderData.class)
                .build();
        cartAdapter = new OrderShopAdaptor(options,this);
        recycler.setAdapter(cartAdapter);
        cartAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cartAdapter.stopListening();
        finish();
    }

    public void search(View view) {
        if  (TextUtils.isEmpty(searchtext.getText().toString())){
            searchtext.setError("Please enter Order Id");
            searchtext.requestFocus();
        }else {
            empty.setVisibility(View.GONE);
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null) {
                Query query = FirebaseFirestore.getInstance().collection("Orders").document(Email).collection("Successful")
                        .whereEqualTo("ordercounter", Integer.parseInt(searchtext.getText().toString()));
                adaptormodel(query);
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

    @Override
    public void prod_names(String string,int Int) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Integer.toString(Int)).setIcon(R.drawable.ic_baseline_filter_list_24).setMessage(string).setIcon(R.drawable.ic_baseline_info_24)
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}