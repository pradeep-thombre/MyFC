package com.udaan_tech.myfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfileEditActivity extends AppCompatActivity {
    Spinner spinner;
    String []  city;
    String location;
    String Phone;
    TextView username,empid,email,update;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        username=findViewById(R.id.username);
        empid=findViewById(R.id.empid);
        email=findViewById(R.id.email);
        update=findViewById(R.id.payment);

        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            Phone = user.getPhoneNumber();
        }catch (Exception e){
            startActivity(new Intent(UserProfileEditActivity.this,MainActivity.class));
        }

        FirebaseFirestore.getInstance().collection("Users").document(Phone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if((documentSnapshot.getLong("ID"))!=0) {
                    empid.setText(String.valueOf(documentSnapshot.getLong("ID")));
                    location = documentSnapshot.getString("Location");
                    username.setText(documentSnapshot.getString("Name"));
                    email.setText(documentSnapshot.getString("Email"));
                }
            }
        });

        spinner=findViewById(R.id.spinner);
        city=getResources().getStringArray(R.array.Select_Location);
        ArrayAdapter myadaptor =new ArrayAdapter(UserProfileEditActivity.this,android.R.layout.simple_spinner_item,city);
        myadaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myadaptor);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(city[position].contentEquals("Select Location"))) {
                    location = city[position];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map = new HashMap<>();
                map.put("Phone",user.getPhoneNumber());
                map.put("ID",Integer.parseInt(empid.getText().toString()));
                map.put("Name",username.getText().toString());
                map.put("Email",email.getText().toString());
                map.put("Location",location);
                FirebaseFirestore.getInstance().collection("Users").document(user.getPhoneNumber()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(UserProfileEditActivity.this,ProfileActivity.class));
                        UserProfileEditActivity.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileEditActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}