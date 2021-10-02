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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CartAdapter extends FirestoreRecyclerAdapter<CartData,CartAdapter.ProductViewholder> {
    HandleUpdate handleUpdate;
    public CartAdapter(@NonNull FirestoreRecyclerOptions<CartData> options,HandleUpdate handleUpdate) {
        super(options);
        this.handleUpdate=handleUpdate;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartAdapter.ProductViewholder holder, int position, @NonNull CartData model) {
        holder.prodnamerv.setText(model.getProductname().toUpperCase());
        holder.prodp.setText("₹. "+String.valueOf(model.getProductprice()));
        holder.quantity.setText(String.valueOf(model.getQuantity()));
        Picasso.get().load(model.getProductimage()).into(holder.imageView6);

        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getQuantity()!=1) {
                    holder.quantity.setText(String.valueOf(model.getQuantity() - 1));
                    Map<String, Object> map = new HashMap<>();
                    map.put("quantity", model.getQuantity() - 1);
                    FirebaseFirestore.getInstance().collection("Cart").document(FirebaseAuth
                            .getInstance().getCurrentUser().getPhoneNumber()).collection("Data")
                            .document(model.getKey()).update(map);
                }
                else{
                    FirebaseFirestore.getInstance().collection("Cart").document(FirebaseAuth
                            .getInstance().getCurrentUser().getPhoneNumber()).collection("Data")
                            .document(model.getKey()).delete();
                }
                handleUpdate.handleclick();
            }
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.quantity.setText(String.valueOf(model.getQuantity()+1));
                Map<String,Object> map = new HashMap<>();
                map.put("quantity",model.getQuantity()+1);
                FirebaseFirestore.getInstance().collection("Cart").document(FirebaseAuth
                        .getInstance().getCurrentUser().getPhoneNumber()).collection("Data")
                        .document(model.getKey()).update(map);
                handleUpdate.handleclick();
            }
        });
    }

    @NonNull
    @Override
    public CartAdapter.ProductViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitems_rv,parent,false);
        return new ProductViewholder(view);
    }

    public class ProductViewholder extends RecyclerView.ViewHolder {
        TextView prodnamerv,prodp,quantity;
        ImageView imageView6,sub,add;
        CardView cardView;
        public ProductViewholder(@NonNull View itemView) {
            super(itemView);
            prodnamerv=(itemView).findViewById(R.id.prodnamerv);
            imageView6=(itemView).findViewById(R.id.imageView6);
            prodp=(itemView).findViewById(R.id.prodp);
            cardView=(itemView).findViewById(R.id.cv);
            sub=(itemView).findViewById(R.id.sub);
            add=(itemView).findViewById(R.id.add);
            quantity=(itemView).findViewById(R.id.quantity);
        }
    }
    interface HandleUpdate{
        public void  handleclick();
    }
}


