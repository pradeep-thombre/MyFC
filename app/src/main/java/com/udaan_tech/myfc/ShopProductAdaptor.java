package com.udaan_tech.myfc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ShopProductAdaptor extends FirestoreRecyclerAdapter<ProductData,ShopProductAdaptor.ProductViewholder> {
    statelistener statelistener;

    public ShopProductAdaptor(@NonNull FirestoreRecyclerOptions<ProductData> options,statelistener statelistener) {
        super(options);
        this.statelistener=statelistener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductViewholder holder, int position, @NonNull ProductData model) {
        holder.productname.setText(model.getProductname().toUpperCase());
        holder.description.setText(model.getDescription());
        holder.productprice.setText("₹. "+Integer.toString(model.getProductprice()));
        Picasso.get().load(model.getProductimage()).into(holder.productimage);
        holder.checkBox.setChecked(model.getStatus());
    }

    @NonNull
    @Override
    public ProductViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_rv,parent,false);
        return new ProductViewholder(view);
    }

    class ProductViewholder extends RecyclerView.ViewHolder{
        TextView productprice,productname,description;
        ImageView productimage;
        CheckBox checkBox;
        CardView cardview;
        public ProductViewholder(@NonNull View itemView) {
            super(itemView);
            productname=itemView.findViewById(R.id.productname);
            productprice=itemView.findViewById(R.id.productprice);
            productimage=itemView.findViewById(R.id.productimage);
            checkBox=itemView.findViewById(R.id.checkBox);
            description=itemView.findViewById(R.id.description);
            cardview=itemView.findViewById(R.id.cardview);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DocumentSnapshot snapshot=getSnapshots().getSnapshot(getAdapterPosition());
                    statelistener.handlecheckchanged(isChecked,snapshot);
                }
            });

        }
    }
    interface statelistener{
        public void handlecheckchanged(boolean ischecked,DocumentSnapshot documentSnapshot);
    }
}
