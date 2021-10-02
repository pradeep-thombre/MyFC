package com.udaan_tech.myfc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.HandleUpdate {
    RecyclerView recycler;
    CartAdapter cartAdapter;
    TextView proceedbtn,price,add;
    int cartprice=0,id=0;
    String images="",fcmail="",productname="",name="";
    LinearLayout empty,linearlayout;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setTitle(" ");
        //getSupportActionBar().hide();

        recycler=findViewById(R.id.recycler);
        price=findViewById(R.id.price);
        empty=findViewById(R.id.empty);
        linearlayout=findViewById(R.id.linearlayout);
        add=findViewById(R.id.add);
        proceedbtn=findViewById(R.id.proceedbtn);

        proceedbtn.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        add.setVisibility(View.GONE);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            phone = user.getPhoneNumber();
            check(phone);
        }else {
            startActivity(new Intent(CartActivity.this,MainActivity.class));
        }

        proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(CartActivity.this,PaymentActivity.class);
                intent.putExtra("image",images);
                intent.putExtra("shop",fcmail);
                intent.putExtra("prodname",productname);
                intent.putExtra("name",name);
                intent.putExtra("id",Integer.toString(id));
                intent.putExtra("price",price.getText().toString());
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,MainListActivity.class));
            }
        });
    }

    private void check(String phone) {
        Query query = FirebaseFirestore.getInstance().collection("Cart").document(phone).collection("Data");
        FirestoreRecyclerOptions<CartData> options=new FirestoreRecyclerOptions.Builder<CartData>()
                .setQuery(query,CartData.class)
                .build();

        cartAdapter = new CartAdapter(options,CartActivity.this);
        recycler.setAdapter(cartAdapter);
        cartAdapter.startListening();

        FirebaseFirestore.getInstance().collection("Users").document(phone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if((documentSnapshot.getLong("ID"))==0) {
                    startActivity(new Intent(CartActivity.this,UserProfileEditActivity.class));
                    CartActivity.this.finish();
                }
                else{
                    handleclick();
                }
            }
        });
    }

    @Override
    public void handleclick() {
        Query query = FirebaseFirestore.getInstance().collection("Cart").document(phone).collection("Data");
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            //@RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> snapshotList =queryDocumentSnapshots.getDocuments();
                cartprice=0;
                images="";
                fcmail="";
                productname="";
                int check=1;

                for (DocumentSnapshot snapshot:snapshotList){
                    if (check==1){
                        productname=snapshot.getString("productname")+" ("+snapshot.getLong("quantity")+")";
                        check+=1;
                    }else{
                        productname=productname+", "+snapshot.getString("productname")+" ("+snapshot.getLong("quantity")+")";
                    }
                    images=images+snapshot.getString("productimage")+",";
                    fcmail=snapshot.getString("shopemail");
                    cartprice=cartprice+(Integer.valueOf(String.valueOf(snapshot.getLong("productprice")))
                            *Integer.valueOf(String.valueOf(snapshot.getLong("quantity"))));
                }
                price.setText("₹. "+Integer.toString(cartprice));
                if (queryDocumentSnapshots.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    linearlayout.setVisibility(View.GONE);
                    Map<String,Object> map = new HashMap<>();
                    map.put("FC","");
                    FirebaseFirestore.getInstance().collection("Users")
                            .document(phone).update(map);
                }
                FirebaseFirestore.getInstance().collection("Users").document(phone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name=documentSnapshot.getString("Name");
                        id=Integer.parseInt(String.valueOf(documentSnapshot.getLong("ID")));
                        if (!( (name.contentEquals("")) ||(id==0) )) {
                            proceedbtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}



