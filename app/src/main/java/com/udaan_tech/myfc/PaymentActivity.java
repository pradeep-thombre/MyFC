package com.udaan_tech.myfc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    ImageSlider imageSlider;
    List<SlideModel> imageList= new ArrayList<>();
    int size=0;
    String[] imglist;
    String[] namelist;

    TextView pricetv,payment,name;
    String upi="0",shopname,ordercounter="0";
    final int UPI_PAYMENT=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        imageSlider = findViewById(R.id.image_slider);
        pricetv = findViewById(R.id.pricetv);
        payment = findViewById(R.id.payment);
        name = findViewById(R.id.name);
        payment.setVisibility(View.GONE);

        Bundle mbundle = getIntent().getExtras();
        pricetv.setText(getIntent().getStringExtra("price"));
        getIntent().getStringExtra("price").replace("₹. ","");
        imglist=getIntent().getStringExtra("image").split(",");
        namelist=getIntent().getStringExtra("prodname").split(",");

        size=imglist.length-1;
        setSliderViews();
        imageSlider.setImageList(imageList,true);
        imageSlider.startSliding(10000);
        FirebaseDatabase.getInstance().getReference("Counters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot Item: snapshot.getChildren()){
                    ordercounter=Integer.toString(Integer.parseInt(Item.getValue().toString())+1);
                    checkbtn();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseFirestore.getInstance().collection("FC")
                .document(getIntent().getStringExtra("shop")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                upi=documentSnapshot.getString("UPI");
                shopname=documentSnapshot.getString("Shopname");
                checkbtn();
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectionAvailable(PaymentActivity.this)) {
                    PayUsingUpi(shopname, upi,
                            getIntent().getStringExtra("prodname"), getIntent().getStringExtra("price")
                                    .replace("₹. ",""));
                }
                else{
                    setContentView(R.layout.no_internet);
                    Thread thread = new Thread(){

                        public void run(){
                            try {
                                sleep(7000);

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            finally {
                                finish();
                            }
                        }
                    };thread.start();
                }
            }
        });
    }

    private void checkbtn() {
        if (ordercounter!="0" || upi!="0"){
            payment.setVisibility(View.VISIBLE);
        }
    }

    private void setSliderViews() {
        int c=0;
        String names="";
        while (c<=size){
            imageList.add(new SlideModel(imglist[c], namelist[c]));
            names=names +Integer.toString(c+1)+"). "+namelist[c]+"\n";
            c+=1;
            name.setText(names);
        }
    }
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
    private void PayUsingUpi(String shopname, String shopupi, String prodname, String amount) {
        Log.e("main","name- "+shopname+" Shop Upi "+shopupi+" Note- "+prodname+" Amount-- "+amount+" INR");
        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        //.appendQueryParameter("pa", shopupi)
                        .appendQueryParameter("pa", "8830055370@okbizaxis")
                        .appendQueryParameter("pn", shopname)
                        .appendQueryParameter("mc", "")
                        .appendQueryParameter("tr", ordercounter)
                        .appendQueryParameter("tn", prodname)
                        .appendQueryParameter("am", amount)
                        //.appendQueryParameter("am", "1")
                        .appendQueryParameter("cu", "INR")
                        //.appendQueryParameter("url", "your-transaction-url")
                        .build();
        Intent upipayintent = new Intent(Intent.ACTION_VIEW);
        upipayintent.setData(uri);
        Intent chooser=Intent.createChooser(upipayintent,"Pay With");

        if(null != chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser,UPI_PAYMENT);
        }
        else{
            Toast.makeText(this, "No UPI App Found, Please Install", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main","response "+resultCode);
        Log.e("main","response "+requestCode);
        switch (requestCode){
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data) {
        String str = data.get(0);
        Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
        String paymentCancel = "";
        if(str == null) str = "discard";
        String status = "Failed";
        String traxid="";
        String approvalRefNo = "";
        String response[] = str.split("&");
        for (int i = 0; i < response.length; i++) {
            String equalStr[] = response[i].split("=");
            if(equalStr.length >= 2) {
                if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                    status = equalStr[1].toLowerCase();
                }
                else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                    approvalRefNo = equalStr[1];
                }
                else if (equalStr[0].toLowerCase().equals("txnId".toLowerCase())) {
                    traxid = equalStr[1].toLowerCase();
                }
            }
            else {
                paymentCancel = "Payment cancelled by user.";
            }
        }

        FirebaseDatabase.getInstance().getReference("Counters").child("Order").setValue(Integer.parseInt(ordercounter));
        Timestamp timestamp =new Timestamp(new Date());
        Boolean  deliverystatus=false;
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        OrderData order = new OrderData(getIntent().getStringExtra("prodname"),
                getIntent().getStringExtra("image"),
                firebaseUser.getPhoneNumber(),
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("id"),
                getIntent().getStringExtra("shop"),
                upi, shopname,
                status, deliverystatus,
                Integer.parseInt(ordercounter),
                Integer.parseInt(getIntent().getStringExtra("price").replace("₹. ","")),
                timestamp);

        if (status.equals("success")) {
                FirebaseFirestore.getInstance().collection("Orders").document(firebaseUser.getPhoneNumber()).collection("Successful")
                        .document(ordercounter).set(order);
                FirebaseFirestore.getInstance().collection("Orders").document(getIntent().getStringExtra("shop")).collection("Successful")
                        .document(ordercounter).set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PaymentActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                        FirebaseFirestore.getInstance().collection("Cart").document(FirebaseAuth
                                .getInstance().getCurrentUser().getPhoneNumber()).delete();
                        emptycart();
                        startActivity(new Intent(PaymentActivity.this,OrdersActivity.class));
                        finish();
                    }
                });
        }
        else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(PaymentActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                FirebaseFirestore.getInstance().collection("Orders").document(firebaseUser.getPhoneNumber()).collection("Cancelled")
                        .document(ordercounter).set(order);
                finish();
        }
        else if (status.equals("submitted")) {
                FirebaseFirestore.getInstance().collection("Orders").document(firebaseUser.getPhoneNumber()).collection("submitted")
                        .document(ordercounter).set(order);
                FirebaseFirestore.getInstance().collection("Orders").document(getIntent().getStringExtra("shop")).collection("submitted")
                        .document(ordercounter).set(order);
        }
        else {
                FirebaseFirestore.getInstance().collection("Orders").document(firebaseUser.getPhoneNumber()).collection("Failed")
                        .document(ordercounter).set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PaymentActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void emptycart() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("Cart").document(user.getPhoneNumber()).collection("Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshots= queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot:snapshots){
                    FirebaseFirestore.getInstance().collection("Cart").document(user.getPhoneNumber())
                            .collection("Data").document(snapshot.getId()).delete();
                }
                Map<String,Object> map = new HashMap<>();
                map.put("FC","");
                FirebaseFirestore.getInstance().collection("Users")
                        .document(user.getPhoneNumber()).update(map);
            }
        });
    }
}