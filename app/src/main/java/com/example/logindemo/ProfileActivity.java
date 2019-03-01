package com.example.logindemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView profileName,profileAge,profileEmail;
    Button profileUpdate;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilePic = findViewById(R.id.ivProfilePic);
        profileName = findViewById(R.id.tvProfileName);
        profileAge = findViewById(R.id.tvProfileAge);
        profileEmail = findViewById(R.id.tvProfileUsername);
        profileUpdate = findViewById(R.id.btnProfileUpdate);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        try {
            DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserProfil userProfil = dataSnapshot.getValue(UserProfil.class);
                    //GETTING AND SETTING AT THE SAME TIME
                    profileAge.setText("AGE:" + userProfil.getAge());
                    profileName.setText("NAME:" + userProfil.getName());
                    profileEmail.setText("USEREMAIL:" + userProfil.getEmail());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

                }
            });
        }catch(Exception e)
        {
            Log.e("FUCK",e.toString());
        }

    }
}
