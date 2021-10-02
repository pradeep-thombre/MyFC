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
import com.squareup.picasso.Picasso;

public class ShopListAdaptor extends FirestoreRecyclerAdapter<ShopData,ShopListAdaptor.ProductViewholder> {
    HandleUpdate handleUpdate;
    public ShopListAdaptor(@NonNull FirestoreRecyclerOptions<ShopData> options,HandleUpdate handleUpdate) {
        super(options);
        this.handleUpdate=handleUpdate;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductViewholder holder, int position, @NonNull ShopData model) {
        holder.sname.setText(model.getShopname().toUpperCase());
        Picasso.get().load(model.getLogo()).into(holder.shopimageView);

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUpdate.handleclick(model.getEmail());

            }
        });

    }

    @NonNull
    @Override
    public ProductViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoplist_rv,parent,false);
        return new ProductViewholder(view);
    }

    public class ProductViewholder extends RecyclerView.ViewHolder {
        ImageView shopimageView;
        TextView sname;
        CardView cardview;
        public ProductViewholder(@NonNull View itemView) {
            super(itemView);
            shopimageView=(itemView).findViewById(R.id.shopimageView);
            sname=(itemView).findViewById(R.id.sname);
            cardview=(itemView).findViewById(R.id.cardview);
        }
    }
    interface HandleUpdate{
        public void  handleclick(String string);
    }
}
