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

public class OrdersActivity extends AppCompatActivity implements OrderCustAdaptor.Handlename {
    RecyclerView recycler;
    OrderCustAdaptor cartAdapter;
    TextView searchtext,mesg;
    LinearLayout empty;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recycler=findViewById(R.id.recycler);
        empty=findViewById(R.id.empty);
        mesg=findViewById(R.id.mesg);
        empty.setVisibility(View.GONE);
        searchtext=findViewById(R.id.searchtext);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            phone=user.getPhoneNumber();
            orderlist();
        }else{
            startActivity(new Intent(OrdersActivity.this,MainActivity.class));
            OrdersActivity.this.finish();
        }
    }

    private void orderlist() {
        Query query = FirebaseFirestore.getInstance().collection("Orders").document(phone)
                .collection("Successful").orderBy("ordercounter", Query.Direction.DESCENDING);
        adaptormodel(query);

        FirebaseFirestore.getInstance().collection("Orders").document(phone)
                .collection("Successful").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void adaptormodel(Query query) {
        FirestoreRecyclerOptions<OrderData> options=new FirestoreRecyclerOptions.Builder<OrderData>()
                .setQuery(query,OrderData.class)
                .build();
        cartAdapter = new OrderCustAdaptor(options,this);
        recycler.setAdapter(cartAdapter);
        cartAdapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        empty.setVisibility(View.GONE);
        if (id==R.id.sortdata){
            showsortdialog();
            return true;
        }
        else if (id==R.id.home){
            startActivity(new Intent(OrdersActivity.this,MainListActivity.class));
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
        Query query = FirebaseFirestore.getInstance().collection("Orders").document(phone).collection("Successful").whereEqualTo("deliverystatus",b);
        adaptormodel(query);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                    mesg.setText("Look's like No Matching results Found");
                }
            }
        });
    }

    public void searchbtn(View view) {
        empty.setVisibility(View.GONE);
        if  (TextUtils.isEmpty(searchtext.getText().toString())){
            searchtext.setError("Please enter Order Id");
            searchtext.requestFocus();
        }else {
            Query query = FirebaseFirestore.getInstance().collection("Orders").document(phone)
                    .collection("Successful").whereEqualTo("ordercounter", Integer.parseInt(searchtext.getText().toString()));
            adaptormodel(query);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        empty.setVisibility(View.VISIBLE);
                        mesg.setText("Look's like you No Matching results Found");
                    }
                }
            });
        }
    }

    @Override
    public void prod_names(String string, int integer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Id- "+Integer.toString(integer)).setIcon(R.drawable.ic_baseline_filter_list_24).setMessage(string).setIcon(R.drawable.ic_baseline_info_24)
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}