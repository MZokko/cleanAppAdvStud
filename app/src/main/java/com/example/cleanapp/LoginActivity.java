package com.example.cleanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cleanapp.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    protected EditText emailTxt, passwordTxt;
    protected Button btnLogin, btnRegister;

    FirebaseAuth  myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthStateListener;

    DatabaseReference userLevel;
    UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        myFirebaseAuth = FirebaseAuth.getInstance();
        emailTxt = findViewById(R.id.emailEditText);
        passwordTxt = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        myFirebaseAuth = FirebaseAuth.getInstance();


        myAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser myFirebaseUser = myFirebaseAuth.getCurrentUser();
                if(myFirebaseUser != null)// weak no password check
                {
                    FirebaseUser user = myFirebaseAuth.getCurrentUser();
                    if(user.isEmailVerified()){
                        getUserLevel();
                    }else {
                        verifyDialog();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please, Login ",Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    String mail = emailTxt.getText().toString();
                    String password = passwordTxt.getText().toString();

                    if( mail.isEmpty() ) {
                        emailTxt.setError("please enter an emailTextField");
                        emailTxt.requestFocus();
                    } else if(password.isEmpty()) {
                        passwordTxt.setError("password missing");
                        passwordTxt.requestFocus();
                    } else if(mail.isEmpty() && password.isEmpty()) {
                        Toast.makeText(LoginActivity.this,"field are empty",Toast.LENGTH_SHORT).show();
                    } else if (!(mail.isEmpty() && password.isEmpty())) {
                        myFirebaseAuth.signInWithEmailAndPassword(mail,password)
                                .addOnCompleteListener(LoginActivity.this, new  OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Error",Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseUser user = myFirebaseAuth.getCurrentUser();
                                    if(user.isEmailVerified()){
                                        getUserLevel();
                                    }else{
                                        verifyDialog();
                                        Toast.makeText(LoginActivity.this, "Try login",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intoRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intoRegister);
            }
        });

    }

    protected String getUserKeyFireAuth(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fireAuthUserKey="";

        if (user != null) {
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            fireAuthUserKey = user.getUid();
        }

        return fireAuthUserKey;
    }

    protected void getUserLevel(){
        userLevel = FirebaseDatabase.getInstance().getReference().child("User").child(getUserKeyFireAuth()).child("userLvl");
        userLevel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean userlvl = dataSnapshot.getValue(Boolean.class);
                if(userlvl == true){
                    Toast.makeText(LoginActivity.this, userlvl.toString(),Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this,OwnerMainActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(LoginActivity.this, userlvl.toString(),Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, RoomDetailModal.TenantMainActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myFirebaseAuth.addAuthStateListener(myAuthStateListener);
    }

    void verifyDialog(){
        FirebaseUser user = myFirebaseAuth.getCurrentUser();
        FirebaseAuth.getInstance().signOut();

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Verify account")
                .setMessage("Your email is not verified yet. Please click on the button below to send a verification email")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.sendEmailVerification();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

}

