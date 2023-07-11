package com.example.storyapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.storyapp.databinding.ActivityCategoryAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {

    //view binding
    private ActivityCategoryAddBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();

        //configure progress dilog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle clic, begin upload
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private String category="";
    private void validateData() {
        //get data
        category=binding.categoryEt.getText().toString().trim();
        if(TextUtils.isEmpty(category)){
            Toast.makeText(this,"Please enter category...!",Toast.LENGTH_SHORT).show();
        }
        else{
            addCategoryFirebase();
        }

    }

    private void addCategoryFirebase() {
        //show progress
        progressDialog.setMessage("Adding category...");
        progressDialog.show();

        //get timestamp
        long timestamp=System.currentTimeMillis();

        //setup info to add in firebase db
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("timestamp",timestamp);
        hashMap.put("uid",""+firebaseAuth.getUid());

        //add to firebase db.... Database Rootv> Categories > categoryid > category info
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //category add success
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this,"Category added successfully...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //category add failed
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this,"Failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }
}