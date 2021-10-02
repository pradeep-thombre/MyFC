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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderCustAdaptor extends FirestoreRecyclerAdapter<OrderData,OrderCustAdaptor.ProductViewholder> {
    Handlename handlename;
    public OrderCustAdaptor(@NonNull FirestoreRecyclerOptions<OrderData> options,Handlename handlename) {
        super(options);
        this.handlename=handlename;
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderCustAdaptor.ProductViewholder holder, int position, @NonNull OrderData model) {
        holder.amount.setText("₹. "+Integer.toString(model.getAmount()));
        holder.Prodname.setText(model.getProdname());
        holder.shopname.setText("Purchased From "+model.getShopname());
        holder.orderid.setText("Order Id- "+Integer.toString(model.getOrdercounter()));
        //String formt= new SimpleDateFormat("dd-MM HH:mm:ss").format(model.getDate());
        //holder.time.setText(formt);
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

        if (model.getOrderstatus().contentEquals("success")){
            holder.orderstatus.setText("Ordered Successfully");
            if(model.getDeliverystatus()==true){
                holder.orderstatus.setText("Delivered Successfully");
                holder.orderstatus.setTextColor(R.color.black);
            }
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-YYYY (HH:mm)");
        holder.time.setText(simpleDateFormat.format(model.getDate().toDate()));

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlename.prod_names("Purchased From- "+model.getShopname()+"\nPrice- "+
                        Integer.toString(model.getAmount())+"\nDish Name- \n"+holder.names,model.getOrdercounter());
            }
        });
    }

    @NonNull
    @Override
    public OrderCustAdaptor.ProductViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_order_rv,parent,false);
        return new ProductViewholder(view);
    }

    public class ProductViewholder extends RecyclerView.ViewHolder {
        TextView Prodname,shopname,orderstatus,orderid,amount,time;
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
            shopname=(itemView).findViewById(R.id.shopname);
            orderstatus=(itemView).findViewById(R.id.orderstatus);
            orderid=(itemView).findViewById(R.id.orderid);
            amount=(itemView).findViewById(R.id.amount);
            time=(itemView).findViewById(R.id.time);
            cardview=(itemView).findViewById(R.id.cardview);
            imageSlider=(itemView).findViewById(R.id.image_slider);
        }
    }
    interface Handlename{
        public void prod_names(String string,int integer);
    }
}
