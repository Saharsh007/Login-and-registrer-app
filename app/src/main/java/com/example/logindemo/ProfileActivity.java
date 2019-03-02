package com.example.logindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


//DON'T FORGET TO ADD DEPENDENCIES
public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView profileName,profileAge,profileEmail;
    Button profileUpdate,changepassword;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilePic = findViewById(R.id.ivPicUpdate);
        profileName = findViewById(R.id.tvNameUpdate);
        profileAge = findViewById(R.id.tvAgeUpdate);
        profileEmail = findViewById(R.id.tvEmailUpdate);
        profileUpdate = findViewById(R.id.btnEditProfile);
        changepassword = findViewById(R.id.btnChangePassword);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog.setMessage("LOADING INFO PLEASE WAIT!");
        progressDialog.show();

        ////RETRIEVING IMAGE FROM FIREBASE STORAGE
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage  = FirebaseStorage.getInstance();
        progressDialog.setMessage("IMAGE LOADING!");
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/ProfilePic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // profilePic.setImageURI(uri);             THIS WON'T WORK AS IT'S RETURNING A URL RATHER THAN A IMAGE
                Picasso.get().load(uri).fit().centerCrop().into(profilePic);//GET THIS FROM SQUARE PICASSO ,DON'T FORGET ITS DEPENDENCY
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "CANNOT LOAD IMAGE", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


        //TO START PROFILE CHANGE ACTIVITY
        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,Updateprofile.class));
            }
        });

        //RETRIVING FROM DATABASE
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

            // IDK WHY BUT THIS ACTIVITY WASN'T FINISHING WHEN USER SIGNS OUT AND WAS TRYING TO ACCESS DATABASE USING UID
            //SO I COMPARED CURRENT USER TO NULL AND IF CONDITION IS TRUE FINISHING THE ACTIVITY
            if(firebaseAuth.getCurrentUser() == null){
                finish();
            }
            //ERROR REMOVED

            DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserProfil userProfil = dataSnapshot.getValue(UserProfil.class);
                    //GETTING AND SETTING AT THE SAME TIME
                    profileAge.setText("AGE:" + userProfil.getAge());
                    profileName.setText("NAME:" + userProfil.getName());
                    profileEmail.setText("USEREMAIL:" + userProfil.getEmail());
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

                }
            });
        //RETRIVING FROM DATABASE DONE

        //PASSWORD CHANGE INTENT
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,UpdatePassword.class));
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
