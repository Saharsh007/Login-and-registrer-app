package com.example.logindemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Updateprofile extends AppCompatActivity {

    EditText newUserName,newUserEmail,newUserAge;
    Button save;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ImageView updateProilePic;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    ////////LOADING IMAGE FROM GALARY
    static int PICK_IMAGE = 123;

    Uri imagePath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                updateProilePic.setImageBitmap(bitmap);
            }catch(Exception e){
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //AFTER THIS IMAGE IS UPLOADED TO FIREBASE USING  #partToSetImage
   ///LOADING IMAGE FROM GALARY CONTINUED AT LOADINGIMAGE PART2




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);
        newUserAge = findViewById(R.id.tvAgeUpdate);
        newUserName = findViewById(R.id.tvNameUpdate);
        newUserEmail = findViewById(R.id.tvEmailUpdate);
        save = findViewById(R.id.btnEditProfile);
        updateProilePic = findViewById(R.id.ivProfilePicUpdate);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //CHANGING USER DETAILS////////////////////////////////////

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();




        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfil userProfil = dataSnapshot.getValue(UserProfil.class);
                //GETTING AND SETTING AT THE SAME TIME
                newUserAge.setText(userProfil.getAge());
                newUserName.setText(userProfil.getName());
                newUserEmail.setText(userProfil.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Updateprofile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

            }
        });


        /////////LOADING IMAGE FROM FIREBASE AND DISPLAYING DONE
        storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/ProfilePic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // profilePic.setImageURI(uri);             THIS WON'T WORK AS IT'S RETURNING A URL RATHER THAN A IMAGE
                Picasso.get().load(uri).fit().centerCrop().into(updateProilePic);//GET THIS FROM SQUARE PICASSO ,DON'T FORGET ITS DEPENDENCY

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Updateprofile.this, "CANNOT LOAD IMAGE", Toast.LENGTH_SHORT).show();
            }
        });

        /////////LOADING IMAGE FROM FIREBASE AND DISPLAYING DONE



        //TO SAVE CURRECT VALUE OF EDITTEXT OTHERWISE IT'LL BE DESTROYED ON ACCTIVITY FINSISH
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String name = newUserName.getText().toString();
                String age = newUserAge.getText().toString();
                String email = newUserEmail.getText().toString();
                UserProfil userProfil = new UserProfil(age,email,name);
                databaseReference.setValue(userProfil);

                ////////////////////////////////////////for image uplaod  #partToSetImage

                StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("ProfilePic");//IN DATABASE userid/images/ProfilePic
                UploadTask uploadTask = imageReference.putFile(imagePath);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Updateprofile.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Updateprofile.this, "Upoad successful", Toast.LENGTH_SHORT).show();

                    }
                });
                ///////////////////////////////////// image uploading part ends
                finish();
            }
        });
        //CHNAGING USER DETAILS DONE///////////////////////////////


        ////////////LOADINGIIMAGE2 LOADING IMAGE FROM GALARY
        updateProilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");//image IS FOR IMAGE, WE CAN USE APPLICATION/*,AUDIO/*
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"),PICK_IMAGE);     //calls onActivityResult
            }
        });
        //LOADING IMAGE DONE FROM GALARY DONE
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
