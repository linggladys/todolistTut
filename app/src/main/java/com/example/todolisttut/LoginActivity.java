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
//import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mloginEmail,mloginPassword;
    private Button mloginBtn;
    private TextView mloginQn;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;


    String[] quotes = new String[]{'"' + "Jeoneun junggug salam - NCT's WinWin" + '"'
            ,'"' + "Jimin, you got no jams. - BTS' RM" + '"'
            ,'"' + "Infires - BTS' Suga" + '"'
            };
    int randomElementIndex = (int) (Math.random()*10)%3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mToolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("This is login");

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        mloginEmail = findViewById(R.id.loginEmail);
        mloginPassword = findViewById(R.id.loginPassword);
        mloginBtn = findViewById(R.id.loginBtn);
        mloginQn = findViewById(R.id.loginPageQuestion);
        //we can move to the regirstation page

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
        }

        //random quotes will popout
        TextView mtvRandomQuote = findViewById(R.id.tvRandomQuote);
        mtvRandomQuote.setText(quotes[randomElementIndex]);


        mloginQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mloginEmail.getText().toString().trim();
                String password = mloginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mloginEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mloginPassword.setError("Password is required");
                    return;
                }else{
                    //we want to perform login functionaility
                    loader.setMessage("Login in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();
                            }else{
                                String error = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Login failed" + error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}