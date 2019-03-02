package com.example.logindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationActivity extends AppCompatActivity {

    EditText userName, userPassword , userEmail,userAge;
    Button regButton;
    TextView userLogin;
    FirebaseAuth firebaseAuth;
    ImageView userProfilePic;
    String name,password,email,age;
    ProgressDialog progressDialogReg;
    StorageReference storageReference;

    FirebaseStorage firebaseStorage;

    ////////LOADING IMAGE FROM GALARY
     static int PICK_IMAGE = 123;
    Uri imagePath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
                imagePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    userProfilePic.setImageBitmap(bitmap);
                }catch(Exception e){
                    e.printStackTrace();
                }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //LOADING IMAGE FROM GALARY CONTINUED AT LOADINGIMAGE PART2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
       // StorageReference myRef1 = storageReference.child(firebaseAuth.getUid()).getRoot();

        ////////////LOADINGIIMAGE2 LOADING IMAGE FROM GALARY
        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");//image IS FOR IMAGE, WE CAN USE APPLICATION/*,AUDIO/*
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"),PICK_IMAGE);
            }
        });
        //LOADING IMAGE DONE FROM GALARY DONE



        //WHEN FORM IS FILLED AND REGISTER BUTTON IS CLICKED
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();
                    progressDialogReg.setMessage("HOLD ON !! YOU ARE BEING REGISTERED");
                    progressDialogReg.show();
                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialogReg.dismiss();
                                Toast.makeText(RegistrationActivity.this,"Successful registration",Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else{
                                progressDialogReg.dismiss();
                                Toast.makeText(RegistrationActivity.this,"Registration falied",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //upload to database
                }
            }
        });
        //REGISTRATION BUTTON ENDS

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegistrationActivity.this,MainActivity.class));  //  a way to write intent

            }
        });


    }

    void setupUIViews(){
        userName =findViewById(R.id.etUserName);
        userPassword =  findViewById(R.id.etUserPassword);
        userEmail = findViewById(R.id.etUserEmail);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
        userAge = findViewById(R.id.etAge);
        userProfilePic = findViewById(R.id.ivProfile);
        progressDialogReg = new ProgressDialog(this);

     }

     boolean validate() {

        Boolean result = true;
         name = userName.getText().toString();
         password = userPassword.getText().toString();
         email = userEmail.getText().toString();
         age = userAge.getText().toString();

        if( (name.isEmpty()) || (password.isEmpty()) || (email.isEmpty()) || (age.isEmpty())  || (imagePath == null))  {
            result = false;
            Toast.makeText(this,"Enter all details properly",Toast.LENGTH_SHORT).show();
        }


        return result;
     }


    //THIS FUNCTION IS FOR SENDING VERIFIACTION EMAIL TO REGISTERED EMAIL ADDRESS ,EXECUTES ONLY WHEN SUCCESSFULLY REGISTERED
     void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
         if(firebaseUser != null)
         {
             firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()){
                         setUserData();
                         firebaseAuth.signOut();
                         Toast.makeText(RegistrationActivity.this,"Successfully registered,verification email sent",Toast.LENGTH_SHORT).show();
                         finish();
                         startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                     }
                     else{
                         Toast.makeText(RegistrationActivity.this,"Verification email not send",Toast.LENGTH_SHORT).show();
                     }
                 }
             });
         }
     }

     void setUserData(){
         FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
         DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
         //for image uplaod

         StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("ProfilePic");//IN DATABASE userid/images/ProfilePic
         UploadTask uploadTask = imageReference.putFile(imagePath);
         uploadTask.addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(RegistrationActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
             }
         }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 Toast.makeText(RegistrationActivity.this, "Upoad successful", Toast.LENGTH_SHORT).show();

             }
         });

         // image uploading part ends
          UserProfil userProfil = new UserProfil(age,email,name);
          databaseReference.setValue(userProfil);
     }
}
