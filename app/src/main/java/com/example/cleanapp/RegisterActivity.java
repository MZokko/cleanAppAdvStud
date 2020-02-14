package com.example.cleanapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cleanapp.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    protected EditText emailTxt,passwordTxt, editTextPhone, editTextFullName, editTextConfirmPass;
    protected Button btnLogin, btnRegister;
    public TextView txtValrAcc;
    protected RadioGroup userLvlradioGroup;
    protected RadioButton radioButtonTenant,radioButtonOwner;

    FirebaseAuth  myFirebaseAuth;
    UserModel myUser = new UserModel();
    DatabaseReference myDataBase= FirebaseDatabase.getInstance().getReference();
    DatabaseReference getUser = FirebaseDatabase.getInstance().getReference().child("User");
    DatabaseReference userLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Register");

        editTextFullName = (EditText) findViewById(R.id.editTextFullName);
        emailTxt = (EditText) findViewById(R.id.registerEmailEditText);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        passwordTxt = (EditText) findViewById(R.id.registerPasswordEditText);
        editTextConfirmPass  = (EditText) findViewById(R.id.registerConfirmPasswordEditText);

        editTextFullName.addTextChangedListener(mTextWatcher);
        emailTxt.addTextChangedListener(mTextWatcher);
        editTextPhone.addTextChangedListener(mTextWatcher);
        passwordTxt.addTextChangedListener(mTextWatcher);
        editTextConfirmPass.addTextChangedListener(mTextWatcher);

        myFirebaseAuth = FirebaseAuth.getInstance();
        btnRegister = findViewById(R.id.buttonRegister);
        txtValrAcc = findViewById(R.id.textViewAlrAcc);
        userLvlradioGroup = findViewById(R.id.radioGroup);
        radioButtonOwner = findViewById(R.id.radioButtonOwner);
        radioButtonTenant = findViewById(R.id.radioButtonTenant);

        checkIsEmpty();

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String mail = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                String confirmPass = editTextConfirmPass.getText().toString();

                if(password.equals(confirmPass)){
                    if (isValidEmail(mail) && password.length() > 7) {
                        myFirebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "sign up fail", Toast.LENGTH_SHORT).show();
                                } else {
                                    //get firebase auth user key
                                    String firebaseAuthUserId = getUserKeyFireAuth();
                                    Log.d("fireAuthKey : ", firebaseAuthUserId);

                                    Toast.makeText(RegisterActivity.this, firebaseAuthUserId, Toast.LENGTH_SHORT).show();
                                    createUserInDB();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.sendEmailVerification();
                                    getUser.child(myUser.getUserKey());

                                    /*getUser.child(myUser.getUserKey()).addChildEventListener(getUser);*/
                                    getUser.child(myUser.getUserKey()).child("userMail").setValue(myUser.getUserMail());
                                    getUser.child(myUser.getUserKey()).child("userPhone").setValue(myUser.getUserPhone());
                                    setUserLvl();
                                    getUser.child(myUser.getUserKey()).child("userLvl").setValue(myUser.getUserLvl());
                                    getUser.child(myUser.getUserKey()).child("userFullName").setValue(myUser.getUserFullName());

                                    Toast.makeText(RegisterActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    //getUserLevel();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this, "Password are not the same", Toast.LENGTH_SHORT).show();
                }

            }
        });

        txtValrAcc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkIsEmpty();
        }
    };

    void checkIsEmpty(){
        String fullname = editTextFullName.getText().toString();
        String email = emailTxt.getText().toString();
        String phone = editTextPhone.getText().toString();
        String password = passwordTxt.getText().toString();

        if(fullname.equals("") || email.equals("") || phone.equals("") || password.equals("")){
            btnRegister.setEnabled(false);
        }else{
            btnRegister.setEnabled(true);
        }

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

    public final static boolean isValidEmail(CharSequence target){
        if (target == null)
            return false;

        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validatePassword(final String password){
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.* [@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    protected void createUserInDB()
    {
        myUser.setUserMail(emailTxt.getText().toString());
        myUser.setUserPhone(editTextPhone.getText().toString());
        myUser.setUserKey(getUserKeyFireAuth());
        myUser.setUserFullName(editTextFullName.getText().toString());
    }

    protected void setUserLvl(){
        if(radioButtonOwner.isChecked()){
            myUser.setUserLvl(true);
        }else{
            myUser.setUserLvl(false);
        }
    }
}