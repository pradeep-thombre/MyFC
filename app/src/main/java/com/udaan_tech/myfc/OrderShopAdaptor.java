package com.udaan_tech.myfc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderShopAdaptor extends FirestoreRecyclerAdapter<OrderData,OrderShopAdaptor.ProductViewholder> {
    Handlename handlename;
    public OrderShopAdaptor(@NonNull FirestoreRecyclerOptions<OrderData> options,Handlename handlename) {
        super(options);
        this.handlename=handlename;
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderShopAdaptor.ProductViewholder holder, int position, @NonNull OrderData model) {
        holder.amount.setText("₹. "+Integer.toString(model.getAmount()));
        holder.Prodname.setText(model.getProdname());
        holder.nameid.setText(model.getCustname()+" ("+model.getCustid()+")");
        holder.orderid.setText("Order Id- "+Integer.toString(model.getOrdercounter()));


        holder.imglist=model.getProdimage().split(",");
        holder.namelist=model.getProdname().split(",");
        holder.size=holder.imglist.length-1;
        int c=0;
        while (c<=holder.size){
            holder.imageList.add(new SlideModel(holder.imglist[c]));
            holder.names=holder.names +Integer.toString(c+1)+"). "+holder.namelist[c]+"\n";
            c+=1;
        }
        holder.imageSlider.setImageList(holder.imageList,true);
        holder.imageSlider.startSliding(7000);

        holder.orderstatus.setVisibility(View.GONE);
        holder.delivered.setVisibility(View.GONE);

        if(model.getDeliverystatus()==true){
            holder.orderstatus.setVisibility(View.VISIBLE);
            holder.delivered.setVisibility(View.GONE);
        }else {
            holder.orderstatus.setVisibility(View.GONE);
            holder.delivered.setVisibility(View.VISIBLE);
        }

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-YYYY (HH:mm)");
        holder.time.setText(simpleDateFormat.format(model.getDate().toDate()));

        holder.delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map = new HashMap<>();
                map.put("deliverystatus",true);
                FirebaseFirestore.getInstance().collection("Orders").document(FirebaseAuth.getInstance().
                        getCurrentUser().getEmail()).collection("Successful").document(String.valueOf(model.getOrdercounter())).update(map);
                FirebaseFirestore.getInstance().collection("Orders").document(model.getCustphone()).collection("Successful").document(String.valueOf(model.getOrdercounter())).update(map);
            }
        });

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlename.prod_names("Purchased By- "+model.getCustname()+" ("+model.getCustid()+")\nMob- "+
                        model.getCustphone()+"\nDish Name- "+holder.names,model.getOrdercounter());
            }
        });
    }

    @NonNull
    @Override
    public OrderShopAdaptor.ProductViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_orders_rv,parent,false);
        return new ProductViewholder(view);
    }

    public class ProductViewholder extends RecyclerView.ViewHolder {
        TextView Prodname,nameid,orderstatus,orderid,amount,time,delivered;
        ImageSlider imageSlider;
        CardView cardview;
        List<SlideModel> imageList= new ArrayList<>();
        int size=0;
        String[] imglist;
        String[] namelist;
        String names="";
        public ProductViewholder(@NonNull View itemView) {
            super(itemView);
            Prodname=(itemView).findViewById(R.id.Prodname);
            nameid=(itemView).findViewById(R.id.nameid);
            orderstatus=(itemView).findViewById(R.id.status);
            orderid=(itemView).findViewById(R.id.orderid);
            amount=(itemView).findViewById(R.id.amount);
            time=(itemView).findViewById(R.id.date);
            delivered=(itemView).findViewById(R.id.delivered);
            imageSlider=(itemView).findViewById(R.id.image_slider);
            cardview=(itemView).findViewById(R.id.cardview);
        }
    }
    interface Handlename{
        public void  prod_names(String string,int integer);
    }
}
