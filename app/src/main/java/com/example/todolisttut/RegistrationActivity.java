package com.example.todolisttut;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity {


    private EditText mregEmail,mregPassword;
    private Button mregBtn;
    private TextView mregQn;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    //initialize the variable of Firebase

    String[] quotes = new String[]{'"' + "Jeoneun junggug salam - NCT's WinWin" + '"'
            ,'"' + "Jimin, you got no jams. - BTS' RM" + '"'
            ,'"' + "Infires - BTS' Suga" + '"'
    };
    int randomElementIndex = (int) (Math.random()*10)%3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        
        Toolbar mToolbar = findViewById(R.id.registrationToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("This is registration");


        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);


        mregEmail = findViewById(R.id.registrationEmail);
        mregPassword= findViewById(R.id.registrationPassword);
        mregBtn = findViewById(R.id.registrationButton);
        mregQn = findViewById(R.id.registrationPageQuestion);

        //random quotes
        TextView mtvRandomQuoteReg = findViewById(R.id.tvRandomQuoteReg);
        mtvRandomQuoteReg.setText(quotes[randomElementIndex]);


        mregQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //we want to confirm
        mregBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we can put string 1st
                String email = mregEmail.getText().toString().trim();
                String password = mregPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    //if email is empty, we want to edit text
                    mregEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    //if password is empty, we want to fill in password
                    mregPassword.setError("Password is required");
                    return;
                }else {
                    loader.setMessage("Registration in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Intent intent = new Intent (RegistrationActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();
                            }else{
                                String error = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this,"Registration failed" + error,Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }

                        }
                    });
                }



            }
        });
    }
}