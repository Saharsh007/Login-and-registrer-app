package com.example.logindemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Updateprofile extends AppCompatActivity {

    EditText newUserName,newUserEmail,newUserAge;
    Button save;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);
        newUserAge = findViewById(R.id.tvAgeUpdate);
        newUserName = findViewById(R.id.tvNameUpdate);
        newUserEmail = findViewById(R.id.tvEmailUpdate);
        save = findViewById(R.id.btnEditProfile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //CHANGING USER DETAILS////////////////////////////////////

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();



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

        //TO SAVE CURRECT VALUE OF EDITTEXT OTHERWISE IT'LL BE DESTROYED ON ACCTIVITY FINSISH
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String name = newUserName.getText().toString();
                String age = newUserAge.getText().toString();
                String email = newUserEmail.getText().toString();
                UserProfil userProfil = new UserProfil(age,email,name);
                databaseReference.setValue(userProfil);
                finish();
            }
        });
        //CHNAGING USER DETAILS DONE///////////////////////////////

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
