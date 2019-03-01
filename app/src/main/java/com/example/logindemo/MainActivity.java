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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
   // FirebaseApp.initializeApp(this);


    EditText Name;
    EditText password;
    TextView info;
    Button Login;
    TextView userRegistration;
    int counter = 5;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText) findViewById(R.id.etName);
        password = (EditText) findViewById(R.id.etPassword);
        info = (TextView) findViewById(R.id.tvInfo);
        info.setText("NO OF ATTEMPTS REMAINING : 5");
        userRegistration = (TextView) findViewById(R.id.tvRegister);
        Login = (Button) findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        forgetPassword = (TextView)findViewById(R.id.tvForgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PasswordActivity.class));
            }
        });


        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null) {
            finish();
            startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(),password.getText().toString());

            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
            }
        });

    }

    private void validate(String userName, String userPassword)
    {
        progressDialog.setMessage("HOLD ON , WE ARE ALMOST THERE!! ");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    checkEmailverification();
                }
                else {
                    counter--;
                    progressDialog.dismiss();  //removing progress dialoge

                    info.setText("NO OF ATTEMPTS REMAINING:" + counter);
                    Toast.makeText(MainActivity.this,"LOGIN FAILED",Toast.LENGTH_SHORT).show();
                    if(counter == 0)
                    {
                        Login.setEnabled(false);
                    }
                }
            }
        });
    }

    void checkEmailverification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();
        if(emailflag){
            finish();
            startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }
        else{
            Toast.makeText(MainActivity.this, "EMAIL NOT VERIFIED", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

        }
}
