package com.udaan_tech.myfc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListAdaptor extends FirestoreRecyclerAdapter<ProductData,ProductListAdaptor.ProductViewholder> {
    handledesc handledesc;
    public ProductListAdaptor(@NonNull FirestoreRecyclerOptions<ProductData> options,handledesc handledesc) {
        super(options);
        this.handledesc=handledesc;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductListAdaptor.ProductViewholder holder, int position, @NonNull ProductData model) {
        holder.prodnamerv.setText(model.getProductname().toUpperCase());
        holder.prodp.setText("₹. "+String.valueOf(model.getProductprice()));
        Picasso.get().load(model.getProductimage()).into(holder.imageView6);
        holder.cardView.setVisibility(View.GONE);
        holder.error.setVisibility(View.GONE);

        //Query query = FirebaseFirestore.getInstance().collection("Cart").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).collection("Data");
        Query query = FirebaseFirestore.getInstance().collection("Users");
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int cartprice=0;
                List<DocumentSnapshot> snapshotList =queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot:snapshotList){
                    if (snapshot.getString("Phone").contentEquals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                        if (model.getFcmail().contentEquals(snapshot.getString("FC")) || snapshot.getString("FC").contentEquals("")){
                            holder.check=1;
                        }
                    }

                }
            }
        });

        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.check == 1) {
                    holder.cardView.setVisibility(View.VISIBLE);
                    holder.cart.setVisibility(View.GONE);
                    int quantity = 1;
                    Map<String,Object> map = new HashMap<>();
                    map.put("FC",model.getFcmail());
                    FirebaseFirestore.getInstance().collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).update(map);
                    CartData cartData = new CartData(model.getFcmail(), model.getProductname(), model.getProductimage(), model.getKey(), model.getProductprice(), quantity);
                    FirebaseFirestore.getInstance().collection("Cart").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).collection("Data").document(model.getKey()).set(cartData);
                }
                else{
                    holder.error.setText("Unable to add Products from 2 Shops, Please Empty Cart First.");
                    holder.error.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cardView.setVisibility(View.GONE);
                holder.cart.setVisibility(View.VISIBLE);
                FirebaseFirestore.getInstance().collection("Cart").document(FirebaseAuth
                        .getInstance().getCurrentUser().getPhoneNumber()).collection("Data").document(model.getKey()).delete();
            }
        });
        holder.imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handledesc.prod_descr("Name- "+model.getProductname()+"\nDescription- " +
                        model.getDescription()+"\nPrice- ₹. "+String.valueOf(model.getProductprice())+" Only.");
            }
        });
    }

    @NonNull
    @Override
    public ProductListAdaptor.ProductViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlist_rv,parent,false);
        return new ProductViewholder(view);
    }

    public class ProductViewholder extends RecyclerView.ViewHolder {
        TextView prodnamerv,prodp,cart,error;
        ImageView imageView6;
        CardView cardView;
        int check=0;
        public ProductViewholder(@NonNull View itemView) {
            super(itemView);
            prodnamerv=(itemView).findViewById(R.id.prodnamerv);
            imageView6=(itemView).findViewById(R.id.imageView6);
            prodp=(itemView).findViewById(R.id.prodp);
            cardView=(itemView).findViewById(R.id.cv);
            cart=(itemView).findViewById(R.id.cart);
            error=(itemView).findViewById(R.id.error);
        }

    }
    interface handledesc{
        public void  prod_descr(String string);
    }
}
