package com.example.logindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class PasswordActivity extends AppCompatActivity {


    EditText passwordemail;
    Button resetpassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        passwordemail =  (EditText)findViewById(R.id.etPasswordEmail);
        resetpassword = (Button)findViewById(R.id.btnPasswordReset);
        progressDialog = new ProgressDialog(this);

        //MAIN CODE TO CHANGE PASSWORD
        firebaseAuth = FirebaseAuth.getInstance();
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernemail = passwordemail.getText().toString().trim();
                if(usernemail.equals("")){
                    Toast.makeText(PasswordActivity.this, "ENTER EMAILID", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("HOLD ON ,REQUEST PROCESSING");
                    progressDialog.show();
                    firebaseAuth.sendPasswordResetEmail(usernemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {



                            if(task.isSuccessful()) {
                                Toast.makeText(PasswordActivity.this, "PASSWORD CHANGE EMAIL SENT TO USER", Toast.LENGTH_SHORT).show();
                                finish();
                                progressDialog.dismiss();
                                startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(PasswordActivity.this,"ENTERED EMAIL ID NOT REGISTERED",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        //MAIN CODE ENDS
    }

}
